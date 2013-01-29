package com.imcode.imcms.flow;

import imcode.server.document.textdocument.TextDocumentDomainObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateTextDocumentPageFlow extends CreateDocumentPageFlow {

    public CreateTextDocumentPageFlow( TextDocumentDomainObject document,
                                       SaveDocumentCommand saveNewDocumentCommand,
                                       DispatchCommand returnCommand ) {
        super( document, returnCommand, saveNewDocumentCommand );
    }


    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        saveDocumentAndReturn(request, response);
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
    }
}