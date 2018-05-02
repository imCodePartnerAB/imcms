package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.ImageFolderService;
import imcode.util.io.FileUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

@Service
@Transactional
class DefaultImageFolderService implements ImageFolderService {

    private final Function<File, ImageFolderDTO> fileToImageFolderDTO;

    @Value("${ImagePath}")
    private File imagesPath;

    DefaultImageFolderService(Function<File, ImageFolderDTO> fileToImageFolderDTO) {
        this.fileToImageFolderDTO = fileToImageFolderDTO;
    }

    @Override
    public ImageFolderDTO getImageFolder() {
        return fileToImageFolderDTO.apply(imagesPath);
    }

    @Override
    public boolean createImageFolder(ImageFolderDTO folderToCreate) {
        final String imageFolderRelativePath = folderToCreate.getPath();
        final File newFolder = new File(imagesPath, imageFolderRelativePath);

        if (newFolder.exists()) {
            throw new FolderAlreadyExistException("Folder with path " + imageFolderRelativePath + " already exist");
        }

        return newFolder.mkdir();
    }

    @Override
    public boolean renameFolder(ImageFolderDTO renameMe) {
        final String newName = renameMe.getName();
        final String imageFolderRelativePath = renameMe.getPath();
        final String path = StringUtils.substringBeforeLast(imageFolderRelativePath, File.separator);

        final File folder = new File(imagesPath, imageFolderRelativePath);
        final File newFolder = new File(imagesPath, path + File.separator + newName);

        if (!folder.exists()) {
            throw new FolderNotExistException("Folder with path " + imageFolderRelativePath + " not exist!");
        }

        if (folder.getAbsolutePath().equals(newFolder.getAbsolutePath())) {
            return true;
        }

        if (newFolder.exists()) {
            throw new FolderAlreadyExistException("Folder with path " + path + File.separator + newName + " already exist!");
        }

        return folder.renameTo(newFolder);
    }

    @Override
    public boolean deleteFolder(ImageFolderDTO deleteMe) throws IOException {
        final String imageFolderRelativePath = deleteMe.getPath();
        final File folderToDelete = new File(imagesPath, imageFolderRelativePath);

        if (!folderToDelete.exists() || !folderToDelete.isDirectory()) {
            throw new FolderNotExistException("Folder with path " + imageFolderRelativePath + " not exist!");
        }

        return FileUtility.forceDelete(folderToDelete);
    }
}
