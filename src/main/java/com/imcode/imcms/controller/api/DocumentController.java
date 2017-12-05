package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.api.DocumentService;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.11.17.
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private DocumentService documentService;

    DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public DocumentDTO get(Integer docId) {
        return documentService.get(docId);
    }

    @PostMapping
    public void save(@RequestBody DocumentDTO saveMe) {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change document structure.");
        }

        documentService.save(saveMe);
    }

    @DeleteMapping
    public void delete(@RequestBody DocumentDTO deleteMe) {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change document structure.");
        }

        documentService.delete(deleteMe);
    }
}
