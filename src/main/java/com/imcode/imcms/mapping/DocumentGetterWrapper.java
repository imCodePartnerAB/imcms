package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.orm.DocumentLanguage;
import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

// todo: remove redundant type annotation (documentGetter.<T>) - introduced to workaroud compiler bug:
// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
public class DocumentGetterWrapper implements DocumentGetter {

    private DocumentGetter documentGetter ;

    public DocumentGetterWrapper(DocumentGetter documentGetter) {
        this.documentGetter = documentGetter;
    }

    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        return documentGetter.getDocuments(documentIds) ;
    }

    public <T extends DocumentDomainObject> T getDocument(int documentId) {
        return documentGetter.<T>getDocument(documentId) ;
    }
    
   public <T extends DocumentDomainObject> T getDefaultDocument(int documentId, DocumentLanguage language) {
        return documentGetter.<T>getDefaultDocument(documentId, language);
   }

   public <T extends DocumentDomainObject> T getDefaultDocument(int documentId) {
        return documentGetter.<T>getDefaultDocument(documentId);
   }

  //  public DocumentDomainObject getWorkingDocument(Integer documentId) {
   //     return documentGetter.getWorkingDocument(documentId) ;
    //}

//	public DocumentDomainObject getDocument(Integer documentId, Integer version) {
//		return documentGetter.getDocument(documentId, version);
//	}
}