package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ImageDTO getImage(@ModelAttribute ImageDTO imageDTO) {
        return imageService.getImage(imageDTO);
    }

	@GetMapping("loop")
	public List<ImageDTO> getLoopImages(@ModelAttribute ImageDTO imageDTO) {
		return imageService.getLoopImages(imageDTO.getDocId(), imageDTO.getLangCode(), imageDTO.getLoopEntryRef().getLoopIndex());
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
