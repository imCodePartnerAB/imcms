package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditDocumentPermissionsPageFlow extends EditDocumentPageFlow {

    public EditDocumentPermissionsPageFlow( DocumentDomainObject document, DispatchCommand returnCommand,
                                            SaveDocumentCommand saveDocumentCommand ) {
        super( document, returnCommand, saveDocumentCommand );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        DispatchCommand okCommand = new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                saveDocumentAndReturn(request, response);
            }
        };

        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                dispatchReturn( request, response );
            }
        };
        DocumentPermissionsPage documentPermissionsPage = new DocumentPermissionsPage( document, okCommand, returnCommand );
        documentPermissionsPage.forward( request, response );
    }

}
