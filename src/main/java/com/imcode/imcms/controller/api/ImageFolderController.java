package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.service.api.ImageFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for Images Content Manager.
 * CRUD operations with image folders and content.
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
    public List<ImageFolderDTO> getImageFolder() {
        return null;
    }
}
