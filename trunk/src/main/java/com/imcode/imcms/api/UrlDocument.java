package com.imcode.imcms.api;

import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.UrlDocumentDomainObject;

public class UrlDocument extends Document {

    public final static int TYPE_ID = DocumentTypeDomainObject.URL_ID;

    UrlDocument(UrlDocumentDomainObject document, ContentManagementSystem contentManagementSystem) {
        super(document, contentManagementSystem);
    }

    public void setUrl(String url ) {
        UrlDocumentDomainObject urlDocumentDomainObject = getInternalUrlDocument();
        urlDocumentDomainObject.setUrl( url );
    }

    private UrlDocumentDomainObject getInternalUrlDocument() {
        UrlDocumentDomainObject urlDocumentDomainObject = (UrlDocumentDomainObject)getInternal();
        return urlDocumentDomainObject;
    }

    public String getUrl() {
        return getInternalUrlDocument().getUrl();
    }
}
