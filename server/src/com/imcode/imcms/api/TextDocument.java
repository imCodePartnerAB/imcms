package com.imcode.imcms.api;

import imcode.server.document.*;

import java.util.Map;

public class TextDocument extends Document {

    TextDocument( DocumentDomainObject document, SecurityChecker securityChecker, DocumentService documentService, DocumentMapper documentMapper, DocumentPermissionSetMapper permissionSetMapper ) {
        super( document, securityChecker, documentService, documentMapper, permissionSetMapper );
    }

    public TextField getTextField( int textFieldIndexInDocument ) throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        TextDocumentTextDomainObject imcmsText = documentMapper.getTextField( internalDocument, textFieldIndexInDocument ) ;
        TextField textField = new TextField(imcmsText) ;
        return textField;
    }

    public void setPlainTextField( int textFieldIndexInDocument, String newText )  throws NoPermissionException {
        setTextField( textFieldIndexInDocument, newText, TextDocumentTextDomainObject.TEXT_TYPE_PLAIN );
    }

    public void setHtmlTextField( int textFieldIndexInDocument, String newText )  throws NoPermissionException {
        setTextField( textFieldIndexInDocument, newText, TextDocumentTextDomainObject.TEXT_TYPE_HTML );
    }

    private void setTextField( int textFieldIndexInDocument, String newText, int textType ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
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

    public void setTemplate( Template newTemplate ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        TemplateDomainObject internalTemplate = newTemplate.getInternal();
        internalDocument.setTemplate( internalTemplate );
    }

    public Document getInclude( int includeIndexInDocument ) throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        Map includedDocumentIds = documentMapper.getIncludedDocuments( internalDocument ) ;
        Integer includedDocumentMetaId = (Integer)includedDocumentIds.get( new Integer( includeIndexInDocument ) );
        if (null != includedDocumentMetaId) {
            DocumentDomainObject includedDocument = documentMapper.getDocument( includedDocumentMetaId.intValue() );
            if( null != includedDocument && DocumentDomainObject.DOCTYPE_TEXT == includedDocument.getDocumentType() ) {
                return new TextDocument( includedDocument, securityChecker, documentService, documentMapper, documentPermissionMapper );
            }
        }
        return null;
    }

    public void setInclude( int includeIndexInDocument, TextDocument documentToBeIncluded ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        if ( null == documentToBeIncluded ) {
            documentMapper.removeInclusion(this.getId(), includeIndexInDocument) ;
        } else {
            documentMapper.setInclude(this.getId(), includeIndexInDocument, documentToBeIncluded.getId()) ;
        }
    }

    public Menu getMenu( int menuIndexInDocument ) throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        return new Menu(menuIndexInDocument) ;
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

    public class Menu {
        private int menuIndex ;

        private Menu( int menuIndex ) {
            this.menuIndex = menuIndex;
        }

        public Document[] getDocuments() {
            String[] documentIds = documentMapper.getMenuLinks(getId(),menuIndex) ;
            Document[] documents = new Document[documentIds.length] ;

            for (int i = 0; i < documentIds.length; i++) {
                String documentId = documentIds[i];
                DocumentDomainObject document = documentMapper.getDocument(Integer.parseInt(documentId)) ;
                if (document.getDocumentType() == DocumentDomainObject.DOCTYPE_TEXT) {
                    documents[i] = new TextDocument( document, securityChecker, documentService, documentMapper, documentPermissionMapper) ;
                } else {
                    documents[i] = new Document( document, securityChecker, documentService, documentMapper, documentPermissionMapper) ;
                }

            }
            return documents ;
        }

        public void addDocument(Document document) throws NoPermissionException {
            securityChecker.hasEditPermission(getInternal().getMetaId());
            securityChecker.hasSharePermission(document) ;

            documentMapper.addDocumentToMenu(getId(),menuIndex,document.getId()) ;

        }
    }

}