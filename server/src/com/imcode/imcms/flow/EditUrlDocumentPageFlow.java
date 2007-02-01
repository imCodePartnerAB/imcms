package com.imcode.imcms.flow;

import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditUrlDocumentPageFlow extends EditDocumentPageFlow {

    private final static String URL_I15D_PAGE__URLDOC = "/jsp/docadmin/url_document.jsp";
    public static final String REQUEST_PARAMETER__URL_DOC__URL = "url";

    public EditUrlDocumentPageFlow( UrlDocumentDomainObject document, DispatchCommand returnCommand,
                                    SaveDocumentCommand saveDocumentCommand ) {
        super( document, returnCommand, saveDocumentCommand );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
            UrlDocumentDomainObject urlDocument = (UrlDocumentDomainObject)document;
            urlDocument.setUrl( request.getParameter( REQUEST_PARAMETER__URL_DOC__URL ) );
            urlDocument.setTarget( EditDocumentInformationPageFlow.getTargetFromRequest( request, EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET) );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__URLDOC ).forward( request, response );
    }
}
