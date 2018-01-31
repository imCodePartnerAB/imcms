package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.service.ImageFolderService;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controller for Images Content Manager.
 * CRUD operations with image folders.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.10.17.
 */
@RestController
@RequestMapping("/images/folders")
public class ImageFolderController {

    private final ImageFolderService imageFolderService;

    @Autowired
    ImageFolderController(ImageFolderService imageFolderService) {
        this.imageFolderService = imageFolderService;
    }

    @GetMapping
    public ImageFolderDTO getImageFolder() {
        return imageFolderService.getImageFolder();
    }

    @PostMapping
    public boolean createImageFolder(@RequestBody ImageFolderDTO folderToCreate) {
        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }

        return imageFolderService.createImageFolder(folderToCreate);
    }

    @PutMapping
    public boolean renameFolder(@RequestBody ImageFolderDTO folderToRename) {
        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }

        return imageFolderService.renameFolder(folderToRename);
    }

    @DeleteMapping
    public boolean deleteFolder(@RequestBody ImageFolderDTO folderToDelete) throws IOException {
        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }

        return imageFolderService.deleteFolder(folderToDelete);
    }
}
