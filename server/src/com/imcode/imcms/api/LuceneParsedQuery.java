package com.imcode.imcms.api;

import imcode.server.document.index.DefaultQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

public class LuceneParsedQuery extends SearchQuery {

    private Query query;

    public LuceneParsedQuery( String queryString ) throws BadQueryException {
        try {
            query = new DefaultQueryParser().parse( queryString );
        } catch ( ParseException e ) {
            throw new BadQueryException( queryString, e );
        }
    }

    Query getQuery() {
        return query;
    }

}