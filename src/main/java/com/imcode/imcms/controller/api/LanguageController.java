package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import imcode.server.Imcms;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/languages")
public class LanguageController {

    private final LanguageService languageService;

    LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public List<Language> getLanguages() {
        return languageService.getAll();
    }

    @PutMapping
    public void changeLanguageForCurrentUser(@RequestBody LanguageDTO languageDTO) {
        final Language language = languageService.findByCode(languageDTO.getCode());

        Imcms.setLanguage(language);
    }
}
