package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.IndexException;
import imcode.server.document.index.DocumentQuery;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.util.List;
import java.util.Arrays;

public class MockDocumentIndex implements DocumentIndex {
    private boolean indexDocumentCalled;
    private boolean removeDocumentCalled;

    public void indexDocument( DocumentDomainObject document ) throws IndexException {
        this.indexDocumentCalled = true ;
    }

    public void removeDocument( DocumentDomainObject document ) throws IndexException {
        this.removeDocumentCalled = true ;
    }

    public List search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
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
}
