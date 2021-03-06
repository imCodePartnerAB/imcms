package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.model.Document;
import org.apache.solr.common.SolrInputDocument;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface BasicDocumentService<D extends Document> extends DeleterByDocumentId {

    long countDocuments();

    D get(int docId) throws DocumentNotExistException;

    boolean publishDocument(int docId, int userId);

    SolrInputDocument index(int docId);

    D copy(int docId);

    String getUniqueAlias(String alias);

}
