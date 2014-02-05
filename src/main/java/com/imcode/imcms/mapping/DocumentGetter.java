package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.orm.DocLanguage;
import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {
    
    /** 
     * @return list of working documents. 
     */ 
    List<DocumentDomainObject> getDocuments(Collection<Integer> docIds);

    /**
     * Returns default document in default language.
     * 
     * @param docId document's meta id.
     *
     */
    <T extends DocumentDomainObject> T getDocument(int docId);

    /**
     * Returns default document
     * @param docId
     * @return
     */
    <T extends DocumentDomainObject> T getDefaultDocument(int docId, DocLanguage language);

    <T extends DocumentDomainObject> T getDefaultDocument(int docId);
}