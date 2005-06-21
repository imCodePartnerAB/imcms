package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.document.*;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Edit textdocument in a document.
 */

public class ChangeText extends HttpServlet {

    private static final String JSP__CHANGE_TEXT = "change_text.jsp";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        Utility.setDefaultHtmlContentType( res );

        Writer out = res.getWriter();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        int documentId = Integer.parseInt( req.getParameter( "meta_id" ) );
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)documentMapper.getDocument( documentId );

        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( textDocument );

        if ( !textDocumentPermissionSet.getEditTexts() ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( documentId, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        int textIndex = Integer.parseInt( req.getParameter( "txt" ) );
        String label = null == req.getParameter( "label" ) ? "" : req.getParameter( "label" );

        TextDomainObject text = textDocument.getText( textIndex );
        if ( null == text ) {
            text = new TextDomainObject( "", TextDomainObject.TEXT_TYPE_PLAIN );
        }

        TextEditPage page = new TextEditPage( documentId, textIndex, text, label );
        page.forward( req, res, user );

    }

    public static class TextEditPage {

        public static final String REQUEST_ATTRIBUTE__PAGE = "page";
        int documentId;
        private int textIndex;
        private String label;
        private TextDomainObject text;

        public TextEditPage( int documentId, int textIndex, TextDomainObject text, String label ) {
            this.documentId = documentId;
            this.text = text;
            this.textIndex = textIndex;
            this.label = label;
        }

        public int getDocumentId() {
            return documentId;
        }

        public String getTextString() {
            return text.getText();
        }

        public int getTextIndex() {
            return textIndex;
        }

        public String getLabel() {
            return label;
        }

        public int getType() {
            return text.getType();
        }

        public void forward( HttpServletRequest request, HttpServletResponse response, UserDomainObject user ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
            String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP__CHANGE_TEXT;
            request.getRequestDispatcher( forwardPath ).forward( request, response );
        }

    }

}
