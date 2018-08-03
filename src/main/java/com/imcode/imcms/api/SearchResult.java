package com.imcode.imcms.api;

import java.util.Collections;
import java.util.List;

public class SearchResult<T> {

    private List<T> documents;
    private int totalCount;

    public SearchResult() {
    }

    public SearchResult(List<T> documents, int totalCount) {
        this.documents = documents;
        this.totalCount = totalCount;
    }

    public static <T> SearchResult<T> empty() {
        return new SearchResult<>(Collections.emptyList(), 0);
    }

    public static <T> SearchResult<T> of(List<T> documents, int totalCount) {
        return new SearchResult<>(documents, totalCount);
    }

    public List<T> getDocuments() {
        return documents;
    }

    public void setDocuments(List<T> documents) {
        this.documents = documents;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
