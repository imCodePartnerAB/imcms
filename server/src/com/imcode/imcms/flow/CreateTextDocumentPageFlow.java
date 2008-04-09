package com.imcode.imcms.flow;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;

import java.io.IOException;

public class CreateTextDocumentPageFlow extends CreateDocumentPageFlow {

    public CreateTextDocumentPageFlow( TextDocumentDomainObject document,
                                       SaveDocumentCommand saveNewDocumentCommand,
                                       DispatchCommand returnCommand ) {
        super( document, returnCommand, saveNewDocumentCommand );
    }

    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)getDocument() ;
        
        for (I18nLanguage language: I18nSupport.getLanguages()) {
        	String parameterName = EditDocumentInformationPageFlow.REQUEST_PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS
        		+ "_" + language.getCode();
        	
            if ( null != request.getParameter( parameterName ) ) {
                textDocument.setText( language, 1, new TextDomainObject( textDocument.getHeadline(language), TextDomainObject.TEXT_TYPE_PLAIN ) );
                textDocument.setText( language, 2, new TextDomainObject( textDocument.getMenuText(language), TextDomainObject.TEXT_TYPE_HTML ) );
            }
        }
                
        saveDocumentAndReturn(request, response);
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
    }
}
