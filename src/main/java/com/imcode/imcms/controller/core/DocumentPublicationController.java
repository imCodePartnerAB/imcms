package com.imcode.imcms.controller.core;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.TypedDocumentService;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for publishing new document's version.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.12.17.
 */
@Controller
@RequestMapping("/publish-document")
public class DocumentPublicationController {

    private final TypedDocumentService documentService;

    public DocumentPublicationController(TypedDocumentService<DocumentDTO> documentService) {
        this.documentService = documentService;
    }

    @RequestMapping("/{docIdentifier}")
    public RedirectView publishDocument(@PathVariable("docIdentifier") int docId) {

        final UserDomainObject user = Imcms.getUser();

        // todo: create annotation instead of copying this each time!
        if (!user.isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to publish documents.");
        }

        documentService.publishDocument(docId, user.getId());

        return new RedirectView("/" + docId, true);
    }

}
