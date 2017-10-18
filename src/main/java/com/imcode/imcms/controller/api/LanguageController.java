package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.service.core.LanguageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/languages")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public List<LanguageDTO> getLanguages() {
        return languageService.getAll();
    }

}
