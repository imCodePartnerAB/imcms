package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DefaultQueryParser;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.QueryParser;
import imcode.util.HttpSessionUtils;
import imcode.util.Utility;
import imcode.util.LocalizedMessage;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.collections.SetUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

public class DocumentFinder extends WebComponent {

    private SelectDocumentCommand selectDocumentCommand;
    private Query restrictingQuery;
    private QueryParser queryParser = new DefaultQueryParser();
    private Set extraSearchResultColumns = SetUtils.orderedSet( new HashSet() ) ;

    public void selectDocument( DocumentDomainObject selectedDocument, HttpServletRequest request,
                                HttpServletResponse response ) throws IOException, ServletException {
        selectDocumentCommand.selectDocument( selectedDocument, request, response );
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        SearchDocumentsPage page = new SearchDocumentsPage();
        page.setDocumentFinder(this);
        forwardWithPage( page, request, response );
    }

    void forwardWithPage( SearchDocumentsPage page, HttpServletRequest request,
                          HttpServletResponse response ) throws IOException, ServletException {
        ImcmsServices service = Imcms.getServices();
        DocumentIndex index = service.getDocumentMapper().getDocumentIndex();
        BooleanQuery booleanQuery = new BooleanQuery();
        if ( null != page.getQuery() ) {
            booleanQuery.add( page.getQuery(), true, false );
        }
        if ( null != restrictingQuery ) {
            booleanQuery.add( restrictingQuery, true, false );
        }
        if ( booleanQuery.getClauses().length > 0 ) {
            page.setDocumentsFound( index.search( booleanQuery, Utility.getLoggedOnUser( request ) ) );
        }
        page.forward( request, response );
    }

    public boolean isDocumentsSelectable() {
        return null != selectDocumentCommand;
    }

    public void setSelectDocumentCommand( SelectDocumentCommand selectDocumentCommand ) {
        this.selectDocumentCommand = selectDocumentCommand;
    }

    public void setRestrictingQuery( Query restrictingQuery ) {
        this.restrictingQuery = restrictingQuery;
    }

    public Query getRestrictingQuery() {
        return restrictingQuery;
    }

    public void setQueryParser( QueryParser queryParser ) {
        this.queryParser = queryParser;
    }

    public Query parse( String queryString ) throws ParseException {
        return queryParser.parse( queryString );
    }

    public void addExtraSearchResultColumn( SearchResultColumn searchResultColumn ) {
        extraSearchResultColumns.add(searchResultColumn) ;
    }

    public SearchResultColumn[] getExtraSearchResultColumns() {
        return (SearchResultColumn[])extraSearchResultColumns.toArray( new SearchResultColumn[extraSearchResultColumns.size()] );
    }

    public interface SelectDocumentCommand {

        void selectDocument( DocumentDomainObject document, HttpServletRequest request,
                             HttpServletResponse response ) throws IOException, ServletException;
    }

    public interface SearchResultColumn {

        String render( DocumentDomainObject document, HttpServletRequest request ) ;

        LocalizedMessage getName();
    }
}
