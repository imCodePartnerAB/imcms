package com.imcode.imcms;

import java.util.Map;
import imcode.server.document.Document ;
import imcode.server.document.DocumentMapper;
import imcode.server.IMCText;

public class TextDocumentBean {
    private SecurityChecker securityChecker;
    private Document document;
    private DocumentMapper mapper;

    TextDocumentBean( SecurityChecker securityChecker,  Document document, DocumentMapper mapper ) {
        this.securityChecker = securityChecker;
        this.document = document;
        this.mapper = mapper;
    }

    /**
     * @return map of rolename String -> {@link DocumentPermissionSet} constants.
     */
    public Map getAllRolesMappedToPermissions() throws NoPermissionException {
        securityChecker.hasEditPermission( document );
        return mapper.getAllRolesMappedToPermissions( document );
    }

    public TextField getTextField( int textFieldIndexInDocument ) throws NoPermissionException {
        securityChecker.hasEditPermission( document );
        IMCText imcmsText = mapper.getTextField( document, textFieldIndexInDocument ) ;
        TextField textField = new TextField(imcmsText) ;
        return textField;
    }

    public class TextField {
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