package com.imcode.imcms.flow;

import imcode.server.document.textdocument.TextDocumentDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.imcode.imcms.flow.EditDocumentPageFlow;

public class CreateDocumentWithEditPageFlow extends CreateDocumentPageFlow {

    private EditDocumentPageFlow editDocumentPageFlow;

    public CreateDocumentWithEditPageFlow( TextDocumentDomainObject parentDocument, int parentMenuIndex,
                                           EditDocumentPageFlow editDocumentPageFlow ) {
        super( parentDocument, parentMenuIndex, editDocumentPageFlow.getDocument() );
        this.editDocumentPageFlow = editDocumentPageFlow;
    }

    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        editDocumentPageFlow.dispatchToFirstPage( request, response );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        editDocumentPageFlow.dispatchFromPage( request, response, page );
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        editDocumentPageFlow.dispatchOkFromEditPage( request, response );
        saveDocument( request );
        dispatchCancel( request, response );
    }

}
