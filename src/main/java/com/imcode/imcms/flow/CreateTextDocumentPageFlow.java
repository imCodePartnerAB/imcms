package com.imcode.imcms.flow;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.Imcms;
import imcode.util.Utility;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.mapping.DocumentSaveException;
import org.apache.commons.lang.UnhandledException;

public class CreateTextDocumentPageFlow extends CreateDocumentPageFlow {

    public CreateTextDocumentPageFlow( TextDocumentDomainObject document,
                                       SaveDocumentCommand saveNewDocumentCommand,
                                       DispatchCommand returnCommand ) {
        super( document, returnCommand, saveNewDocumentCommand );
    }

    // todo: copy non-default language headline and text 
    protected void dispatchOkFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)getDocument() ;

        String parameterName = EditDocumentInformationPageFlow.REQUEST_PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS;

        if (null != request.getParameter( parameterName ) ) {
            textDocument.setText( 1, new TextDomainObject( textDocument.getHeadline(), TextDomainObject.TEXT_TYPE_PLAIN ) );
            textDocument.setText( 2, new TextDomainObject( textDocument.getMenuText(), TextDomainObject.TEXT_TYPE_HTML ) );
        }
                
        saveDocumentAndReturn(request, response);
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) {
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
    }
}