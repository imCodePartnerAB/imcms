package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class EditDocumentPageFlow extends DocumentPageFlow {

    protected DocumentDomainObject document;
    public final static String URL_I15D_PAGE__PREFIX = "/imcms/";
    public static final String PAGE__EDIT = "edit";

    protected EditDocumentPageFlow( final DocumentDomainObject document, DispatchCommand returnCommand,
                                    SaveDocumentCommand saveDocumentCommand ) {
        super( returnCommand, saveDocumentCommand );
        this.document = document ;
    }

    public DocumentDomainObject getDocument() {
        return document ;
    }

    protected void dispatchFromPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        dispatchFromEditPage( request, response, page );
    }

    protected void dispatchOk( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        dispatchOkFromEditPage( request, response );
        if (!response.isCommitted()) {
            saveDocumentAndReturn( request, response );
        }
    }

    protected abstract void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response,
                                                  String page ) throws IOException, ServletException;

    protected abstract void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException;

}
