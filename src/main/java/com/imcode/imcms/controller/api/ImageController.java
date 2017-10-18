package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.service.api.ImageService;
import imcode.server.Imcms;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
public class ImageController {

    public static final Logger LOG = Logger.getLogger(ImageController.class);

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ImageDTO getImage(@ModelAttribute ImageDTO imageDTO) {
        return imageService.getImage(imageDTO);
    }

    @PostMapping
    public void saveImage(@RequestBody ImageDTO image) throws IllegalAccessException {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new IllegalAccessException("User do not have access to change image structure.");
        }

        imageService.saveImage(image);
    }
}
