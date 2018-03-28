package com.imcode.imcms.api;

import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.UrlDocumentDomainObject;

public class UrlDocument extends Document {

    public final static int TYPE_ID = DocumentTypeDomainObject.URL_ID;
    private static final long serialVersionUID = 4023512323646165396L;

    UrlDocument(UrlDocumentDomainObject document, ContentManagementSystem contentManagementSystem) {
        super(document, contentManagementSystem);
    }

    private UrlDocumentDomainObject getInternalUrlDocument() {
        UrlDocumentDomainObject urlDocumentDomainObject = (UrlDocumentDomainObject) getInternal();
        return urlDocumentDomainObject;
    }

    public String getUrl() {
        return getInternalUrlDocument().getUrl();
    }

    public void setUrl(String url) {
        UrlDocumentDomainObject urlDocumentDomainObject = getInternalUrlDocument();
        urlDocumentDomainObject.setUrl(url);
    }
}
