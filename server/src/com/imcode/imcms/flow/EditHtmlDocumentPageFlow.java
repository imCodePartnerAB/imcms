package com.imcode.imcms.flow;

import imcode.server.Imcms;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditHtmlDocumentPageFlow extends EditDocumentPageFlow {

    private final static String URL_I15D_PAGE__HTMLDOC = "/jsp/docadmin/html_document.jsp";
    public static final String REQUEST_PARAMETER__HTML_DOC__HTML = "html";

    public EditHtmlDocumentPageFlow( HtmlDocumentDomainObject document, DispatchCommand returnCommand,
                                     SaveDocumentCommand saveDocumentCommand ) {
        super( document, returnCommand, saveDocumentCommand );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        HtmlDocumentDomainObject htmlDocument = (HtmlDocumentDomainObject)document;
        htmlDocument.setHtml( request.getParameter( REQUEST_PARAMETER__HTML_DOC__HTML ) );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__HTMLDOC ).forward( request, response );
    }

}
