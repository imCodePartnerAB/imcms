package com.imcode.imcms.api;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * Base class for Lucene based document search queries, used with {@link DocumentService#getDocuments(SearchQuery)}
 */
public abstract class SearchQuery {

    private Sort sort ;
    private boolean logged;

    /**
     * Returns the query
     * @return query
     */
    abstract Query getQuery() ;
    
    Sort getSort() {
        return sort ;
    }

    /**
     * Sets sorting of for this query
     * @param sort sorting for this query
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * Sets whether this query should be logged
     * @param logged true if the query should be logged, false otherwise
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    /**
     * Tests whether this query should be logged
     * @return true if this query is set to be logged, false otherwise
     */
    boolean isLogged() {
        return logged;
    }

}