package com.imcode.imcms.api;

import imcode.server.document.*;
import imcode.server.document.textdocument.*;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserAndRoleMapper;
import imcode.server.*;

import java.util.*;

public class TextDocument extends Document {

    TextDocument(TextDocumentDomainObject document, IMCServiceInterface service, SecurityChecker securityChecker, DocumentService documentService, DocumentMapper documentMapper, DocumentPermissionSetMapper documentPermissionSetMapper, UserAndRoleMapper userAndRoleMapper) {
        super(document, service, securityChecker, documentService, documentMapper, documentPermissionSetMapper, userAndRoleMapper);
    }

    public TextField getTextField(int textFieldIndexInDocument) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        TextDomainObject imcmsText = documentMapper.getTextField(internalDocument, textFieldIndexInDocument);
        TextField textField = new TextField(imcmsText);
        return textField;
    }

    public void setPlainTextField(int textFieldIndexInDocument, String newText) throws NoPermissionException {
        setTextField(textFieldIndexInDocument, newText, TextDomainObject.TEXT_TYPE_PLAIN);
    }

    public void setHtmlTextField(int textFieldIndexInDocument, String newText) throws NoPermissionException {
        setTextField(textFieldIndexInDocument, newText, TextDomainObject.TEXT_TYPE_HTML);
    }

    private void setTextField(int textFieldIndexInDocument, String newText, int textType) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        TextDomainObject imcmsText = new TextDomainObject(newText, textType);
        ((TextDocumentDomainObject)internalDocument).setText( textFieldIndexInDocument, imcmsText);
    }

    /**
     * @deprecated Use {@link #setImage(int, java.lang.String, java.lang.String, int, int, int, int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)} instead. link_target is used instead of link_targetname.
     * @throws NoPermissionException
     */
    public void setImage(int imageIndexInDocument, String image_src, String image_name,
                         int width, int heigth, int border, int v_space, int h_space, String align,
                         String link_target, String link_targetname, String link_href,
                         String alt_text, String low_src) throws NoPermissionException {
        setImage( imageIndexInDocument, image_src, image_name, width, heigth, border, v_space, h_space, align, link_target, link_href, alt_text, low_src );

    }

    public void setImage( int imageIndexInDocument, String image_src, String image_name, int width, int heigth,
                           int border, int v_space,
                           int h_space, String align, String link_target, String link_href, String alt_text,
                           String low_src ) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        imcode.server.document.textdocument.ImageDomainObject internalImage = new ImageDomainObject();

        internalImage.setUrl(image_src); // image srcurl,  relative imageurl
        internalImage.setName(image_name);  // html imagetag name
        internalImage.setWidth(width);
        internalImage.setHeight(heigth);
        internalImage.setBorder(border);
        internalImage.setVerticalSpace(v_space);
        internalImage.setHorizontalSpace(h_space);
        internalImage.setTarget(link_target); // link target
        internalImage.setAlign(align);
        internalImage.setAlternateText(alt_text);
        internalImage.setLowResolutionUrl(low_src);
        internalImage.setLinkUrl(link_href);  // link href
        documentMapper.saveDocumentImage(this.getId(), imageIndexInDocument, internalImage, super.securityChecker.getCurrentLoggedInUser());
    }

    public Image getImage( int imageIndexInDocument ) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        ImageDomainObject imageDomainObject = ((TextDocumentDomainObject)internalDocument).getImage( imageIndexInDocument );
        if( null != imageDomainObject ) {
            return new Image(imageDomainObject, service);
        } else {
            return null;
        }
    }

    public Template getTemplate() {
        TemplateDomainObject template = ((TextDocumentDomainObject)internalDocument).getTemplate();
        Template result = new Template(template);
        return result;
    }

    public void setTemplate(TemplateGroup templateGroup, Template template) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        setTemplateInternal( template );
        ((TextDocumentDomainObject)internalDocument).setTemplateGroupId( templateGroup.getId() );
    }

    /**
     * @deprecated
     * @param template
     * @throws NoPermissionException
     */
    public void setTemplate(Template template) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        // todo: check if the template is alowed to be set on this internalTextDocument
        setTemplateInternal(template);
    }

    private void setTemplateInternal(Template newTemplate) {
        TemplateDomainObject internalTemplate = newTemplate.getInternal();
        ((TextDocumentDomainObject)internalDocument).setTemplate(internalTemplate);
    }

    public Document getInclude(int includeIndexInDocument) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        Integer includedDocumentId = ((TextDocumentDomainObject)internalDocument).getIncludedDocumentId( includeIndexInDocument );
        if (null != includedDocumentId) {
            DocumentDomainObject includedDocument = documentMapper.getDocument(includedDocumentId.intValue());
            if (null != includedDocument ) {
                return documentService.wrapDocumentDomainObject( includedDocument );
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
     * @deprecated Sort order is now per menu. Method Will be removed some time after version 1.8.4.
     */
    public void setMenuSortOrder(int sortOrder) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        //((TextDocumentDomainObject)internalTextDocument).setMenuSortOrder(sortOrder);
    }

    /**
     * Get the current sort order of the menus in this textdocument.
     *
     * @return the current sort order of the menus in this textdocument,
     *         one of {@link TextDocument.Menu.SORT_BY_HEADLINE},
     *         {@link TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING},
     *         or {@link TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING}.
     * @deprecated Sort order is now per menu. Method Will be removed some time after version 1.8.4.
     */
    public int getMenuSortOrder() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        //return ((TextDocumentDomainObject)internalTextDocument).getMenuSortOrder();
        return TextDocument.Menu.SORT_BY_HEADLINE ;
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

    public static class TextField {
        TextDomainObject imcmsText;

        private TextField(TextDomainObject imcmsText) {
            this.imcmsText = imcmsText;
        }

        /**
         * Set the format of the text in this textfield to HTML. (Should not be html-formatted.)
         */
        public void setHtmlFormat() {
            this.imcmsText.setType(TextDomainObject.TEXT_TYPE_HTML);
        }

        /**
         * Set the format of the text in this textfield to plain text. (Should be html-formatted.)
         */
        public void setPlainFormat() {
            this.imcmsText.setType(TextDomainObject.TEXT_TYPE_PLAIN);
        }

        /**
         * Get the text of this textfield.
         *
         * @return the text of this textfield.
         */
        public String getText() {
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
        public String getHtmlFormattedText() {
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
            child = documentService.wrapDocumentDomainObject( internalMenuItem.getDocument() );
        }

        public Document getDocument() {
            return child;
        }

        public int getManualNumber() {
            Integer sortKey = internalMenuItem.getSortKey();
            if (null == sortKey) {
                return 0 ;
            }
            return sortKey.intValue();
        }

        public TreeKey getTreeKey() {
            return new TreeKey(internalMenuItem.getTreeSortKey());
        }

        public class TreeKey {
            TreeSortKeyDomainObject internalTreeSortKey;

            public TreeKey(TreeSortKeyDomainObject internalTreeSortKey) {
                this.internalTreeSortKey = internalTreeSortKey;
            }

            public int getLevelCount() {
                return internalTreeSortKey.getLevelCount();
            }

            /**
             * @param level The level in this three key that you want the sort number from.
             *              If the tree key is 1.3.5 then the level key on level 2 is 3.
             * @return the key on the level requested. Throws a NoSuchElementException() if there is none.
             */
            public int getLevelKey(int level) {
                return internalTreeSortKey.getLevelKey(level - 1);
            }

            public String toString() {
                return internalTreeSortKey.toString();
            }
        }
    }

    public class Menu {
        /**
         * Menu sorted by headline. *
         */
        public final static int SORT_BY_HEADLINE = imcode.server.document.textdocument.MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE;
        /**
         * Menu sorted by 'manual' order. *
         */
        public final static int SORT_BY_MANUAL_ORDER_DESCENDING = imcode.server.document.textdocument.MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER;
        /**
         * Menu sorted by datetime. *
         */
        public final static int SORT_BY_MODIFIED_DATETIME_DESCENDING = imcode.server.document.textdocument.MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME;
        /**
         * Menu sorted by tree sort order
         */
        public final static int SORT_BY_TREE_ORDER_DESCENDING = imcode.server.document.textdocument.MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER;

        private SecurityChecker securityChecker;
        private TextDocumentDomainObject internalTextDocument;
        private int menuIndex;

        private Menu(TextDocument document, int menuIndex, SecurityChecker securityChecker) {
            this.internalTextDocument = (TextDocumentDomainObject)document.internalDocument ;
            this.menuIndex = menuIndex ;
            this.securityChecker = securityChecker;
        }

        public MenuItem[] getMenuItems() throws NoPermissionException {
            MenuItemDomainObject[] menuItemsDomainObjects = internalTextDocument.getMenu( menuIndex ).getMenuItems();
            MenuItem[] menuItems = new MenuItem[menuItemsDomainObjects.length];
            for (int i = 0; i < menuItemsDomainObjects.length; i++) {
                MenuItemDomainObject menuItemDomainObject = menuItemsDomainObjects[i];
                menuItems[i] = new MenuItem(menuItemDomainObject);
            }
            return menuItems;
        }

        /**
         * Add a internalTextDocument to the menu.
         *
         * @param documentToAdd the internalTextDocument to add
         * @throws NoPermissionException          If you lack permission to edit the menudocument or permission to add the owner.
         * @throws DocumentAlreadyInMenuException If the owner already is in the menu.
         */
        public void addDocument(Document documentToAdd) throws NoPermissionException, DocumentAlreadyInMenuException {
            securityChecker.hasEditPermission(documentToAdd.getId());
            securityChecker.userHasPermissionToAddDocumentToAnyMenu(documentToAdd);
            internalTextDocument.getMenu(menuIndex).addMenuItem( new MenuItemDomainObject( documentToAdd.internalDocument ) );
        }

        /**
         * Remove a internalTextDocument from the menu.
         *
         * @param documentToRemove the internalTextDocument to remove
         * @throws NoPermissionException If you lack permission to edit the menudocument.
         */
        public void removeDocument(Document documentToRemove) throws NoPermissionException {
            securityChecker.hasEditPermission(documentToRemove.getId());
            internalTextDocument.getMenu(menuIndex).removeMenuItem( new MenuItemDomainObject( documentToRemove.internalDocument ) );
        }

        public Document[] getDocuments() {
            MenuItemDomainObject[] menuItemDomainObjects = internalTextDocument.getMenu(menuIndex).getMenuItems() ;
            List documentList = new ArrayList() ;
            for (int i = 0; i < menuItemDomainObjects.length; i++) {
                MenuItemDomainObject menuItemDomainObject = menuItemDomainObjects[i];
                DocumentDomainObject documentDomainObject = menuItemDomainObject.getDocument();
                boolean documentIsVisibleInMenu = (documentDomainObject.isPublishedAndNotArchived()
                                    && documentMapper.userHasAtLeastDocumentReadPermission( securityChecker.getCurrentLoggedInUser(), documentDomainObject ))
                                                  || documentMapper.userHasMoreThanReadPermissionOnDocument(securityChecker.getCurrentLoggedInUser(), documentDomainObject );
                if (documentIsVisibleInMenu) {
                    Document document = documentService.wrapDocumentDomainObject(documentDomainObject);
                    documentList.add(document);
                }
            }
            return (Document[])documentList.toArray( new Document[documentList.size()]);
        }
    }
}