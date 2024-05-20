package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.component.ImageFolderCacheManager;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFileUsageDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.FolderNotEmptyException;
import com.imcode.imcms.storage.exception.ForbiddenDeleteStorageFileException;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.storage.exception.SuchStorageFileExistsException;
import imcode.server.ImcmsConstants;
import imcode.server.document.index.ImageFileIndex;
import imcode.util.image.Format;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

@Service
@Transactional
class DefaultImageFolderService implements ImageFolderService {

	private final BiFunction<StoragePath, Boolean, ImageFolderDTO> storagePathToImageFolderDTO;
    private final Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO;

    private final ImageFileService imageFileService;
	private final ImageService imageService;
    private final ImageFolderCacheManager imageFolderCacheManager;
    private final ImageFileIndex imageFileIndex;

    private final StorageClient storageClient;
    private final StoragePath storageImagesPath;
    private final StoragePath storageGeneratedImagesPath;

    DefaultImageFolderService(BiFunction<StoragePath, Boolean, ImageFolderDTO> storagePathToImageFolderDTO,
                              Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO,
                              ImageFileService imageFileService,
                              ImageService imageService,
                              ImageFolderCacheManager ImageFolderCacheManager,
                              @Qualifier("imageStorageClient") StorageClient storageClient,
                              @Value("${ImagePath}") String imagesPath,
                              @Lazy ImageFileIndex imageFileIndex) {
	    this.storagePathToImageFolderDTO = storagePathToImageFolderDTO;
        this.storagePathToImageFileDTO = storagePathToImageFileDTO;
        this.imageFileService = imageFileService;
	    this.imageService = imageService;
        this.imageFolderCacheManager = ImageFolderCacheManager;
        this.storageClient = storageClient;
        this.storageImagesPath = StoragePath.get(DIRECTORY, imagesPath);
	    this.imageFileIndex = imageFileIndex;
	    this.storageGeneratedImagesPath = storageImagesPath.resolve(DIRECTORY, ImcmsConstants.IMAGE_GENERATED_FOLDER);
    }

    @PostConstruct
    private void init(){
        if(!storageClient.exists(storageImagesPath)) storageClient.create(storageImagesPath);
        if(!storageClient.exists(storageGeneratedImagesPath)) storageClient.create(storageGeneratedImagesPath);
    }

    @Override
    public ImageFolderDTO getImageFolder() {
        return imageFolderCacheManager.getOrPut(storageImagesPath, () ->
                storagePathToImageFolderDTO.apply(storageImagesPath, true)
        );
    }

    @Override
    public void createImageFolder(ImageFolderDTO folderToCreate) {
        final String imageFolderRelativePath = folderToCreate.getPath();
        final StoragePath newFolderPath = storageImagesPath.resolve(DIRECTORY, imageFolderRelativePath);

        if (storageClient.exists(newFolderPath)) {
            throw new SuchStorageFileExistsException("Folder with path " + imageFolderRelativePath + " already exist");
        }

        storageClient.create(newFolderPath);

        imageFolderCacheManager.invalidate(newFolderPath.getParentPath(), storageImagesPath);
    }

    @Override
    public void renameFolder(ImageFolderDTO renameMe) {
        final String newName = renameMe.getName();
        final String imageFolderRelativePath = renameMe.getPath();
        final String newImageFolderRelativePath = imageFolderRelativePath.replaceAll("\\w+$", newName);

        final StoragePath folderPath = storageImagesPath.resolve(DIRECTORY, imageFolderRelativePath);
        final StoragePath newFolderPath = storageImagesPath.resolve(DIRECTORY, newImageFolderRelativePath);

        if (!storageClient.exists(folderPath)) {
            throw new StorageFileNotFoundException("Folder with path " + imageFolderRelativePath + " not exist!");
        }

        if (folderPath.equals(newFolderPath)) return;

        if (storageClient.exists(newFolderPath)) {
            throw new SuchStorageFileExistsException("Folder with path " + newImageFolderRelativePath + " already exist!");
        }

        String folderInUrl = storageImagesPath.relativize(folderPath).toString();
        String newFolderInUrl = storageImagesPath.relativize(newFolderPath).toString();
	    imageService.getImagesByFolderInUrl(folderInUrl).forEach(imageDTO -> {
		    imageDTO.setPath(StringUtils.replaceOnce(imageDTO.getPath(), folderInUrl, newFolderInUrl));
		    imageService.updateImage(imageDTO);
	    });

        storageClient.walk(folderPath).stream().filter(storagePath -> storagePath.getType().equals(SourceFile.FileType.FILE)).forEach(storagePath -> {
            imageFileIndex.removeImageFile(storageImagesPath.relativize(storagePath).toString());
        });

        storageClient.move(folderPath, newFolderPath);

        imageFolderCacheManager.invalidate(folderPath, folderPath.getParentPath(), storageImagesPath);
        storageClient.walk(newFolderPath).stream().filter(storagePath -> storagePath.getType().equals(SourceFile.FileType.FILE)).forEach(storagePath -> {
            imageFileIndex.indexImageFile(storageImagesPath.relativize(storagePath).toString());
        });
    }

    @Override
    public boolean canBeDeleted(ImageFolderDTO checkMe) {
        final String imageFolderRelativePath = checkMe.getPath();
        final StoragePath folderToDeletePath = storageImagesPath.resolve(DIRECTORY, imageFolderRelativePath);

        if (storageGeneratedImagesPath.equals(folderToDeletePath)) {
            throw new ForbiddenDeleteStorageFileException("Forbidden to delete folder for generated images");
        }

        if (storageClient.walk(folderToDeletePath).stream().anyMatch(storagePath -> storagePath.getType() == FILE)) {
            throw new FolderNotEmptyException("Folder with path " + imageFolderRelativePath + " not empty!");
        }

        return true;
    }

    @Override
    public void deleteFolder(ImageFolderDTO deleteMe) {
        if (canBeDeleted(deleteMe)) {
            final String imageFolderRelativePath = deleteMe.getPath();
            final StoragePath folderToDeletePath = storageImagesPath.resolve(DIRECTORY, imageFolderRelativePath);

            //not required index deletion because folder can be deleted only if is empty.
            // so to delete folder user needs to delete all nested files and in this situation index will be cleared
//            storageClient.walk(folderToDeletePath).stream().filter(storagePath -> storagePath.getType().equals(SourceFile.FileType.FILE)).forEach(storagePath -> {
//                imageFileIndex.removeImageFile(storageImagesPath.relativize(storagePath).toString());
//            });

            storageClient.delete(folderToDeletePath, true);

            imageFolderCacheManager.invalidate(folderToDeletePath, folderToDeletePath.getParentPath(), storageImagesPath);
        }
    }

    @Override
    public ImageFolderDTO getImagesFrom(ImageFolderDTO folderToGetImages) {
        final StoragePath folderPath = storageImagesPath.resolve(DIRECTORY, folderToGetImages.getPath());

        return imageFolderCacheManager.getOrPut(folderPath, () -> {
            if (folderPath.equals(storageImagesPath)) {
                return getImageFolder();
            } else {
                List<ImageFileDTO> folderFiles = storageClient.listPaths(folderPath).parallelStream()
                        .filter(filePath -> Format.isImage(FilenameUtils.getExtension(filePath.toString())))
                        .map(storagePathToImageFileDTO)
                        .collect(Collectors.toList());
                folderToGetImages.setFiles(folderFiles);
                return folderToGetImages;
            }
        });
    }

    @Override
    public List<ImageFolderItemUsageDTO> checkFolder(ImageFolderDTO folderToCheck) {
        List<ImageFolderItemUsageDTO> usages = new ArrayList<>();

        final ImageFolderDTO folderWithImages = getImagesFrom(folderToCheck);

        final List<ImageFileDTO> imagesToCheck = folderWithImages.getFiles();

        for (ImageFileDTO image : imagesToCheck) {
            List<ImageFileUsageDTO> imageUsages = imageFileService.getImageFileUsages(image.getPath());
            if (!imageUsages.isEmpty()) {
                usages.add(new ImageFolderItemUsageDTO(folderWithImages.getPath(), image.getName(), imageUsages));
            }
        }

        return usages;
    }

	@Override
	public boolean exists(ImageFolderDTO folderToCheck) {
        final String imageFolderRelativePath = folderToCheck.getPath();
        final StoragePath folderToCheckPath = storageImagesPath.resolve(DIRECTORY, imageFolderRelativePath);

        return storageClient.exists(folderToCheckPath);
    }

    private List<StoragePath> walkImageFolder(StoragePath storagePath){
        return storageClient.walk(storagePath);
    }
}
