package com.imcode.imcms.api;

import org.apache.lucene.search.Query;

public class LuceneQuery extends SearchQuery {

    private Query query;

    public LuceneQuery(Query query) {
        this.query = query;
    }

    Query getQuery() {
        return query;
    }
    
}
