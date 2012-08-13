package com.imcode.imcms.mapping;

import com.imcode.imcms.api.I18nLanguage;
import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;


public class DocumentGetterWrapper implements DocumentGetter {

    private DocumentGetter documentGetter ;

    public DocumentGetterWrapper(DocumentGetter documentGetter) {
        this.documentGetter = documentGetter;
    }

    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        return documentGetter.getDocuments(documentIds) ;
    }

    public DocumentDomainObject getDocument(int documentId) {
        return documentGetter.getDocument(documentId) ;
    }
    
   public DocumentDomainObject getDefaultDocument(int documentId, I18nLanguage language) {
        return documentGetter.getDefaultDocument(documentId, language);
   }

   public DocumentDomainObject getDefaultDocument(int documentId) {
        return documentGetter.getDefaultDocument(documentId);
   }

  //  public DocumentDomainObject getWorkingDocument(Integer documentId) {
   //     return documentGetter.getWorkingDocument(documentId) ;
    //}

//	public DocumentDomainObject getDocument(Integer documentId, Integer version) {
//		return documentGetter.getDocument(documentId, version);
//	}
}