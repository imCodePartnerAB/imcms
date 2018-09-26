package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

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

    @GetMapping("/check")
    @CheckAccess
    public List<ImageFolderItemUsageDTO> checkImageUsages(ImageFolderDTO folderToCheck) {
        return imageFolderService.checkFolder(folderToCheck);
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
