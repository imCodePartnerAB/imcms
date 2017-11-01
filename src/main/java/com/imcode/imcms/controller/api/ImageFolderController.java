package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.service.api.ImageFolderService;
import imcode.server.Imcms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ImageFolderController(ImageFolderService imageFolderService) {
        this.imageFolderService = imageFolderService;
    }

    @GetMapping
    public ImageFolderDTO getImageFolder() {
        return imageFolderService.getImageFolder();
    }

    @PostMapping
    public boolean createNewImageFolder(@RequestBody String folderPath) throws IllegalAccessException {
        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new IllegalAccessException("User do not have access to change image structure.");
        }

        return imageFolderService.createNewFolder(folderPath);
    }
}
