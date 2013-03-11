package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentQuery;
import imcode.server.document.index.IndexException;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MockDocumentIndex implements DocumentIndex {

    private boolean indexDocumentCalled;
    private boolean removeDocumentCalled;

    public void indexDocument(DocumentDomainObject document) throws IndexException {
        this.indexDocumentCalled = true;
    }

    public void removeDocument(DocumentDomainObject document) throws IndexException {
        this.removeDocumentCalled = true;
    }


    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return Arrays.asList(new DocumentDomainObject[0]);
    }

    public void rebuild() {
    }

    public boolean isRemoveDocumentCalled() {
        return removeDocumentCalled;
    }

    public boolean isIndexDocumentCalled() {
        return indexDocumentCalled;
    }

    @Override
    public List<DocumentDomainObject> search(SolrQuery solrQuery, UserDomainObject searchingUser) throws IndexException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DocumentIndexService service() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void indexDocument(int docId) throws IndexException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeDocument(int docId) throws IndexException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
