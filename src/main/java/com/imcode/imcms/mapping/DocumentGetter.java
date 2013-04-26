package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {
    
    /** 
     * @return list of working documents. 
     */ 
    List<DocumentDomainObject> getDocuments(Collection<Integer> metaIds);

    /**
     * Returns default document in default language.
     * 
     * @param metaId document's meta id.
     *
     */
    <T extends DocumentDomainObject> T getDocument(int metaId);

    /**
     * Returns default document
     * @param metaId
     * @return
     */
    <T extends DocumentDomainObject> T getDefaultDocument(int metaId, DocumentLanguage language);

    <T extends DocumentDomainObject> T getDefaultDocument(int metaId);
}