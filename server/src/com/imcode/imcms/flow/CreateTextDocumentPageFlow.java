package com.imcode.imcms.flow;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateTextDocumentPageFlow extends CreateDocumentPageFlow {

    public CreateTextDocumentPageFlow( TextDocumentDomainObject document,
                                       SaveDocumentCommand saveNewDocumentCommand,
                                       DispatchCommand returnCommand ) {
        super( document, returnCommand, saveNewDocumentCommand );
    }

    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)getDocument() ;
        if ( null != request.getParameter( EditDocumentInformationPageFlow.REQUEST_PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS ) ) {
            textDocument.setText( 1, new TextDomainObject( textDocument.getHeadline(), TextDomainObject.TEXT_TYPE_PLAIN ) );
            textDocument.setText( 2, new TextDomainObject( textDocument.getMenuText(), TextDomainObject.TEXT_TYPE_HTML ) );
        }
        saveDocumentAndReturn(request, response);
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
    }
}
