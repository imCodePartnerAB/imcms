package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageHistoryDTO;
import com.imcode.imcms.domain.service.ImageHistoryService;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/images/history")
public class ImageHistoryController {

    private final ImageHistoryService imageHistoryService;

    public ImageHistoryController(ImageHistoryService imageHistoryService) {
        this.imageHistoryService = imageHistoryService;
    }

    @GetMapping
    @CheckAccess(docPermission = AccessContentType.IMAGE)
    public List<ImageHistoryDTO> getImage(ImageDTO imageDTO) {
        return imageHistoryService.getAll(imageDTO);
    }
}
