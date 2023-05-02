package com.imcode.imcms.mapping;

import com.imcode.imcms.api.SearchResult;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentQuery;
import imcode.server.document.index.IndexException;
import imcode.server.user.UserDomainObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class MockDocumentIndex implements DocumentIndex {
    private boolean indexDocumentCalled;
    private boolean removeDocumentCalled;

    public void indexDocument( DocumentDomainObject document ) throws IndexException {
        this.indexDocumentCalled = true ;
    }

    public void removeDocument( DocumentDomainObject document ) throws IndexException {
        this.removeDocumentCalled = true ;
    }

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return Arrays.asList(new DocumentDomainObject[0]);
    }

    public SearchResult<DocumentDomainObject> search(DocumentQuery query,
                                                     UserDomainObject searchingUser,
                                                     int startPosition,
                                                     int maxResults) throws IndexException {
        return SearchResult.empty();
    }

    @Override
    public SearchResult<DocumentDomainObject> search(DocumentQuery query,
                                                     UserDomainObject searchingUser,
                                                     int startPosition,
                                                     int maxResults,
                                                     Predicate<DocumentDomainObject> filterPredicate) throws IndexException {
        return SearchResult.empty();
    }

    public void rebuild() {
    }

    @Override
    public boolean isIndexBuildingThreadAlive() {
        return false;
    }

    public boolean isRemoveDocumentCalled() {
        return removeDocumentCalled;
    }

    public boolean isIndexDocumentCalled() {
        return indexDocumentCalled;
    }
}
