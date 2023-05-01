package com.imcode.imcms.mapping;

import com.imcode.imcms.model.Language;
import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {

    /**
     * @return a list of default documents.
     */
    List<DocumentDomainObject> getDocuments(Collection<Integer> docIds);

    /**
     * Returns default document.
     *
     * @param docId document's id.
     */
    <T extends DocumentDomainObject> T getDocument(int docId);

    /**
     * Returns default document
     *
     * @param docId document's id.
     */
    <T extends DocumentDomainObject> T getDefaultDocument(int docId, Language language);

    <T extends DocumentDomainObject> T getDefaultDocument(int docId);
}
