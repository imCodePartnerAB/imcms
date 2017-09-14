package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Experimental
public class ChangeDocDefaultVersionPageFlow extends DocumentPageFlow {

    private DocumentDomainObject document;

    private UserDomainObject user;

    public ChangeDocDefaultVersionPageFlow(DocumentDomainObject document, DispatchCommand returnCommand,
                                           SaveDocumentCommand saveDocumentCommand, UserDomainObject user) {
        super(returnCommand, saveDocumentCommand);

        this.document = document;
        this.user = user;
    }

    @Override
    public DocumentDomainObject getDocument() {
        return document;
    }

    @Override
    protected void dispatchFromPage(HttpServletRequest request,
                                    HttpServletResponse response, String page) throws IOException,
            ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void dispatchOk(HttpServletRequest request,
                              HttpServletResponse response, String page) throws IOException,
            ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void dispatchToFirstPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        saveDocument(request);
        //dispatchReturn( request, response );
        request.getRequestDispatcher("/servlet/GetDoc").forward(request, response);
    }


}