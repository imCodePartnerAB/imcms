package com.imcode.imcms.servlet;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.util.HttpSessionUtils;
import imcode.util.Utility;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DocumentFinder {

    public static final String REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER = "finder";
    private SelectDocumentCommand selectDocumentCommand;
    private Query restrictingQuery;
    private CancelCommand cancelCommand;

    public static DocumentFinder getInstance( HttpServletRequest request ) {
        DocumentFinder documentFinder = (DocumentFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, DocumentFinder.REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER );
        if ( null == documentFinder ) {
            documentFinder = new DocumentFinder();
        }
        return documentFinder;
    }

    public void selectDocument( DocumentDomainObject selectedDocument, HttpServletRequest request,
                                HttpServletResponse response ) throws IOException, ServletException {
        selectDocumentCommand.selectDocument( selectedDocument, request, response );
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        HttpSessionUtils.setSessionAttributeAndSetNameInRequestAttribute( this, request, DocumentFinder.REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER );
        SearchDocumentsPage page = new SearchDocumentsPage();
        forwardWithPage( page, request, response );
    }

    void forwardWithPage( SearchDocumentsPage page, HttpServletRequest request,
                          HttpServletResponse response ) throws IOException, ServletException {
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
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
        return null != selectDocumentCommand ;
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

    public boolean isCancelable() {
        return null != cancelCommand;
    }

    public void cancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        cancelCommand.cancel( request, response );
    }

    public void setCancelCommand( CancelCommand cancelCommand ) {
        this.cancelCommand = cancelCommand;
    }

    public interface SelectDocumentCommand {

        public void selectDocument( DocumentDomainObject document, HttpServletRequest request,
                                    HttpServletResponse response ) throws IOException, ServletException;
    }

    public interface CancelCommand {
        public void cancel(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
    }
}
