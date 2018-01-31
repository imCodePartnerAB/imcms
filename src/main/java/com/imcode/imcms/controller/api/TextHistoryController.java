package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.dto.TextHistoryDTO;
import com.imcode.imcms.domain.service.TextHistoryService;
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
    public List<TextHistoryDTO> getTextHistories(TextDTO textDTO) {
        final Integer index = textDTO.getIndex();
        final LoopEntryRefDTO loopEntryRef = textDTO.getLoopEntryRef();
        final String langCode = textDTO.getLangCode();

        return this.textHistoryService.findAllByLanguageAndLoopEntryRefAndNo(langCode, loopEntryRef, index);
    }
}
