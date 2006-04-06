package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class CreateDocumentPageFlow extends DocumentPageFlow {

    protected EditDocumentInformationPageFlow editDocumentInformationPageFlow;

    protected CreateDocumentPageFlow( DocumentDomainObject document,
                                      DispatchCommand returnCommand, SaveDocumentCommand saveDocumentCommand ) {
        super(returnCommand, saveDocumentCommand);
        editDocumentInformationPageFlow = new EditDocumentInformationPageFlow( document, returnCommand, saveDocumentCommand );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        editDocumentInformationPageFlow.dispatchToFirstPage( request, response );
    }

    public DocumentDomainObject getDocument() {
        return editDocumentInformationPageFlow.getDocument();
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
                dispatchReturn( request, response );
            }
        } else {
            dispatchOkFromEditPage( request, response );
        }
    }

    protected abstract void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response,
                                                  String page ) throws IOException, ServletException;

    protected abstract void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException;

    protected abstract void dispatchOkFromDocumentInformation( HttpServletRequest request,
                                                               HttpServletResponse response ) throws IOException, ServletException;

}
