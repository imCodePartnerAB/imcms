package com.imcode.imcms.servlet;

import imcode.server.document.DocumentDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SearchDocuments extends HttpServlet {

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        DocumentFinder documentFinder = DocumentFinder.getInstance( request ) ;
        SearchDocumentsPage page = SearchDocumentsPage.fromRequest( request );

        if (page.isCancelButtonPressed()) {
            documentFinder.cancel(request,response) ;
        } else if (null != page.getSelectedDocument()) {
            documentFinder.selectDocument( page.getSelectedDocument(), request, response );
        } else {
            documentFinder.forwardWithPage( page, request, response );
        }
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doGet(request, response);
    }

} // End class
