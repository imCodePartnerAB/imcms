package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.Parser;

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
        ImcmsServices service = Imcms.getServices();
        Utility.setDefaultHtmlContentType( res );

        Writer out = res.getWriter();
        int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        // Check if user has admin rights to edit textfield
        if ( !service.checkDocAdminRights( meta_id, user, 65536 ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }
        ImcmsServices imcref = Imcms.getServices();
        int documentId = Integer.parseInt( req.getParameter( "meta_id" ) );
        int textIndex = Integer.parseInt( req.getParameter( "txt" ) );
        String label = null == req.getParameter( "label" ) ? "" :  req.getParameter( "label" );
        TextDomainObject text = imcref.getText( documentId, textIndex  );
        int type = text.getType();

        String[] tags = {
            "&", "&amp;",
            "<", "&lt;",
            ">", "&gt;"
        };

        String text_string = Parser.parseDoc( text.getText(), tags );
        TextEditPage page = new TextEditPage( documentId, textIndex, text_string, label, type );
        page.forward(req, res, user);

    }

    public static class TextEditPage {

            public static final String REQUEST_ATTRIBUTE__PAGE = "page";
            int documentId;
            private int textIndex;
            private String text_string;
            private String label;
            private int type;

            public TextEditPage( int documentId, int textIndex, String text_string, String label, int type ) {
                this.documentId = documentId;
                this.text_string = text_string;
                this.textIndex = textIndex;
                this.label = label;
                this.type = type;
            }

            public int getDocumentId() {
                return documentId;
            }

            public String getTextString() {
                return text_string;
            }

            public int getTextIndex() {
                return textIndex;
            }

            public String getLabel() {
                return label;
            }

            public int getType() {
                return type;
            }

            public void forward(HttpServletRequest request, HttpServletResponse response, UserDomainObject user) throws IOException, ServletException {
                request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
                String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP__CHANGE_TEXT;
                request.getRequestDispatcher( forwardPath ).forward( request, response );
            }

    }

}
