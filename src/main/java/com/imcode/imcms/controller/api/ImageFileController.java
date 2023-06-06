package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ExifDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFileUsageDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public List<ImageFileDTO> saveNewImageFiles(@RequestParam(required = false) String folder,
                                                @RequestParam List<MultipartFile> files) throws IOException {
        return imageFileService.saveNewImageFiles(folder, files);
    }

	@PostMapping("moveImageFile")
	public ImageFileDTO moveImageFile(@RequestBody ImageFileDTO imageFileDTO) throws IOException {
		return imageFileService.moveImageFile(imageFileDTO.getPath(), imageFileDTO.getName());
	}

    @PutMapping("/editMetadata")
    public ImageFileDTO editMetadata(String path, @RequestBody ExifDTO.CustomExifDTO customExifDTO) throws IOException {
        return imageFileService.editCommentMetadata(path, customExifDTO);
    }

    @DeleteMapping
    public List<ImageFileUsageDTO> deleteImage(@RequestBody ImageFileDTO imageFileDTO, HttpServletResponse response) throws IOException {
        List<ImageFileUsageDTO> usages = imageFileService.deleteImage(imageFileDTO);

        if (!usages.isEmpty()) {
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        }
        return usages;
    }
}
