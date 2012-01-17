package com.imcode.imcms.api;

import org.apache.lucene.search.Query;

/**
 * Wraps lucene search {@link Query} into a query acceptable by {@link DocumentService#getDocuments(SearchQuery)}
 */
public class LuceneQuery extends SearchQuery {

    private Query query;

    /**
     * Constructs LuceneQuery from already created {@link Query}
     * @param query {@link Query}
     */
    public LuceneQuery(Query query) {
        this.query = query;
    }

    Query getQuery() {
        return query;
    }
    
}
