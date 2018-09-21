package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.exception.ImageReferenceException;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.domain.service.ImageService;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@Transactional
class DefaultImageFileService implements ImageFileService {

    private final Function<File, ImageFileDTO> fileToImageFileDTO;

    @Autowired
    private ImageService imageService;

    @Value("${ImagePath}")
    private File imagesPath;

    DefaultImageFileService(Function<File, ImageFileDTO> fileToImageFileDTO) {
        this.fileToImageFileDTO = fileToImageFileDTO;
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
    public boolean deleteImage(ImageFileDTO imageFileDTO) throws IOException {
        final String imageFileDTOPath = imageFileDTO.getPath();

        List<ImageDTO> foundUsagesInDocumentContent = imageService.getUsedImagesInWorkingAndLatestVersions(imageFileDTOPath);

        if (!foundUsagesInDocumentContent.isEmpty()) {
            throw new ImageReferenceException("Requested image file " + imageFileDTOPath.replaceFirst(File.separator, "") + " is referenced at system");
        }

        final File imageFile = new File(imagesPath, imageFileDTOPath);

        return FileUtility.forceDelete(imageFile);
    }
}
