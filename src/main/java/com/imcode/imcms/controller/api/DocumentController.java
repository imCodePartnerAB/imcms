package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.service.TypedDocumentService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.11.17.
 */
@RestController
@RequestMapping("/documents")
class DocumentController {

    private TypedDocumentService<Document> documentService;

    DocumentController(TypedDocumentService<Document> documentService) {
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

    @RequestMapping(value = "/copy/{docId}", method = RequestMethod.POST)
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
    public UberDocumentDTO save(@RequestBody UberDocumentDTO saveMe) {
        return documentService.save(saveMe);
    }

    @DeleteMapping
//    @CheckAccess(AccessType.DOC_INFO)
    public void delete(@RequestBody DocumentDTO deleteMe) { // todo: change to receive only id

        throw new NotImplementedException("Document deletion is disabled for now...");
//        documentService.delete(deleteMe);
    }
}
