package com.imcode.imcms.api;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public abstract class SearchQuery {

    private Sort sort ;
    private boolean logged;

    abstract Query getQuery() ;
    
    Sort getSort() {
        return sort ;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
    
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    boolean isLogged() {
        return logged;
    }

}