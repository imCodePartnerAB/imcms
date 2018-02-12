package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
public class ImageController {

    public static final Logger LOG = Logger.getLogger(ImageController.class);

    private final ImageService imageService;

    ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ImageDTO getImage(@ModelAttribute ImageDTO imageDTO) {
        return imageService.getImage(imageDTO);
    }

    @PostMapping
    @CheckAccess(AccessType.IMAGE)
    public void saveImage(@RequestBody ImageDTO image) {
        imageService.saveImage(image);
    }

    @DeleteMapping
    @CheckAccess(AccessType.IMAGE)
    public void deleteImage(@RequestBody ImageDTO image) {
        imageService.deleteImage(image);
    }
}
