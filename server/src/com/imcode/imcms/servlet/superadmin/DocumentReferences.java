package com.imcode.imcms.servlet.superadmin;

import imcode.server.user.UserDomainObject;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.Imcms;
import imcode.util.Utility;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class DocumentReferences extends HttpServlet {

    public static final String REQUEST_PARAMETER__REFERENCED_DOCUMENT_ID = "id";
    public static final String REQUEST_PARAMETER__RETURNURL = "returnurl";
    public static final String REQUEST_PARAMETER__BUTTON_RETURN = "return";
    public static final String REQUEST_ATTRIBUTE__DOCUMENT_MENU_PAIRS = "documentMenuPairs";

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( request );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( request, response );
            return;
        }

        if (null != request.getParameter( REQUEST_PARAMETER__BUTTON_RETURN )) {
            request.getRequestDispatcher( request.getParameter( REQUEST_PARAMETER__RETURNURL )).forward( request, response );
            return ;
        }

        if ( null != request.getParameter( REQUEST_PARAMETER__REFERENCED_DOCUMENT_ID ) ) {
            forwardToDocumentReferencesPage( request, response, user );
            return ;
        }

    }

    private void forwardToDocumentReferencesPage( HttpServletRequest request, HttpServletResponse response,
                                                     UserDomainObject user ) throws IOException, ServletException {
        int documentId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__REFERENCED_DOCUMENT_ID ) );
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( documentId );
        DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairs = documentMapper.getDocumentMenuPairsContainingDocument( document );
        request.setAttribute( REQUEST_ATTRIBUTE__DOCUMENT_MENU_PAIRS, documentMenuPairs );
        request.getRequestDispatcher( "/imcms/" + user.getLanguageIso639_2() + "/jsp/document_references.jsp" ).forward( request, response );
    }

}
