package com.imcode.imcms.api;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public abstract class SearchQuery {

    private Sort sort ;

    abstract Query getQuery() ;
    
    Sort getSort() {
        return sort ;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

}