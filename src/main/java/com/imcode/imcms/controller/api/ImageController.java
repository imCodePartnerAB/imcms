package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.exception.ImageAlternateTextRequiredException;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    @CheckAccess(docPermission = AccessContentType.IMAGE)
    public ImageDTO getImage(@ModelAttribute ImageDTO imageDTO) {
        return imageService.getImage(imageDTO);
    }

	@GetMapping("loop")
    @CheckAccess(docPermission = AccessContentType.LOOP)
	public List<ImageDTO> getLoopImages(@ModelAttribute ImageDTO imageDTO) {
		return imageService.getLoopImages(imageDTO.getDocId(), imageDTO.getLangCode(), imageDTO.getLoopEntryRef().getLoopIndex());
	}

    @PostMapping
    @CheckAccess(docPermission = AccessContentType.IMAGE)
    public void saveImage(@RequestBody ImageDTO image) {
        try {
            imageService.saveImage(image);
        } catch (ImageAlternateTextRequiredException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    @CheckAccess(docPermission = AccessContentType.IMAGE)
    public void deleteImage(@RequestBody ImageDTO image) {
        imageService.deleteImage(image);
    }
}
