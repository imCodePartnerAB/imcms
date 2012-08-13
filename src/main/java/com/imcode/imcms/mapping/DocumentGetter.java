package com.imcode.imcms.mapping;

import com.imcode.imcms.api.I18nLanguage;
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
    DocumentDomainObject getDocument(int metaId);

    /**
     * Returns default document
     * @param metaId
     * @return
     */
    DocumentDomainObject getDefaultDocument(int metaId, I18nLanguage language);

    DocumentDomainObject getDefaultDocument(int metaId);
}