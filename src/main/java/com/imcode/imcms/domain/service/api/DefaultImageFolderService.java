package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.exception.DirectoryNotEmptyException;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.ImageFolderService;
import imcode.util.image.Format;
import imcode.util.io.FileUtility;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@Transactional
class DefaultImageFolderService implements ImageFolderService {

    private final BiFunction<File, Boolean, ImageFolderDTO> fileToImageFolderDTO;
    private final Function<File, ImageFileDTO> fileToImageFileDTO;
    private File imagesPath;

    @SneakyThrows
    DefaultImageFolderService(BiFunction<File, Boolean, ImageFolderDTO> fileToImageFolderDTO,
                              Function<File, ImageFileDTO> fileToImageFileDTO,
                              @Value("${ImagePath}") Resource imagesPath) {

        this.fileToImageFolderDTO = fileToImageFolderDTO;
        this.fileToImageFileDTO = fileToImageFileDTO;
        this.imagesPath = imagesPath.getFile();
    }

    @Override
    public ImageFolderDTO getImageFolder() {
        return fileToImageFolderDTO.apply(imagesPath, true);
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

    public boolean canBeDeleted(ImageFolderDTO checkMe) {
        final String imageFolderRelativePath = checkMe.getPath();
        final File folderToDelete = new File(imagesPath, imageFolderRelativePath);

        if (!folderToDelete.exists() || !folderToDelete.isDirectory()) {
            throw new FolderNotExistException("Folder with path " + imageFolderRelativePath + " not exist!");
        }

        File[] files = Optional.ofNullable(
                folderToDelete.listFiles(
                        file -> file.isFile() && Format.isImage(FilenameUtils.getExtension(file.getName()))
                )
        ).orElse(new File[0]);

        if (files.length > 0) {
            throw new DirectoryNotEmptyException("Folder with path " + imageFolderRelativePath + " not empty!");
        }

        return true;
    }

    @Override
    public boolean deleteFolder(ImageFolderDTO deleteMe) throws IOException {
        if (canBeDeleted(deleteMe)) {
            final String imageFolderRelativePath = deleteMe.getPath();
            final File folderToDelete = new File(imagesPath, imageFolderRelativePath);

            return FileUtility.forceDelete(folderToDelete);
        } else {
            return false;
        }
    }

    @Override
    public ImageFolderDTO getImagesFrom(ImageFolderDTO folderToGetImages) {

        final File folderFile = new File(imagesPath, folderToGetImages.getPath());

        final File[] files = folderFile.listFiles();

        if (files == null) {
            return folderToGetImages;
        }

        final List<ImageFileDTO> folderFiles = new ArrayList<>();

        for (File file : files) {
            if (Format.isImage(FilenameUtils.getExtension(file.getName()))) {
                folderFiles.add(fileToImageFileDTO.apply(file));
            }
        }

        folderToGetImages.setFiles(folderFiles);

        return folderToGetImages;
    }
}
