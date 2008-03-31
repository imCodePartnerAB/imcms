package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.mapping.DocumentMapper;

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
        
        // TODO 18n: refactor: where create TextDomObj / here or inside API
        //if ( null == text ) {
        //    text = new TextDomainObject( "", TextDomainObject.TEXT_TYPE_HTML );
        //    text.setLanguage(I18nSupport.getCurrentLanguage());
        //}        
        
        I18nLanguage language = I18nSupport.getCurrentLanguage();
        Meta meta = textDocument.getMeta();
        I18nMeta i18nMeta = meta.getI18nMeta(language);
        boolean enabled = i18nMeta.getEnabled(); 
        
        if (text.isTemporary() && !enabled) {
            TextDao textDao = (TextDao) Imcms.getServices().getSpringBean("textDao");            
        	
        	text = textDao.getText(documentId, textIndex, language.getId());
        	
        	if (text == null) {
        		text = TextDocumentDomainObject.createTemporaryText(
        				meta.getMetaId(), textIndex, language);
        	}
        }
        
        
    	String queryString = request.getQueryString();
    	
    	// TODO 18n: refactor
    	queryString = queryString.replaceFirst("lang=..&?", "");
    	
    	request.setAttribute("queryString", queryString);
    	
        TextEditPage page = new TextEditPage( documentId, textIndex, text, label );
        page.setEnabled(enabled);
        page.setSubstitutedWithDefault(!enabled && meta.getMissingI18nShowRule()
        		== Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE);
        
        page.forward( request, res, user );
    }

    public static class TextEditPage {

        public static final String REQUEST_ATTRIBUTE__PAGE = "page";
        int documentId;
        private int textIndex;
        private String label;
        private TextDomainObject text;
        private boolean enabled;
        private boolean substitutedWithDefault;

        public enum Format {
            NONE,
            TEXT,
            EDITOR
        }
        
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

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isSubstitutedWithDefault() {
			return substitutedWithDefault;
		}

		public void setSubstitutedWithDefault(boolean substitutedWithDefault) {
			this.substitutedWithDefault = substitutedWithDefault;
		}
    }

}
