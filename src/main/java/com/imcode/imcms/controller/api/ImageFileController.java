package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Images Content Manager.
 * CRUD operations with image files.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.10.17.
 */
@RestController
@RequestMapping("/images/files")
public class ImageFileController {

    private final ImageFileService imageFileService;

    ImageFileController(ImageFileService imageFileService) {
        this.imageFileService = imageFileService;
    }

    @PostMapping
    public List<ImageFileDTO> saveNewImageFiles(@RequestParam(required = false) String folder,
                                                @RequestParam List<MultipartFile> files) throws IOException {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }

        return imageFileService.saveNewImageFiles(folder, files);
    }

    @DeleteMapping
    public boolean deleteImage(@RequestBody ImageFileDTO imageFileDTO) throws IOException {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }

        return imageFileService.deleteImage(imageFileDTO);
    }
}
