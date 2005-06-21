package com.imcode.imcms.flow;

import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.commons.lang.UnhandledException;

public class CreateDocumentWithEditPageFlow extends CreateDocumentPageFlow {

    private EditDocumentPageFlow editDocumentPageFlow;

    public CreateDocumentWithEditPageFlow( EditDocumentPageFlow editDocumentPageFlow ) {
        super( editDocumentPageFlow.getDocument(), editDocumentPageFlow.returnCommand, editDocumentPageFlow.saveDocumentCommand );
        this.editDocumentPageFlow = editDocumentPageFlow;
    }

    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        editDocumentPageFlow.dispatchToFirstPage( request, response );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        editDocumentPageFlow.dispatchFromPage( request, response, page );
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        editDocumentPageFlow.dispatchOkFromEditPage( request, response );
        try {
            saveDocumentAndReturn( request, response );
        } catch ( NoPermissionToEditDocumentException e ) {
            throw new ShouldHaveCheckedPermissionsEarlierException(e);
        }
    }

}
