package com.imcode.imcms.flow;

import com.imcode.imcms.servlet.GetDoc;
import imcode.server.document.FormerExternalDocumentDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateFormerExternalDocumentPageFlow extends CreateDocumentPageFlow {

    public CreateFormerExternalDocumentPageFlow( FormerExternalDocumentDomainObject document,
                                                 SaveDocumentCommand saveNewDocumentCommand,
                                                 DispatchCommand returnCommand ) {
        super( document, saveNewDocumentCommand, returnCommand );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
    }

    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        saveDocument( request );
        GetDoc.redirectToExternalDocumentTypeWithAction( getDocument(), response, "new" );
    }
}
