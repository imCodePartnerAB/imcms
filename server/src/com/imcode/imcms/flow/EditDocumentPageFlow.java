package com.imcode.imcms.flow;

import imcode.server.ApplicationServer;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class EditDocumentPageFlow extends DocumentPageFlow {

    protected DocumentDomainObject document;

    public DocumentDomainObject getDocument() {
        return document ;
    }

    protected void saveDocument( HttpServletRequest request ) {
            UserDomainObject user = Utility.getLoggedOnUser( request );
            ApplicationServer.getIMCServiceInterface().getDocumentMapper().saveDocument( document, user );
    }

    protected EditDocumentPageFlow( DocumentDomainObject document ) {
        this.document = document ;
    }

    protected void dispatchCancel( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.sendRedirect( "AdminDoc?meta_id=" + document.getId() );
    }

    protected void dispatchFromPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        dispatchFromEditPage( request, response, page );
    }

    protected void dispatchOk( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException {
        dispatchOkFromEditPage( request, response );
        saveDocument( request );
        dispatchCancel( request, response );
    }

    protected abstract void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response,
                                                  String page ) throws IOException, ServletException;

    protected abstract void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException;

}
