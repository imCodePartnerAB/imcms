package com.imcode.imcms.servlet;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SearchDocuments extends HttpServlet {

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        final SearchDocumentsPage page = SearchDocumentsPage.fromRequest( request );
        final DocumentFinder documentFinder = page.getDocumentFinder();

        if ( page.isCancelButtonPressed() ) {
            documentFinder.cancel( request, response );
        } else if ( null != page.getDocumentSelectedForEditing() ) {
            goToEditDocumentInformation( page, documentFinder, request, response );
        } else if ( null != page.getSelectedDocument() ) {
            documentFinder.selectDocument( page.getSelectedDocument(), request, response );
        } else {
            documentFinder.forwardWithPage( page, request, response );
        }
    }

    private void goToEditDocumentInformation( final SearchDocumentsPage page, final DocumentFinder documentFinder,
                                              HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        DocumentDomainObject documentSelectedForEditing = page.getDocumentSelectedForEditing();
        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                documentFinder.forwardWithPage( page, request, response );
            }
        };
        EditDocumentInformationPageFlow editDocumentInformationPageFlow = new EditDocumentInformationPageFlow( documentSelectedForEditing, returnCommand, new DocumentMapper.SaveEditedDocumentCommand() );
        editDocumentInformationPageFlow.setAdminButtonsHidden( true );
        editDocumentInformationPageFlow.dispatch( request, response );
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doGet( request, response );
    }

} // End class
