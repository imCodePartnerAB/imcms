package com.imcode.imcms.domain.service.api;

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
import imcode.util.image.Format;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

@Service
@Transactional
class DefaultImageFolderService implements ImageFolderService {

	private final BiFunction<StoragePath, Boolean, ImageFolderDTO> storagePathToImageFolderDTO;
    private final Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO;

    private final ImageFileService imageFileService;
	private final ImageService imageService;

    private final StorageClient storageClient;
    private final StoragePath storageImagesPath;
    private final StoragePath storageGeneratedImagesPath;

    DefaultImageFolderService(BiFunction<StoragePath, Boolean, ImageFolderDTO> storagePathToImageFolderDTO,
                              Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO,
                              ImageFileService imageFileService, ImageService imageService,
                              @Qualifier("imageStorageClient") StorageClient storageClient, @Value("${ImagePath}") String imagesPath) {
	    this.storagePathToImageFolderDTO = storagePathToImageFolderDTO;
        this.storagePathToImageFileDTO = storagePathToImageFileDTO;
        this.imageFileService = imageFileService;
	    this.imageService = imageService;
        this.storageClient = storageClient;
        this.storageImagesPath = StoragePath.get(DIRECTORY, imagesPath);
        this.storageGeneratedImagesPath = storageImagesPath.resolve(DIRECTORY, ImcmsConstants.IMAGE_GENERATED_FOLDER);
    }

    @PostConstruct
    private void init(){
        if(!storageClient.exists(storageImagesPath)) storageClient.create(storageImagesPath);
        if(!storageClient.exists(storageGeneratedImagesPath)) storageClient.create(storageGeneratedImagesPath);
    }

    @Override
    public ImageFolderDTO getImageFolder() {
        return storagePathToImageFolderDTO.apply(storageImagesPath, true);
    }

    @Override
    public void createImageFolder(ImageFolderDTO folderToCreate) {
        final String imageFolderRelativePath = folderToCreate.getPath();
        final StoragePath newFolderPath = storageImagesPath.resolve(DIRECTORY, imageFolderRelativePath);

        if (storageClient.exists(newFolderPath)) {
            throw new SuchStorageFileExistsException("Folder with path " + imageFolderRelativePath + " already exist");
        }

        storageClient.create(newFolderPath);
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

        storageClient.move(folderPath, newFolderPath);
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
            storageClient.delete(folderToDeletePath, true);
        }
    }

    @Override
    public ImageFolderDTO getImagesFrom(ImageFolderDTO folderToGetImages) {
        final StoragePath folderPath = storageImagesPath.resolve(DIRECTORY, folderToGetImages.getPath());
        final List<StoragePath> filesPath = storageClient.listPaths(folderPath);

        final List<ImageFileDTO> folderFiles = new ArrayList<>();

        for(StoragePath filePath: filesPath){
            if (Format.isImage(FilenameUtils.getExtension(filePath.toString()))) {
                folderFiles.add(storagePathToImageFileDTO.apply(filePath));
            }
        }

        folderToGetImages.setFiles(folderFiles);
        return folderToGetImages;
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
}
