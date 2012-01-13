/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-09
 * Time: 16:26:49
 */
package com.imcode.imcms.api;

import org.apache.lucene.queryParser.ParseException;

/**
 * Thrown when parse errors are encountered.
 */
public class BadQueryException extends Exception {

    public BadQueryException( String queryString, ParseException cause ) {
        super("Bad query: "+queryString,cause) ;
    }

}