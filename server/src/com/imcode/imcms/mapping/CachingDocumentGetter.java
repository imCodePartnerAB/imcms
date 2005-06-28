package com.imcode.imcms.mapping;

import imcode.server.document.DocumentGetter;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentId;

import java.util.Map;
import java.lang.ref.SoftReference;

import org.apache.commons.lang.UnhandledException;

class CachingDocumentGetter implements DocumentGetter {
    private final DocumentGetter documentGetter;
    private final Map documentCache;

    CachingDocumentGetter(DocumentGetter documentGetter, Map documentCache) {
        this.documentGetter = documentGetter;
        this.documentCache = documentCache ;
    }

    public DocumentDomainObject getDocument(DocumentId documentId) {
        DocumentDomainObject result;
        try {
            DocumentDomainObject document = null ;
            SoftReference[] documentSoftReferenceArray = (SoftReference[]) documentCache.get(documentId);
            if (null != documentSoftReferenceArray && null != documentSoftReferenceArray[0]) {
                document = (DocumentDomainObject) documentSoftReferenceArray[0].get();
            }
            if (null == document) {
                documentSoftReferenceArray = new SoftReference[1];
                documentCache.put(documentId, documentSoftReferenceArray);
                document = this.documentGetter.getDocument(documentId);
                documentSoftReferenceArray[0] = new SoftReference(document);
            }
            if (null != document) {
                document = (DocumentDomainObject) document.clone();
            }
            result = document;
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
        return result;
    }

}
