package com.imcode.imcms.api;

import imcode.server.document.index.DefaultQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

/**
 * Document search query. Provides means of query creation from String
 */
public class LuceneParsedQuery extends SearchQuery {

    private Query query;

    /**
     * Constructs Lucene search query from a String
     * @param queryString a string representing a search query
     * @throws BadQueryException if the given string cannot be parsed into query
     */
    public LuceneParsedQuery( String queryString ) throws BadQueryException {
        try {
            query = parse(queryString);
        } catch ( ParseException e ) {
            throw new BadQueryException( queryString, e );
        }
    }

    /**
     * Attempts parsing of a String into Lucene search query
     * @param queryString a string representing a search query
     * @return a {@link Query} parsed from the given String
     * @throws ParseException if the given string cannot be parsed into query
     */
    public static Query parse(String queryString) throws ParseException {
        return new DefaultQueryParser().parse( queryString );
    }

    Query getQuery() {
        return query;
    }

}
