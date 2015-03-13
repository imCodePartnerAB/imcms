package com.imcode.imcms.api;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentStoredFields;

/**
 * Created by Shadowgun on 05.03.2015.
 */
public class SearchItem {
    private Document foundDocument;
    private DocumentStoredFields documentStoredFields;

    public SearchItem(Document foundDocument, DocumentStoredFields documentStoredFields) {
        this.foundDocument = foundDocument;
        this.documentStoredFields = documentStoredFields;
    }

    public Document getFoundDocument() {
        return foundDocument;
    }

    public DocumentStoredFields getDocumentStoredFields() {
        return documentStoredFields;
    }

}
