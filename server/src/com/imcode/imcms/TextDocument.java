package com.imcode.imcms;

import imcode.server.IMCText;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;

public class TextDocument extends Document {

    TextDocument( SecurityChecker securityChecker,  imcode.server.document.DocumentDomainObject document, DocumentMapper documentMapper, DocumentPermissionSetMapper permissionSetMapper ) {
        super( securityChecker, document, documentMapper, permissionSetMapper );
    }

    public TextDocument.TextField getTextField( int textFieldIndexInDocument ) throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        IMCText imcmsText = documentMapper.getTextField( internalDocument, textFieldIndexInDocument ) ;
        TextDocument.TextField textField = new TextDocument.TextField(imcmsText) ;
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
            super.securityChecker.getAccessingUser(),
            String.valueOf( textType ) );
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