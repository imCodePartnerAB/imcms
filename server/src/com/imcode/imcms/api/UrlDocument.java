package com.imcode.imcms.api;

import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;

public class UrlDocument extends Document {

    public final static int TYPE_ID = DocumentTypeDomainObject.URL_ID;

    UrlDocument(UrlDocumentDomainObject document, ContentManagementSystem contentManagementSystem) {
        super(document, contentManagementSystem);
    }

    public void setUrl(String url ) throws NoPermissionException {
        contentManagementSystem.getSecurityChecker().hasEditPermission( this );
        UrlDocumentDomainObject urlDocumentDomainObject = getInternalUrlDocument();
        urlDocumentDomainObject.setUrl( url );
    }

    private UrlDocumentDomainObject getInternalUrlDocument() {
        UrlDocumentDomainObject urlDocumentDomainObject = (UrlDocumentDomainObject)getInternal();
        return urlDocumentDomainObject;
    }

    public String getUrl() throws NoPermissionException {
        contentManagementSystem.getSecurityChecker().hasAtLeastDocumentReadPermission( this );
        return getInternalUrlDocument().getUrl();
    }
}
