package com.imcode.imcms.flow;

import com.imcode.imcms.servlet.GetDoc;
import com.imcode.imcms.flow.CreateDocumentPageFlow;
import imcode.server.document.FormerExternalDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateFormerExternalDocumentPageFlow extends CreateDocumentPageFlow {

    public CreateFormerExternalDocumentPageFlow( TextDocumentDomainObject parentDocument, int parentMenuIndex,
                                                 FormerExternalDocumentDomainObject document ) {
        super( parentDocument, parentMenuIndex, document );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
    }

    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        saveDocument( request );
        GetDoc.redirectToExternalDocumentTypeWithAction( getDocument(), response, "new" );
    }
}
