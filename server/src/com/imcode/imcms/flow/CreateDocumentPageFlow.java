package com.imcode.imcms.flow;

import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class CreateDocumentPageFlow extends DocumentPageFlow {

    protected TextDocumentDomainObject parentDocument;
    protected int parentMenuIndex;
    protected EditDocumentInformationPageFlow editDocumentInformationPageFlow;

    protected CreateDocumentPageFlow( final TextDocumentDomainObject parentDocument,
                                      final int parentMenuIndex, DocumentDomainObject document ) {
        this.parentDocument = parentDocument;
        this.parentMenuIndex = parentMenuIndex;
        editDocumentInformationPageFlow = new EditDocumentInformationPageFlow( document );
    }

    protected void dispatchCancel( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.sendRedirect( "AdminDoc?meta_id=" + parentDocument.getId() + "&flags="
                               + IMCConstants.DISPATCH_FLAG__EDIT_MENU + "&editmenu=" + parentMenuIndex );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        editDocumentInformationPageFlow.dispatchToFirstPage( request, response );
    }

    public DocumentDomainObject getDocument() {
        return editDocumentInformationPageFlow.getDocument();
    }

    protected void saveDocument( HttpServletRequest request ) {
        final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        UserDomainObject user = Utility.getLoggedOnUser( request );
        documentMapper.saveNewDocumentAndAddToMenu( getDocument(), user, parentDocument, parentMenuIndex );
    }

    protected void dispatchFromPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        editDocumentInformationPageFlow.dispatchFromEditPage( request, response, page );
        if ( !response.isCommitted() ) {
            dispatchFromEditPage( request, response, page );
        }
    }

    protected void dispatchOk( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        if ( EditDocumentInformationPageFlow.PAGE__DOCUMENT_INFORMATION.equals( page ) ) {
            editDocumentInformationPageFlow.dispatchOkFromEditPage( request, response );
            if ( !response.isCommitted() ) {
                dispatchOkFromDocumentInformation( request, response );
            }
            if ( !response.isCommitted() ) {
                dispatchCancel( request, response );
            }
        } else {
            dispatchOkFromEditPage( request, response );
        }
    }

    protected abstract void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response,
                                                  String page ) throws IOException, ServletException;

    protected abstract void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException;

    protected abstract void dispatchOkFromDocumentInformation( HttpServletRequest request,
                                                               HttpServletResponse response ) throws IOException, ServletException;
}
