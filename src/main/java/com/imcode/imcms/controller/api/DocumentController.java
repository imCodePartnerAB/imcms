package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.11.17.
 */
@RestController
@RequestMapping("/documents")
class DocumentController {

    private DocumentService<DocumentDTO> documentService;

    DocumentController(DocumentService<DocumentDTO> documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public DocumentDTO get(Integer docId, DocumentType type) {
        if (docId == null) {
            return documentService.createEmpty(type);

        } else {
            return documentService.get(docId);
        }
    }

    /**
     * Simply save document.
     *
     * @param saveMe unified document, compatible with each {@link DocumentType} except HTML (yet?)
     * @return saved document's id
     */
    @PostMapping
    public int save(@RequestBody UberDocumentDTO saveMe) {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change document structure.");
        }

        return documentService.save(saveMe);
    }

    @DeleteMapping
    public void delete(@RequestBody DocumentDTO deleteMe) { // todo: change to receive only id

        throw new NotImplementedException("Document deletion is disabled for now...");

        // todo: create annotation instead of copying this each time!
//        if (!Imcms.getUser().isSuperAdmin()) {
//            throw new NoPermissionToEditDocumentException("User do not have access to change document structure.");
//        }
//
//        documentService.delete(deleteMe);
    }
}
