package com.imcode.imcms.api;

import imcode.server.IMCText;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.TemplateDomainObject;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.NoPermissionException;
import com.imcode.imcms.api.SecurityChecker;
import com.imcode.imcms.api.Template;

public class TextDocument extends Document {

    TextDocument( SecurityChecker securityChecker, DocumentDomainObject document, DocumentMapper documentMapper, DocumentPermissionSetMapper permissionSetMapper ) {
        super( securityChecker, document, documentMapper, permissionSetMapper );
    }

    public TextField getTextField( int textFieldIndexInDocument ) throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        IMCText imcmsText = documentMapper.getTextField( internalDocument, textFieldIndexInDocument ) ;
        TextField textField = new TextField(imcmsText) ;
        return textField;
    }

    public void setPlainTextField( int textFieldIndexInDocument, String newText )  throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        setTextField( textFieldIndexInDocument, newText, IMCText.TEXT_TYPE_PLAIN );
    }

    public void setHtmlTextField( int textFieldIndexInDocument, String newText )  throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        setTextField( textFieldIndexInDocument, newText, IMCText.TEXT_TYPE_HTML );
    }

    private void setTextField( int textFieldIndexInDocument, String newText, int textType ) {
        IMCText imcmsText = new IMCText( newText, textType );
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

    public static class TextField {
        IMCText imcmsText ;

        private TextField (IMCText imcmsText) {
            this.imcmsText = imcmsText ;
        }

        public void setHtmlFormat() {
            this.imcmsText.setType(IMCText.TEXT_TYPE_HTML) ;
        }

        public void setPlainFormat() {
            this.imcmsText.setType(IMCText.TEXT_TYPE_PLAIN) ;
        }

        public String getText() {
            return imcmsText.getText() ;
        }

        public String getHtmlFormattedText() {
            return imcmsText.toHtmlString() ;
        }
    }

}