package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.ValidationLink;
import com.imcode.imcms.domain.service.LinkValidationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/links")
public class LinksValidationController {

    private final LinkValidationService linkValidationService;

    public LinksValidationController(LinkValidationService linkValidationService) {
        this.linkValidationService = linkValidationService;
    }

    @GetMapping("/validate")
    public List<ValidationLink> validateDocumentsLinks(@RequestParam int startDocumentId,
                                                       @RequestParam int endDocumentId,
                                                       @RequestParam boolean filterBrokenLinks) {
        return linkValidationService.validateDocumentsLinks(startDocumentId, endDocumentId, filterBrokenLinks);
    }
}
