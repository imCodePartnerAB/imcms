package com.imcode.imcms.api;

import imcode.server.document.*;
import imcode.server.user.UserAndRoleMapper;
import imcode.server.*;

import java.util.*;

public class TextDocument extends Document {

    TextDocument(DocumentDomainObject document, SecurityChecker securityChecker, DocumentService documentService, DocumentMapper documentMapper, DocumentPermissionSetMapper documentPermissionSetMapper, UserAndRoleMapper userAndRoleMapper) {
        super(document, securityChecker, documentService, documentMapper, documentPermissionSetMapper, userAndRoleMapper);
    }

    public TextField getTextField(int textFieldIndexInDocument) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        TextDocumentTextDomainObject imcmsText = documentMapper.getTextField(internalDocument, textFieldIndexInDocument);
        TextField textField = new TextField(imcmsText, this);
        return textField;
    }

    public void setPlainTextField(int textFieldIndexInDocument, String newText) throws NoPermissionException {
        setTextField(textFieldIndexInDocument, newText, TextDocumentTextDomainObject.TEXT_TYPE_PLAIN);
    }

    public void setHtmlTextField(int textFieldIndexInDocument, String newText) throws NoPermissionException {
        setTextField(textFieldIndexInDocument, newText, TextDocumentTextDomainObject.TEXT_TYPE_HTML);
    }

    private void setTextField(int textFieldIndexInDocument, String newText, int textType) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        TextDocumentTextDomainObject imcmsText = new TextDocumentTextDomainObject(newText, textType);
        this.documentMapper.saveText(imcmsText,
                internalDocument, textFieldIndexInDocument,
                super.securityChecker.getCurrentLoggedInUser(),
                String.valueOf(textType));
    }

    public Template getTemplate() {
        TemplateDomainObject template = internalDocument.getTemplate();
        Template result = new Template(template);
        return result;
    }

    public void setTemplate(Template newTemplate) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        // todo: check if the template is alowed to be set on this document
        TemplateDomainObject internalTemplate = newTemplate.getInternal();
        internalDocument.setTemplate(internalTemplate);
    }

    public Document getInclude(int includeIndexInDocument) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        Map includedDocumentIds = documentMapper.getIncludedDocuments(internalDocument);
        Integer includedDocumentMetaId = (Integer) includedDocumentIds.get(new Integer(includeIndexInDocument));
        if (null != includedDocumentMetaId) {
            DocumentDomainObject includedDocument = documentMapper.getDocument(includedDocumentMetaId.intValue());
            if (null != includedDocument ) {
                return documentService.createDocumentOfSubtype( includedDocument );
            }
        }
        return null;
    }

    public void setInclude(int includeIndexInDocument, TextDocument documentToBeIncluded) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        if (null == documentToBeIncluded) {
            documentMapper.removeInclusion(this.getId(), includeIndexInDocument);
        } else {
            documentMapper.setInclude(this.getId(), includeIndexInDocument, documentToBeIncluded.getId());
        }
    }

    /**
     * Set the current sort order of the menus in this textdocument.
     *
     * @param sortOrder One of {@link TextDocument.Menu.SORT_BY_HEADLINE},
     *                  {@link TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING},
     *                  or {@link TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING}.
     * @throws NoPermissionException if the current user lacks permission to edit this owner.
     */
    public void setMenuSortOrder(int sortOrder) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        internalDocument.setMenuSortOrder(sortOrder);
    }

    /**
     * Get the current sort order of the menus in this textdocument.
     *
     * @return the current sort order of the menus in this textdocument,
     *         one of {@link TextDocument.Menu.SORT_BY_HEADLINE},
     *         {@link TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING},
     *         or {@link TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING}.
     */
    public int getMenuSortOrder() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        return internalDocument.getMenuSortOrder();
    }

    /**
     * Get the menu with the given index in the owner.
     *
     * @param menuIndexInDocument the index of the menu in the owner.
     * @return the menu with the given index in the owner.
     * @throws NoPermissionException if you lack permission to read this owner.
     */
    public Menu getMenu(int menuIndexInDocument) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        return new Menu(this, menuIndexInDocument, securityChecker);
    }

    public void setLanguage(Language language) {
        internalDocument.setLanguageIso639_2(language.getIsoCode639_2());
    }

    public static class TextField {
        TextDocumentTextDomainObject imcmsText;
        private TextDocument document;

        private TextField(TextDocumentTextDomainObject imcmsText, TextDocument document) {
            this.imcmsText = imcmsText;
            this.document = document;
        }

        /**
         * Set the format of the text in this textfield to HTML. (Should not be html-formatted.)
         */
        public void setHtmlFormat() throws NoPermissionException {
            document.securityChecker.hasEditPermission(document.getId());
            this.imcmsText.setType(TextDocumentTextDomainObject.TEXT_TYPE_HTML);
        }

        /**
         * Set the format of the text in this textfield to plain text. (Should be html-formatted.)
         */
        public void setPlainFormat() throws NoPermissionException {
            document.securityChecker.hasEditPermission(document.getId());
            this.imcmsText.setType(TextDocumentTextDomainObject.TEXT_TYPE_PLAIN);
        }

        /**
         * Get the text of this textfield.
         *
         * @return the text of this textfield.
         */
        public String getText() throws NoPermissionException {
            document.securityChecker.hasAtLeastDocumentReadPermission(document);
            if (null != imcmsText) {
                return imcmsText.getText();
            } else {
                return "";
            }
        }

        /**
         * Get the text of this textfield as a html-formatted string,
         * suitable for displaying in a html-page.
         *
         * @return the text of this textfield as a html-formatted string, suitable for displaying in a html-page.
         */
        public String getHtmlFormattedText() throws NoPermissionException {
            document.securityChecker.hasAtLeastDocumentReadPermission(document);
            if (null != imcmsText) {
                return imcmsText.toHtmlString();
            } else {
                return "";
            }
        }
    }

    public class MenuItem {
        MenuItemDomainObject internalMenuItem;
        Document child;

        public MenuItem(MenuItemDomainObject internalMenuItem) {
            this.internalMenuItem = internalMenuItem;
            child = documentService.createDocumentOfSubtype( internalMenuItem.getDocument() );
        }

        public Document getDocument() {
            return child;
        }

        public int getManualNumber() {
            return internalMenuItem.getManualNumber();
        }

        public TreeKey getTreeKey() {
            return new TreeKey(internalMenuItem.getTreeKey());
        }

        public class TreeKey {
            TreeKeyDomainObject internalTreeKey;

            public TreeKey(TreeKeyDomainObject internalTreeKey) {
                this.internalTreeKey = internalTreeKey;
            }

            public int getLevelCount() {
                return internalTreeKey.getLevelCount();
            }

            /**
             * @param level The level in this three key that you want the sort number from.
             *              If the tree key is 1.3.5 then the level key on level 2 is 3.
             * @return the key on the level requested. Throws a NoSuchElementException() if there is none.
             */
            public int getLevelKey(int level) {
                return internalTreeKey.getLevelKey(level);
            }

            public String toString() {
                return internalTreeKey.toString();
            }
        }
    }

    public class Menu {
        /**
         * Menu sorted by headline. *
         */
        public final static int SORT_BY_HEADLINE = IMCConstants.MENU_SORT_BY_HEADLINE;
        /**
         * Menu sorted by 'manual' order. *
         */
        public final static int SORT_BY_MANUAL_ORDER_DESCENDING = IMCConstants.MENU_SORT_BY_MANUAL_ORDER;
        /**
         * Menu sorted by datetime. *
         */
        public final static int SORT_BY_MODIFIED_DATETIME_DESCENDING = IMCConstants.MENU_SORT_BY_DATETIME;
        /**
         * Menu sorted by tree sort order
         */
        public final static int SORT_BY_TREE_ORDER_DESCENDING = IMCConstants.MENU_SORT_BY_MANUAL_TREE_ORDER;

        private MenuDomainObject internalMenu;
        private SecurityChecker securityChecker;

        private Menu(TextDocument owner, int menuIndex, SecurityChecker securityChecker) {
            internalMenu = new MenuDomainObject(owner.internalDocument, menuIndex, owner.documentMapper);
            this.securityChecker = securityChecker;
        }

        public MenuItem[] getMenuItems() throws NoPermissionException {
            MenuItemDomainObject[] menuItemsDomainObjects = internalMenu.getMenuItems();
            MenuItem[] menuItems = new MenuItem[menuItemsDomainObjects.length];
            for (int i = 0; i < menuItemsDomainObjects.length; i++) {
                MenuItemDomainObject menuItemDomainObject = menuItemsDomainObjects[i];
                menuItems[i] = new MenuItem(menuItemDomainObject);
            }
            return menuItems;
        }

        /**
         * Add a document to the menu.
         *
         * @param documentToAdd the document to add
         * @throws NoPermissionException          If you lack permission to edit the menudocument or permission to add the owner.
         * @throws DocumentAlreadyInMenuException If the owner already is in the menu.
         */
        public void addDocument(Document documentToAdd) throws NoPermissionException, DocumentAlreadyInMenuException {
            securityChecker.hasEditPermission(documentToAdd.getId());
            securityChecker.hasSharePermission(documentToAdd);
            try {
                documentMapper.addDocumentToMenu(securityChecker.getCurrentLoggedInUser(), internalMenu.getOwnerDocument().getMetaId(), internalMenu.getMenuIndex(), documentToAdd.getId());
            } catch (DocumentMapper.DocumentAlreadyInMenuException e) {
                throw new DocumentAlreadyInMenuException("Menu " + internalMenu.getMenuIndex() + " of owner " +
                        internalMenu.getOwnerDocument().getMetaId() + " already contains owner " + documentToAdd.getId());
            }
        }

        /**
         * Remove a document from the menu.
         *
         * @param documentToRemove the document to remove
         * @throws NoPermissionException If you lack permission to edit the menudocument.
         */
        public void removeDocument(Document documentToRemove) throws NoPermissionException {
            securityChecker.hasEditPermission(documentToRemove.getId());
            documentMapper.removeDocumentFromMenu(securityChecker.getCurrentLoggedInUser(),
                    internalMenu.getOwnerDocument().getMetaId(),
                    internalMenu.getMenuIndex(),
                    documentToRemove.getId());

        }

        public Document[] getDocuments() {
            MenuItemDomainObject[] menuItemDomainObjects = documentMapper.getMenuItemsForDocument(internalDocument.getMetaId(), internalMenu.getMenuIndex());
            Document[] documents = new Document[menuItemDomainObjects.length];
            for (int i = 0; i < menuItemDomainObjects.length; i++) {
                MenuItemDomainObject menuItemDomainObject = menuItemDomainObjects[i];
                documents[i] = documentService.createDocumentOfSubtype(menuItemDomainObject.getDocument());
            }
            return documents;
        }
    }
}