package com.imcode.imcms.api;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.user.UserAndRoleMapper;

/**
 * Created by IntelliJ IDEA.
 * User: Hasse
 * Date: 2004-feb-11
 * Time: 09:54:10
 * To change this template use File | Settings | File Templates.
 */
public class UrlDocument extends Document {
    public UrlDocument(DocumentDomainObject document, SecurityChecker securityChecker, DocumentService documentService, DocumentMapper documentMapper, DocumentPermissionSetMapper documentPermissionSetMapper, UserAndRoleMapper userAndRoleMapper) {
        super(document, securityChecker, documentService, documentMapper, documentPermissionSetMapper, userAndRoleMapper);
    }

    public void setUrl(String url ) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        internalDocument.setUrlRef( url );
    }

    public String getUrl() throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        return internalDocument.getUrlRef();
    }
}
