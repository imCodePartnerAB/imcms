package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;


public class DocumentGetterWrapper implements DocumentGetter {

    private DocumentGetter documentGetter ;

    public DocumentGetterWrapper(DocumentGetter documentGetter) {
        this.documentGetter = documentGetter;
    }

    public List getDocuments(Collection documentIds) {
        return documentGetter.getDocuments(documentIds) ;
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        return documentGetter.getDocument(documentId) ;
    }
    
    public DocumentDomainObject getPublishedDocument(Integer documentId) {
        return documentGetter.getPublishedDocument(documentId) ;
    }    
    
    public DocumentDomainObject getWorkingDocument(Integer documentId) {
        return documentGetter.getWorkingDocument(documentId) ;
    }

	public DocumentDomainObject getDocument(Integer documentId, Integer version) {
		return documentGetter.getDocument(documentId, version);
	}    
}