package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TextValidationResult;
import com.imcode.imcms.domain.service.TextValidationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controller for text validator feature in Text Editor.
 * Validates current text editor content with returning result.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@RestController
@RequestMapping("/texts/validate")
public class TextValidationController {

    private final TextValidationService textValidationService;

    public TextValidationController(TextValidationService textValidationService) {
        this.textValidationService = textValidationService;
    }

    /**
     * Provides simple validation api entry point.
     *
     * @param content Text, that should be validated
     * @return anonymous object entity
     * @throws IOException if content couldn't be validated
     */
    @PostMapping
    public TextValidationResult validateText(@RequestParam String content) throws IOException {
        return textValidationService.validateText(content);
    }

}
