package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Experimental
public class ChangeDocDefaultVersionPageFlow extends DocumentPageFlow {

    private static final long serialVersionUID = 1203685374461509107L;
    private DocumentDomainObject document;

    public ChangeDocDefaultVersionPageFlow(DocumentDomainObject document, DispatchCommand returnCommand,
                                           SaveDocumentCommand saveDocumentCommand) {
        super(returnCommand, saveDocumentCommand);

        this.document = document;
    }

    @Override
    public DocumentDomainObject getDocument() {
        return document;
    }

    @Override
    protected void dispatchFromPage(HttpServletRequest request,
                                    HttpServletResponse response, String page) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void dispatchOk(HttpServletRequest request,
                              HttpServletResponse response, String page) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void dispatchToFirstPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        saveDocument(request);
        //dispatchReturn( request, response );
        request.getRequestDispatcher("/servlet/GetDoc").forward(request, response);
    }


}