package com.imcode.imcms.controller.core;

import com.imcode.imcms.api.exception.NoPermissionException;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private final DelegatingByTypeDocumentService documentService;
    private final AccessService accessService;
    private final boolean isVersioningAllowed;

    public DocumentPublicationController(DelegatingByTypeDocumentService documentService,
                                         AccessService accessService,
                                         @Value("${document.versioning:true}") boolean isVersioningAllowed) {

        this.documentService = documentService;
        this.accessService = accessService;
        this.isVersioningAllowed = isVersioningAllowed;
    }

    @RequestMapping("/{docIdentifier}")
    public RedirectView publishDocument(@PathVariable("docIdentifier") int docId,
                                        @RequestParam(value = "return", required = false) String returnUrl) {
        final UserDomainObject currentUser = Imcms.getUser();

        if(!accessService.hasUserPublishAccess(currentUser, docId)){
            throw new NoPermissionException("User do not has the necessary permission");
        }

        if (isVersioningAllowed) {
            documentService.publishDocument(docId, currentUser.getId());
        }

        return new RedirectView((returnUrl == null) ? "/" + docId : returnUrl, true);
    }

}
