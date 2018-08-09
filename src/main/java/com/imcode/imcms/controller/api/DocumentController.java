package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.11.17.
 */
@RestController
@RequestMapping("/documents")
class DocumentController {

    private DelegatingByTypeDocumentService documentService;

    DocumentController(DelegatingByTypeDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public Document get(Integer docId, DocumentType type, Integer parentDocId) {
        if (docId == null) {
            return documentService.createNewDocument(type, parentDocId);

        } else {
            return documentService.get(docId);
        }
    }

    @PostMapping("/copy/{docId}")
    public Document copy(@PathVariable final Integer docId) {
        return documentService.copy(docId);
    }

    /**
     * Simply save document.
     *
     * @param saveMe unified document, compatible with each {@link DocumentType} except HTML (yet?)
     * @return saved document
     */
    @PostMapping
    @CheckAccess(AccessType.DOC_INFO)
    public Document save(@RequestBody UberDocumentDTO saveMe) {
        return documentService.save(saveMe);
    }

    @CheckAccess
    @DeleteMapping("/{docId}")
    public void delete(@PathVariable Integer docId) {
        documentService.deleteByDocId(docId);
    }
}
