/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-09
 * Time: 15:41:15
 */
package com.imcode.imcms.api;

import org.apache.lucene.search.Query;

public abstract class SearchQuery {

    abstract Query getQuery() ;

}