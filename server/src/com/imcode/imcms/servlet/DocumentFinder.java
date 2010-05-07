package com.imcode.imcms.servlet;

import com.imcode.imcms.servlet.admin.Handler;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DefaultQueryParser;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.QueryParser;
import imcode.server.document.index.SimpleDocumentQuery;
import imcode.util.Utility;
import org.apache.commons.collections.SetUtils;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentFinder extends WebComponent {

    private Handler<Integer> selectDocumentCommand;
    private Query restrictingQuery;
    private QueryParser queryParser = new DefaultQueryParser();
    private Set extraSearchResultColumns = SetUtils.orderedSet( new HashSet() ) ;
    private DocumentFinderPage page ;
    private Comparator documentComparator ;
    private boolean logged;

    public DocumentFinder() {
        this(new SearchDocumentsPage());
    }

    public DocumentFinder(DocumentFinderPage page) {
        this.page = page ;
        page.setDocumentFinder(this);
    }

    public void selectDocument(DocumentDomainObject selectedDocument) throws IOException, ServletException {
        selectDocumentCommand.handle( selectedDocument.getId() );
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        forwardWithPage(request, response, page);
    }

    void forwardWithPage(HttpServletRequest request, HttpServletResponse response, DocumentFinderPage page) throws IOException, ServletException {
        ImcmsServices service = Imcms.getServices();
        DocumentIndex index = service.getDocumentMapper().getDocumentIndex();
        final BooleanQuery booleanQuery = new BooleanQuery();
        if ( null != page.getQuery() ) {
            booleanQuery.add( page.getQuery(), true, false );
        }
        if ( null != restrictingQuery ) {
            booleanQuery.add( restrictingQuery, true, false );
        }
        if ( booleanQuery.getClauses().length > 0 ) {
            List documentsFound = index.search(new SimpleDocumentQuery(booleanQuery, null, logged), Utility.getLoggedOnUser( request ) );
            if (null != documentComparator) {
                Collections.sort(documentsFound, documentComparator) ;
            }
            page.setDocumentsFound( documentsFound );
        }
        page.forward( request, response );
    }

    public boolean isDocumentsSelectable() {
        return null != selectDocumentCommand;
    }

    public void setSelectDocumentCommand( Handler<Integer> selectDocumentCommand ) {
        this.selectDocumentCommand = selectDocumentCommand;
    }

    public void setRestrictingQuery( Query restrictingQuery ) {
        this.restrictingQuery = restrictingQuery;
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

    public void setDocumentComparator( Comparator documentComparator ) {
        this.documentComparator = documentComparator;
    }

    public void dispatchReturn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        cancel(request, response);
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public interface SearchResultColumn extends Serializable {

        String render( DocumentDomainObject document, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException;

        LocalizedMessage getName();
    }

}
