package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.component.ImageFolderCacheManager;
import com.imcode.imcms.domain.dto.ExifDTO;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFileUsageDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.mapping.ImageCacheMapper;
import com.imcode.imcms.persistence.entity.ImageCacheDomainObject;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import imcode.server.document.textdocument.FileStorageImageSource;
import imcode.util.ImcmsImageUtils;
import imcode.util.Utility;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

@Service
@Transactional
class DefaultImageFileService implements ImageFileService {

    private final ImageService imageService;
    private final ImageFolderCacheManager imageFolderCacheManager;
    private final ImageCacheMapper imageCacheMapper;

    private final StorageClient storageClient;
    private final StoragePath storageImagesPath;

    private final Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO;

    DefaultImageFileService(Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO,
                            ImageService imageService,
                            ImageFolderCacheManager imageFolderCacheManager,
                            @Value("${ImagePath}") String imagesPath, ImageCacheMapper imageCacheMapper,
                            @Qualifier("imageStorageClient") StorageClient storageClient) {
        this.storagePathToImageFileDTO = storagePathToImageFileDTO;
	    this.imageService = imageService;
        this.imageFolderCacheManager = imageFolderCacheManager;
        this.storageImagesPath = StoragePath.get(DIRECTORY, imagesPath);
        this.imageCacheMapper = imageCacheMapper;
        this.storageClient = storageClient;
    }

    @Override
    public List<ImageFileDTO> saveNewImageFiles(String folder, List<MultipartFile> files) throws IOException {
        final StoragePath targetFolderPath = getTargetFolder(folder);
        final ArrayList<ImageFileDTO> imageFileDTOS = new ArrayList<>();
        final Function<StoragePath, Boolean> mapAndAddToList = storagePathToImageFileDTO.andThen(imageFileDTOS::add);

        // do not rewrite using Java Stream API, file transfer can be long operation in cycle.
        for (MultipartFile file : files) {
            final StoragePath destination = getDestinationFolder(targetFolderPath, file.getOriginalFilename());

            storageClient.put(destination, file.getInputStream());
            mapAndAddToList.apply(destination);
        }

        imageFolderCacheManager.invalidate(targetFolderPath);

        return imageFileDTOS;
    }

	@Override
	public ImageFileDTO saveNewImageFile(String folder, Path filePath) throws IOException {
        final StoragePath targetFolderPath = getTargetFolder(folder);
        final StoragePath destination = getDestinationFolder(targetFolderPath, filePath.getFileName().toString());

        storageClient.put(destination, Files.newInputStream(filePath));

        imageFolderCacheManager.invalidate(targetFolderPath);

        return storagePathToImageFileDTO.apply(destination);
	}

	private StoragePath getTargetFolder(String folder) {
        StoragePath targetFolderPath = storageImagesPath;

        if (!(folder == null || folder.isEmpty())) {
            targetFolderPath = targetFolderPath.resolve(DIRECTORY, folder);

            if (!storageClient.exists(targetFolderPath)) {
                throw new StorageFileNotFoundException("Folder " + folder + " not exist! Folder creation is another service job.");
            } else if (!storageClient.canPut(targetFolderPath)) {
                throw new RuntimeException("Can't write to specified directory!");
            }
        }
        return targetFolderPath;
    }

    private StoragePath getDestinationFolder(StoragePath targetFolderPath, String filename){
        int copiesCount = 1;
        final String originalFilename = Utility.normalizeString(filename)
                .replace("(", "").replace(")", "");
        StoragePath destination = targetFolderPath.resolve(FILE, originalFilename);

        while (storageClient.exists(destination)) {
            final String baseName = FilenameUtils.getBaseName(originalFilename);
            final String newName = baseName + copiesCount + "." + FilenameUtils.getExtension(originalFilename);
            destination = targetFolderPath.resolve(FILE, newName);
            copiesCount++;
        }

        return destination;
    }

    @Override
    public List<ImageFileUsageDTO> deleteImage(ImageFileDTO imageFileDTO) {
        final String imageFileDTOPath = imageFileDTO.getPath();

        List<ImageFileUsageDTO> usages = getImageFileUsages(imageFileDTOPath);
        if (usages.isEmpty()) {
            //No usages found. Can safely remove file
            final StoragePath pathToDelete = storageImagesPath.resolve(FILE, imageFileDTOPath);
            storageClient.delete(pathToDelete, true);

            imageFolderCacheManager.invalidate(pathToDelete.getParentPath());
        }
        return usages;
    }

    @Override
    public List<ImageFileUsageDTO> getImageFileUsages(String imageFileDTOPath) {
        List<ImageJPA> foundUsagesInDocumentContent =
                imageService.getUsedImagesInWorkingAndLatestVersions(imageFileDTOPath.startsWith(File.separator) ? imageFileDTOPath.substring(1) : imageFileDTOPath);

        List<ImageCacheDomainObject> foundImageCache =
                imageCacheMapper.getAllImageResourcesByResourcePath(File.separator + storageImagesPath.getName() + imageFileDTOPath);

        List<ImageFileUsageDTO> usages = new ArrayList<>();
        if (!foundUsagesInDocumentContent.isEmpty() || !foundImageCache.isEmpty()) {
            usages.addAll(foundUsagesInDocumentContent.stream()
                    .map(item -> new ImageFileUsageDTO(item.getVersion().getDocId(), item.getVersion().getNo(), item.getIndex(), "content image"))
                    .collect(Collectors.toList())
            );

            usages.addAll(foundImageCache.stream()
                    .map(item -> new ImageFileUsageDTO(null, null, null, "image cache content"))
                    .collect(Collectors.toList())
            );
        }
        return usages;
    }

	@Override
	public ImageFileDTO moveImageFile(final String destinationFolder, final String filePath) {

		final List<ImageDTO> imagesDTO = imageService.getImagesByUrl(filePath);
        final StoragePath imageFilePath = storageImagesPath.resolve(FILE, filePath);
        final StoragePath destinationImageFilePath = storageImagesPath.resolve(FILE, destinationFolder);

        imagesDTO.forEach(imageDTO -> {
            imageDTO.setPath(storageImagesPath.relativize(destinationImageFilePath).toString());
            imageService.updateImage(imageDTO);
        });

        storageClient.move(imageFilePath, destinationImageFilePath);

        imageFolderCacheManager.invalidate(imageFilePath.getParentPath(), destinationImageFilePath.getParentPath());

        return storagePathToImageFileDTO.apply(destinationImageFilePath);
	}

    @Override
    public ImageFileDTO editCommentMetadata(String path, ExifDTO.CustomExifDTO customExif) throws IOException {
        final StoragePath storagePath = StoragePath.get(SourceFile.FileType.FILE, ImcmsImageUtils.imagesPath, path);

        final String comment = ExifDTO.CustomExifDTO.mapToString(customExif);
        final FileStorageImageSource imageSource = new FileStorageImageSource(path);

        final byte[] content = ImcmsImageUtils.editCommentMetadata(comment, imageSource);

        try(InputStream inputStream = new ByteArrayInputStream(Objects.requireNonNull(content))){
            storageClient.put(storagePath, inputStream);
        }

        imageFolderCacheManager.invalidate(storagePath.getParentPath());

        return storagePathToImageFileDTO.apply(storagePath);
    }

	@Override
	public boolean exists(String imagePath) {
        final String originalFilename = Utility.normalizeString(FilenameUtils.getName(imagePath))
                .replace("(", "").replace(")", "");
        final StoragePath storagePath = storageImagesPath.resolve(FILE, FilenameUtils.getPath(imagePath), originalFilename);
        return storageClient.exists(storagePath);
	}

}
