package com.imcode.imcms.api;

import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.user.UserAndRoleMapper;
import imcode.server.IMCServiceInterface;

public class UrlDocument extends Document {
    public UrlDocument(UrlDocumentDomainObject document, IMCServiceInterface service, SecurityChecker securityChecker, DocumentService documentService, DocumentMapper documentMapper, DocumentPermissionSetMapper documentPermissionSetMapper, UserAndRoleMapper userAndRoleMapper) {
        super(document, service, securityChecker, documentService, documentMapper, documentPermissionSetMapper, userAndRoleMapper);
    }

    public void setUrl(String url ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        ((UrlDocumentDomainObject)internalDocument).setUrlDocumentUrl( url );
    }

    public String getUrl() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return ((UrlDocumentDomainObject)internalDocument).getUrl();
    }
}
