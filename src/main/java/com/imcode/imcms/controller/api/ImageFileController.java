package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFileUsageDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
    @CheckAccess
    public List<ImageFileDTO> saveNewImageFiles(@RequestParam(required = false) String folder,
                                                @RequestParam List<MultipartFile> files) throws IOException {
        return imageFileService.saveNewImageFiles(folder, files);
    }

    @DeleteMapping
    @CheckAccess
    public List<ImageFileUsageDTO> deleteImage(@RequestBody ImageFileDTO imageFileDTO, HttpServletResponse response) throws IOException {
        List<ImageFileUsageDTO> usages = imageFileService.deleteImage(imageFileDTO);

        if (!usages.isEmpty()) {
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        }
        return usages;
    }
}
