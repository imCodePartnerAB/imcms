package com.imcode.imcms.api;

import imcode.server.document.UrlDocumentDomainObject;

public class UrlDocument extends Document {
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
