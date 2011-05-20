package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import com.imcode.imcms.mapping.DocumentMapper;
import org.apache.commons.lang.StringUtils;

/**
 * Edit textdocument in a document.
 */

public class ChangeText extends HttpServlet {

    private static final String JSP__CHANGE_TEXT = "change_text.jsp";

    public void doGet( HttpServletRequest request, HttpServletResponse res ) throws ServletException, IOException {
        Utility.setDefaultHtmlContentType( res );

        UserDomainObject user = Utility.getLoggedOnUser( request );
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        int documentId = Integer.parseInt( request.getParameter( "meta_id" ) );
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)documentMapper.getDocument( documentId );

        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( textDocument );

        if ( !textDocumentPermissionSet.getEditTexts() ) {	// Checking to see if user may edit this
            AdminDoc.adminDoc( documentId, user, request, res, getServletContext() );
            return;
        }

        int textIndex = Integer.parseInt( request.getParameter( "txt" ) );
        String label = null == request.getParameter( "label" ) ? "" : request.getParameter( "label" );

        TextDomainObject text = textDocument.getText( textIndex );
        if ( null == text ) {
            text = new TextDomainObject( "", TextDomainObject.TEXT_TYPE_HTML );
        }
				String[] formats = request.getParameterValues("format") ;
	      String rows = StringUtils.defaultString(request.getParameter("rows")) ;
	      String width = StringUtils.defaultString(request.getParameter("width")) ;
	      String returnUrl = StringUtils.defaultString(request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL)) ;
				TextEditPage page = new TextEditPage( documentId, textIndex, text, label, formats, rows, width, returnUrl );
        page.forward( request, res, user );

    }

    public static class TextEditPage {

        public static final String REQUEST_ATTRIBUTE__PAGE = "page";
        int documentId;
        private int textIndex;
        private String label;
        private TextDomainObject text;
        private String[] formats;
        private String rows;
        private String width;
        private String returnUrl;

        public enum Format {
            NONE,
            TEXT,
            EDITOR
        }
        
        public TextEditPage( int documentId, int textIndex, TextDomainObject text, String label, String[] formats, String rows, String width, String returnUrl ) {
            this.documentId = documentId;
            this.text = text;
            this.textIndex = textIndex;
            this.label = label;
            this.formats = formats;
            this.rows = rows;
            this.width = width;
            this.returnUrl = returnUrl;
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

				public String[] getFormats() {
					return formats;
				}
	
				public String getRows() {
					return rows;
				}
	
				public String getWidth() {
					return width;
				}
	
				public String getReturnUrl() {
					return returnUrl;
				}

				public void forward( HttpServletRequest request, HttpServletResponse response, UserDomainObject user ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
            String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP__CHANGE_TEXT;
            request.getRequestDispatcher( forwardPath ).forward( request, response );
        }

    }

}
