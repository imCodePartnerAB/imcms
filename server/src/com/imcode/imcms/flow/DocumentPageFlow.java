package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.HttpSessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class DocumentPageFlow extends HttpPageFlow {

    protected CreateDocumentPageFlow.SaveDocumentCommand saveDocumentCommand;

    protected DocumentPageFlow( DispatchCommand returnCommand,
                       SaveDocumentCommand saveDocumentCommand ) {
        super( returnCommand );
        this.saveDocumentCommand = saveDocumentCommand;
    }

    public abstract DocumentDomainObject getDocument() ;

    protected void saveDocument( HttpServletRequest request ) throws IOException, ServletException {
        saveDocumentCommand.saveDocument( getDocument(), Utility.getLoggedOnUser( request ) );
    }

    protected void saveDocumentAndReturn( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        saveDocument( request );
        dispatchReturn( request, response );
    }

    public static DocumentPageFlow fromRequest( HttpServletRequest request ) {
        return (DocumentPageFlow)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
    }

    public static interface SaveDocumentCommand {
        void saveDocument( DocumentDomainObject document, UserDomainObject user ) ;
    }
}