package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.api.DocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.11.17.
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public DocumentDTO getDocument(int docId) {
        return documentService.get(docId);
    }
}
