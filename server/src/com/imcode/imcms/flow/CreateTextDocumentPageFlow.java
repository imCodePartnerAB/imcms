package com.imcode.imcms.flow;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import com.imcode.imcms.flow.CreateDocumentPageFlow;

public class CreateTextDocumentPageFlow extends CreateDocumentPageFlow {

    public CreateTextDocumentPageFlow(final TextDocumentDomainObject parentDocument,
                                   final int parentMenuIndex, TextDocumentDomainObject document ) {
        super(parentDocument, parentMenuIndex, document);
    }

    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) {
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)getDocument() ;
        if ( null != request.getParameter( EditDocumentInformationPageFlow.REQUEST_PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS ) ) {
            textDocument.setText( 1, new TextDomainObject( textDocument.getHeadline(), TextDomainObject.TEXT_TYPE_PLAIN ) );
            textDocument.setText( 2, new TextDomainObject( textDocument.getMenuText(), TextDomainObject.TEXT_TYPE_PLAIN ) );
        }
        saveDocument(request) ;
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
    }
}
