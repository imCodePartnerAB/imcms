package com.imcode.imcms.api;

import com.imcode.imcms.api.exception.BadQueryException;
import imcode.server.document.index.DefaultQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

public class LuceneParsedQuery extends SearchQuery {

    private Query query;

    public LuceneParsedQuery(String queryString) throws BadQueryException {
        try {
            query = parse(queryString);
        } catch (ParseException e) {
            throw new BadQueryException(queryString, e);
        }
    }

    public static Query parse(String queryString) throws ParseException {
        return new DefaultQueryParser().parse(queryString);
    }

    Query getQuery() {
        return query;
    }

}
