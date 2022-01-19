package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.dto.TextHistoryDTO;
import com.imcode.imcms.domain.service.TextHistoryService;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/texts/history")
public class TextHistoryController {

    private final TextHistoryService textHistoryService;

    public TextHistoryController(TextHistoryService textHistoryService) {
        this.textHistoryService = textHistoryService;
    }

    @GetMapping
    @CheckAccess(docPermission = AccessContentType.TEXT)
    public List<TextHistoryDTO> getTextHistories(TextDTO textDTO) {
        return this.textHistoryService.getAll(textDTO);
    }
}
