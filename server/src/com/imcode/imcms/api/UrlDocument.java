package com.imcode.imcms.api;

import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.UrlDocumentDomainObject;

/**
 * Document that points to url.
 */
public class UrlDocument extends Document {

    /**
     * TYPE_ID of url document
     */
    public final static int TYPE_ID = DocumentTypeDomainObject.URL_ID;

    UrlDocument(UrlDocumentDomainObject document, ContentManagementSystem contentManagementSystem) {
        super(document, contentManagementSystem);
    }

    /**
     * Sets url of this url document to the given one
     * @param url a url
     */
    public void setUrl(String url ) {
        UrlDocumentDomainObject urlDocumentDomainObject = getInternalUrlDocument();
        urlDocumentDomainObject.setUrl( url );
    }

    private UrlDocumentDomainObject getInternalUrlDocument() {
        UrlDocumentDomainObject urlDocumentDomainObject = (UrlDocumentDomainObject)getInternal();
        return urlDocumentDomainObject;
    }

    /**
     * Returns url of this url document
     * @return this url document's url
     */
    public String getUrl() {
        return getInternalUrlDocument().getUrl();
    }
}
