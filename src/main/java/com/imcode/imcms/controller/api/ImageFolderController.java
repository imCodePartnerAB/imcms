package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.security.CheckAccess;
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
    public ImageFolderDTO getImageFolder(ImageFolderDTO folderToGet) {
        return folderToGet.getPath() == null
                ? imageFolderService.getImageFolder()
                : imageFolderService.getImagesFrom(folderToGet);
    }

    @PostMapping("/can-delete")
    @CheckAccess
    public boolean canBeDeleted(@RequestBody ImageFolderDTO folderToCheck) throws IOException {
        return imageFolderService.canBeDeleted(folderToCheck);
    }

    @PostMapping
    @CheckAccess
    public boolean createImageFolder(@RequestBody ImageFolderDTO folderToCreate) {
        return imageFolderService.createImageFolder(folderToCreate);
    }

    @PutMapping
    @CheckAccess
    public boolean renameFolder(@RequestBody ImageFolderDTO folderToRename) {
        return imageFolderService.renameFolder(folderToRename);
    }

    @DeleteMapping
    @CheckAccess
    public boolean deleteFolder(@RequestBody ImageFolderDTO folderToDelete) throws IOException {
        return imageFolderService.deleteFolder(folderToDelete);
    }
}
