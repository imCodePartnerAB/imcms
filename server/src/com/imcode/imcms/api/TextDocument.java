package com.imcode.imcms.api;

import imcode.server.document.*;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.NoPermissionException;
import com.imcode.imcms.api.SecurityChecker;
import com.imcode.imcms.api.Template;

import java.util.Map;

public class TextDocument extends Document {

    TextDocument( SecurityChecker securityChecker, DocumentService documentService, DocumentDomainObject document, DocumentMapper documentMapper, DocumentPermissionSetMapper permissionSetMapper ) {
        super( securityChecker, documentService, document, documentMapper, permissionSetMapper );
    }

    public TextField getTextField( int textFieldIndexInDocument ) throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        TextDocumentTextDomainObject imcmsText = documentMapper.getTextField( internalDocument, textFieldIndexInDocument ) ;
        TextField textField = new TextField(imcmsText) ;
        return textField;
    }

    public void setPlainTextField( int textFieldIndexInDocument, String newText )  throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        setTextField( textFieldIndexInDocument, newText, TextDocumentTextDomainObject.TEXT_TYPE_PLAIN );
    }

    public void setHtmlTextField( int textFieldIndexInDocument, String newText )  throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        setTextField( textFieldIndexInDocument, newText, TextDocumentTextDomainObject.TEXT_TYPE_HTML );
    }

    private void setTextField( int textFieldIndexInDocument, String newText, int textType ) {
        TextDocumentTextDomainObject imcmsText = new TextDocumentTextDomainObject( newText, textType );
        this.documentMapper.saveText(
            imcmsText,
            internalDocument.getMetaId(),
            textFieldIndexInDocument,
            super.securityChecker.getCurrentLoggedInUser(),
            String.valueOf( textType ) );
    }

    public Template getTemplate() {
        TemplateDomainObject template = internalDocument.getTemplate();
        Template result = new Template(template);
        return result;
    }

    public void setTemplate( Template newTemplate ) {
        TemplateDomainObject internalTemplate = newTemplate.getInternal();
        internalDocument.setTemplate( internalTemplate );
    }

    public Document getInclude( int includeIndexInDocument ) throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        Map includedDocumentIds = documentMapper.getIncludedDocuments( internalDocument ) ;
        Integer includedDocumentMetaId = (Integer)includedDocumentIds.get( new Integer( includeIndexInDocument ) );
        if (null != includedDocumentMetaId) {
            DocumentDomainObject includedDocument = documentMapper.getDocument( includedDocumentMetaId.intValue() );
            if( null != includedDocument && DocumentDomainObject.DOCTYPE_TEXT == includedDocument.getDocumentType() ) {
                return new TextDocument( securityChecker, documentService, includedDocument, documentMapper, documentPermissionMapper );
            }
        }
        return null;
    }

    public void setInclude( int includeIndexInDocument, TextDocument documentToBeIncluded ) throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        if (null == documentToBeIncluded) {
            documentMapper.removeInclusion(this.getId(), includeIndexInDocument) ;
        } else {
            documentMapper.setInclude(this.getId(), includeIndexInDocument, documentToBeIncluded.getId()) ;
        }
    }

    public static class TextField {
        TextDocumentTextDomainObject imcmsText ;

        private TextField (TextDocumentTextDomainObject imcmsText) {
            this.imcmsText = imcmsText ;
        }

        public void setHtmlFormat() {
            this.imcmsText.setType(TextDocumentTextDomainObject.TEXT_TYPE_HTML) ;
        }

        public void setPlainFormat() {
            this.imcmsText.setType(TextDocumentTextDomainObject.TEXT_TYPE_PLAIN) ;
        }

        public String getText() {
            return imcmsText.getText() ;
        }

        public String getHtmlFormattedText() {
            return imcmsText.toHtmlString() ;
        }
    }
}