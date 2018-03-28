package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.DocumentValidatingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents/validate")
public class DocumentValidationController {

    private final DocumentValidatingService validatingService;

    public DocumentValidationController(DocumentValidatingService validatingService) {
        this.validatingService = validatingService;
    }

    @GetMapping("/isTextDocument/{identifier}")
    public Boolean isTextDoc(@PathVariable("identifier") String identifier) {
        return validatingService.isTextDocument(identifier);
    }

}
