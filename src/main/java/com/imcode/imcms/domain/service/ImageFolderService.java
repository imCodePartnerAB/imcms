package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageFolderDTO;

import java.io.IOException;

/**
 * Service for Images Content Manager.
 * CRUD operations with image folders and content.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.10.17.
 */
public interface ImageFolderService {

    ImageFolderDTO getImageFolder();

    boolean createImageFolder(ImageFolderDTO folderToCreate);

    boolean renameFolder(ImageFolderDTO renameMe);

    boolean canBeDeleted(ImageFolderDTO folderToCheck) throws IOException;

    boolean deleteFolder(ImageFolderDTO deleteMe) throws IOException;

    ImageFolderDTO getImagesFrom(ImageFolderDTO folderToGetImages);
}
