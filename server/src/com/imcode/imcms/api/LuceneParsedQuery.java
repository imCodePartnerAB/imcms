/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-09
 * Time: 15:42:14
 */
package com.imcode.imcms.api;

import imcode.server.ApplicationServer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

public class LuceneParsedQuery extends SearchQuery {

    private Query query;

    public LuceneParsedQuery( String queryString ) throws BadQueryException {
        try {
            query =
            ApplicationServer.getIMCServiceInterface().getDocumentMapper().getDocumentIndex().parseLucene( queryString );
        } catch ( ParseException e ) {
            throw new BadQueryException( queryString, e );
        }
    }

    Query getQuery() {
        return query;
    }

}