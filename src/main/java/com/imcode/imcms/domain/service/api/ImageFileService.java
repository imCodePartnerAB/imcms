package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Service for Images Content Manager.
 * CRUD operations with image files.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.10.17.
 */
@Service
@Transactional
public class ImageFileService {

    private final Function<File, ImageFileDTO> fileToImageFileDTO;

    @Value("${ImagePath}")
    private File imagesPath;

    public ImageFileService(Function<File, ImageFileDTO> fileToImageFileDTO) {
        this.fileToImageFileDTO = fileToImageFileDTO;
    }

    public List<ImageFileDTO> saveNewImageFiles(String folder, List<MultipartFile> files) throws IOException {

        final File targetFolder = getTargetFolder(folder);
        final ArrayList<ImageFileDTO> imageFileDTOS = new ArrayList<>();
        final Function<File, Boolean> mapAndAddToList = fileToImageFileDTO.andThen(imageFileDTOS::add);

        // do not rewrite using Java Stream API, file transfer can be long operation. in cycle.
        for (MultipartFile file : files) {
            int copiesCount = 1;
            final String originalFilename = file.getOriginalFilename();
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

    public boolean deleteImage(ImageFileDTO imageFileDTO) {
        final String imageFileDTOPath = imageFileDTO.getPath();
        final File imageFile = new File(imagesPath.getParentFile(), imageFileDTOPath);

        return imageFile.delete();
    }
}
