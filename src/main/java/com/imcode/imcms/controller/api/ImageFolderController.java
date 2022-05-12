package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.security.AccessRoleType;
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
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public List<ImageFolderItemUsageDTO> checkImageUsages(ImageFolderDTO folderToCheck) {
        return imageFolderService.checkFolder(folderToCheck);
    }

    @PostMapping("/can-delete")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public boolean canBeDeleted(@RequestBody ImageFolderDTO folderToCheck) throws IOException {
        return imageFolderService.canBeDeleted(folderToCheck);
    }

    @PostMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public void createImageFolder(@RequestBody ImageFolderDTO folderToCreate) {
        imageFolderService.createImageFolder(folderToCreate);
    }

    @PutMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public void renameFolder(@RequestBody ImageFolderDTO folderToRename) {
        imageFolderService.renameFolder(folderToRename);
    }

    @DeleteMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public void deleteFolder(@RequestBody ImageFolderDTO folderToDelete) throws IOException {
        imageFolderService.deleteFolder(folderToDelete);
    }
}
