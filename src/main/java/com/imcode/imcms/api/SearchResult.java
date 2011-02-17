package com.imcode.imcms.api;

import java.util.List;

public class SearchResult {
    private List documents;
    private int totalCount;


    public SearchResult() {
    }

    public SearchResult(List documents, int totalCount) {
        this.documents = documents;
        this.totalCount = totalCount;
    }


    public List getDocuments() {
        return documents;
    }

    public void setDocuments(List documents) {
        this.documents = documents;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
