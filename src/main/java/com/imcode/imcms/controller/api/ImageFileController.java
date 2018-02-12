package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
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
    @CheckAccess(AccessType.IMAGE)
    public List<ImageFileDTO> saveNewImageFiles(@RequestParam(required = false) String folder,
                                                @RequestParam List<MultipartFile> files) throws IOException {
        return imageFileService.saveNewImageFiles(folder, files);
    }

    @DeleteMapping
    @CheckAccess(AccessType.IMAGE)
    public boolean deleteImage(@RequestBody ImageFileDTO imageFileDTO) throws IOException {
        return imageFileService.deleteImage(imageFileDTO);
    }
}
