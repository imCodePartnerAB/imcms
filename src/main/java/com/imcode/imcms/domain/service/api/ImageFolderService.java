package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import imcode.util.io.FileUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

/**
 * Service for Images Content Manager.
 * CRUD operations with image folders and content.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.10.17.
 */
@Service
@Transactional
public class ImageFolderService {

    private final Function<File, ImageFolderDTO> fileToImageFolderDTO;

    @Value("${ImagePath}")
    private File imagesPath;

    public ImageFolderService(Function<File, ImageFolderDTO> fileToImageFolderDTO) {
        this.fileToImageFolderDTO = fileToImageFolderDTO;
    }

    public ImageFolderDTO getImageFolder() {
        return fileToImageFolderDTO.apply(imagesPath);
    }

    public boolean createImageFolder(ImageFolderDTO folderToCreate) {
        final String imageFolderRelativePath = folderToCreate.getPath();
        final File newFolder = new File(imagesPath, imageFolderRelativePath);

        if (newFolder.exists()) {
            throw new FolderAlreadyExistException("Folder with path " + imageFolderRelativePath + " already exist");
        }

        return newFolder.mkdir();
    }

    public boolean renameFolder(ImageFolderDTO renameMe) {
        final String newName = renameMe.getName();
        final String imageFolderRelativePath = renameMe.getPath();
        final String path = StringUtils.substringBeforeLast(imageFolderRelativePath, File.separator);

        final File folder = new File(imagesPath, imageFolderRelativePath);
        final File newFolder = new File(imagesPath, path + "/" + newName);

        if (!folder.exists()) {
            throw new FolderNotExistException("Folder with path " + imageFolderRelativePath + " not exist!");
        }

        if (newFolder.exists()) {
            throw new FolderAlreadyExistException("Folder with path " + path + "/" + newName + " already exist!");
        }

        return folder.renameTo(newFolder);
    }

    public boolean deleteFolder(ImageFolderDTO deleteMe) throws IOException {
        final String imageFolderRelativePath = deleteMe.getPath();
        final File folderToDelete = new File(imagesPath, imageFolderRelativePath);

        if (!folderToDelete.exists() || !folderToDelete.isDirectory()) {
            throw new FolderNotExistException("Folder with path " + imageFolderRelativePath + " not exist!");
        }

        return FileUtility.forceDelete(folderToDelete);
    }
}
