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

    /**
     * Set the current sort order of the menus in this textdocument.
     *
     * @param sortOrder One of {@link TextDocument.Menu.SORT_BY_HEADLINE},
     *                  {@link TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING},
     *                  or {@link TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING}.
     * @throws NoPermissionException if the current user lacks permission to edit this document.
     */
    public void setMenuSortOrder(int sortOrder) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setMenuSortOrder(sortOrder);
    }

    /**
     * Get the current sort order of the menus in this textdocument.
     *
     * @return the current sort order of the menus in this textdocument,
     *                  one of {@link TextDocument.Menu.SORT_BY_HEADLINE},
     *                  {@link TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING},
     *                  or {@link TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING}.
     */
    public int getMenuSortOrder() throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        return internalDocument.getMenuSortOrder() ;
    }

    /**
     * Get the menu with the given index in the document.
     * @param menuIndexInDocument the index of the menu in the document.
     * @return the menu with the given index in the document.
     * @throws NoPermissionException if you lack permission to read this document.
     */
    public Menu getMenu( int menuIndexInDocument ) throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        return new Menu(menuIndexInDocument) ;
    }

    public static class TextField {
        TextDocumentTextDomainObject imcmsText ;

        private TextField (TextDocumentTextDomainObject imcmsText) {
            this.imcmsText = imcmsText ;
        }

        /**
         * Set the format of the text in this textfield to HTML. (Should not be html-formatted.)
         */
        public void setHtmlFormat() {
            this.imcmsText.setType(TextDocumentTextDomainObject.TEXT_TYPE_HTML) ;
        }

        /**
         *  Set the format of the text in this textfield to plain text. (Should be html-formatted.)
         */
        public void setPlainFormat() {
            this.imcmsText.setType(TextDocumentTextDomainObject.TEXT_TYPE_PLAIN) ;
        }

        /**
         * Get the text of this textfield.
         *
         * @return the text of this textfield.
         */
        public String getText() {
            return imcmsText.getText() ;
        }

        /**
         * Get the text of this textfield as a html-formatted string,
         * suitable for displaying in a html-page.
         *
         * @return the text of this textfield as a html-formatted string, suitable for displaying in a html-page.
         */
        public String getHtmlFormattedText() {
            return imcmsText.toHtmlString() ;
        }
    }

    public class Menu {
        private int menuIndex ;
        /** Menu sorted by 'manual' order. **/
        public final static int SORT_BY_MANUAL_ORDER_DESCENDING    = 2 ;
        /** Menu sorted by headline. **/
        public final static int SORT_BY_HEADLINE        = 1 ;
        /** Menu sorted by datetime. **/
        public final static int SORT_BY_MODIFIED_DATETIME_DESCENDING        = 3 ;

        private Menu( int menuIndex ) {
            this.menuIndex = menuIndex;
        }

        /**
         * Get the documents in this menu.
         *
         * @return an array of the documents in this menu.
         */
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

        /**
         * Add a document to the menu.
         *
         * @param document the document to add
         * @throws NoPermissionException If you lack permission to edit the menudocument or permission to add the document.
         */
        public void addDocument(Document document) throws NoPermissionException {
            securityChecker.hasEditPermission(getId());
            securityChecker.hasSharePermission(document) ;

            documentMapper.addDocumentToMenu( securityChecker.getCurrentLoggedInUser(), getId(),menuIndex,document.getId()) ;
        }

        /**
         * Remove a document from the menu.
         *
         * @param document the document to add
         * @throws NoPermissionException If you lack permission to edit the menudocument.
         */
        public void removeDocument(Document document) throws NoPermissionException {
            securityChecker.hasEditPermission(getId());

            documentMapper.removeDocumentFromMenu(securityChecker.getCurrentLoggedInUser(), getId(), menuIndex, document.getId()) ;

        }
    }

}