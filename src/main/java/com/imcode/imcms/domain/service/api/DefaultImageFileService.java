package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFileUsageDTO;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.mapping.ImageCacheMapper;
import com.imcode.imcms.persistence.entity.ImageCacheDomainObject;
import com.imcode.imcms.persistence.entity.ImageJPA;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultImageFileService implements ImageFileService {

    private final Function<File, ImageFileDTO> fileToImageFileDTO;
	private final Function<ImageJPA, ImageDTO> imageJPAToImageDTO;
    private final ImageService imageService;
    private final ImageCacheMapper imageCacheMapper;
    @Value("${ImagePath}")
    private File imagesPath;

    DefaultImageFileService(Function<File, ImageFileDTO> fileToImageFileDTO,
                            Function<ImageJPA, ImageDTO> imageJPAToImageDTO, ImageService imageService, ImageCacheMapper imageCacheMapper) {
        this.fileToImageFileDTO = fileToImageFileDTO;
	    this.imageJPAToImageDTO = imageJPAToImageDTO;
	    this.imageService = imageService;
        this.imageCacheMapper = imageCacheMapper;
    }

    @Override
    public List<ImageFileDTO> saveNewImageFiles(String folder, List<MultipartFile> files) throws IOException {

        final File targetFolder = getTargetFolder(folder);
        final ArrayList<ImageFileDTO> imageFileDTOS = new ArrayList<>();
        final Function<File, Boolean> mapAndAddToList = fileToImageFileDTO.andThen(imageFileDTOS::add);

        // do not rewrite using Java Stream API, file transfer can be long operation. in cycle.
        for (MultipartFile file : files) {
            int copiesCount = 1;
            String originalFilename = Utility.normalizeString(file.getOriginalFilename());
            originalFilename = originalFilename.replace("(", "").replace(")", "");
            File destination = new File(targetFolder, originalFilename);

            while (destination.exists()) {
                final String baseName = FilenameUtils.getBaseName(originalFilename);
                final String newName = baseName + copiesCount + "." + FilenameUtils.getExtension(originalFilename);
                destination = new File(targetFolder, newName);
                copiesCount++;
            }

            file.transferTo(destination);
            mapAndAddToList.apply(destination);
        }

        return imageFileDTOS;
    }

    private File getTargetFolder(String folder) {
        final File targetFolder;

        if ((folder == null) || folder.isEmpty()) {
            targetFolder = imagesPath;

        } else {
            targetFolder = new File(imagesPath, folder);

            if (!targetFolder.exists()) {
                throw new FolderNotExistException("Folder " + folder + " not exist! Folder creation is another service job.");

            } else if (!targetFolder.isDirectory()) {
                throw new RuntimeException("Target directory is not a directory...");

            } else if (!targetFolder.canWrite()) {
                throw new RuntimeException("Can't write to specified directory!");
            }
        }
        return targetFolder;
    }

    @Override
    public List<ImageFileUsageDTO> deleteImage(ImageFileDTO imageFileDTO) throws IOException {
        final String imageFileDTOPath = imageFileDTO.getPath();

        List<ImageFileUsageDTO> usages = getImageFileUsages(imageFileDTOPath);

        if (usages.isEmpty()) {
            //No usages found. Can safely remove file
            final File imageFile = new File(imagesPath, imageFileDTOPath);
            FileUtility.forceDelete(imageFile);
        }
        return usages;
    }

    @Override
    public List<ImageFileUsageDTO> getImageFileUsages(String imageFileDTOPath) {
        List<ImageJPA> foundUsagesInDocumentContent =
                imageService.getUsedImagesInWorkingAndLatestVersions(imageFileDTOPath.startsWith(File.separator) ? imageFileDTOPath.substring(1) : imageFileDTOPath);

        List<ImageCacheDomainObject> foundImageCache =
                imageCacheMapper.getAllImageResourcesByResourcePath(File.separator + imagesPath.getName() + imageFileDTOPath);

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
	public ImageFileDTO moveImageFile(final String destinationFolder, final String filePath) throws IOException {
		final List<ImageDTO> imagesDTO = imageService.getImagesByUrl(filePath);
		final Path imageFilePath = Paths.get(imagesPath.getPath(), filePath);
		final Path destinationImageFilePath = Paths.get(imagesPath.getPath(), destinationFolder);

		final Path result = Files.move(imageFilePath, destinationImageFilePath);
		final Path relativePath = imagesPath.toPath().relativize(result);

		imagesDTO.forEach(imageDTO -> {
			imageDTO.setPath(relativePath.toString());
			imageService.updateImage(imageDTO);
		});

		return fileToImageFileDTO.apply(result.toFile());
	}
}
