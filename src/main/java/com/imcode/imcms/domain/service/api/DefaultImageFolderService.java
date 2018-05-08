package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.ImageFolderService;
import imcode.util.image.Format;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@Transactional
class DefaultImageFolderService implements ImageFolderService {


    private final Function<File, ImageFileDTO> fileToImageFileDTO;
    private final File imagesPath;

    DefaultImageFolderService(Function<File, ImageFileDTO> fileToImageFileDTO,
                              @Value("${ImagePath}") File imagesPath) {

        this.fileToImageFileDTO = fileToImageFileDTO;
        this.imagesPath = imagesPath;
    }

    @Override
    public ImageFolderDTO getImageFolder(ImageFolderDTO folderToGet) {
        final String path = folderToGet.getPath();

        final File[] files = new File(imagesPath.getPath(), path).listFiles();

        if (files == null) {
            return folderToGet;
        }

        final List<ImageFolderDTO> subFolders = new ArrayList<>();
        final List<ImageFileDTO> folderFiles = new ArrayList<>();

        for (final File file : files) {
            final String fileName = file.getName();

            if ((file.isDirectory())) {
                final String relativePath = file.getPath().replace(imagesPath.getPath(), "");
                subFolders.add(new ImageFolderDTO(fileName, relativePath));

            } else if (Format.isImage(FilenameUtils.getExtension(fileName))) {
                folderFiles.add(fileToImageFileDTO.apply(file));
            }
        }

        folderToGet.setName(path.isEmpty() ? imagesPath.getName() : FilenameUtils.getName(path));

        folderToGet.setFiles(folderFiles);
        folderToGet.setFolders(subFolders);

        return folderToGet;
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
