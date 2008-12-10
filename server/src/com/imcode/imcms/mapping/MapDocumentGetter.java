package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang.UnhandledException;

public class MapDocumentGetter extends AbstractDocumentGetter {

    Map documentsMap = new HashMap() ;

    public MapDocumentGetter(DocumentDomainObject[] documents) {
        for ( int i = 0; null != documents && i < documents.length; i++ ) {
            DocumentDomainObject document = documents[i];
            documentsMap.put(new Integer(document.getId()), document) ;
        }
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        DocumentDomainObject document = (DocumentDomainObject) documentsMap.get(documentId) ;
        if (null != document) {
            try {
                return (DocumentDomainObject) document.clone() ;
            } catch ( CloneNotSupportedException e ) {
                throw new UnhandledException(e);
            }
        }
        return null ;
    }
}
