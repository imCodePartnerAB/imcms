package com.imcode.imcms.api;

import imcode.server.document.index.DocumentQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * Base class for Lucene based document search queries, used with {@link DocumentService#getDocuments(SearchQuery)}
 */
public abstract class SearchQuery implements DocumentQuery {

    private Sort sort;
    private boolean logged;

    /**
     * Returns the query
     *
     * @return query
     */
    public abstract Query getQuery();

    public Sort getSort() {
        return sort;
    }

    /**
     * Sets sorting of for this query
     *
     * @param sort sorting for this query
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * Tests whether this query should be logged
     *
     * @return true if this query is set to be logged, false otherwise
     */
    public boolean isLogged() {
        return logged;
    }

    /**
     * Sets whether this query should be logged
     *
     * @param logged true if the query should be logged, false otherwise
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

}