package com.imcode.imcms.api;

import imcode.server.document.*;
import imcode.server.document.textdocument.*;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserAndRoleMapper;
import imcode.server.*;

import java.util.*;

import org.apache.commons.collections.*;
import org.apache.commons.collections.functors.CloneTransformer;
import org.apache.commons.collections.map.TransformedSortedMap;
import org.apache.commons.lang.StringUtils;

public class TextDocument extends Document {

    TextDocument(TextDocumentDomainObject document, IMCServiceInterface service, SecurityChecker securityChecker, DocumentService documentService, DocumentMapper documentMapper, DocumentPermissionSetMapper documentPermissionSetMapper, UserAndRoleMapper userAndRoleMapper) {
        super(document, service, securityChecker, documentService, documentMapper, documentPermissionSetMapper, userAndRoleMapper);
    }

    /**
     * @return A SortedMap that contains the textFileds index as keys, and instaces of TextFields as values. Only the
     *         TextFields that contains any text is returned.
     */
    public SortedMap getTextFields() {
        Predicate predicate = new Predicate() {
            public boolean evaluate(Object o) {
                Map.Entry entry = (Map.Entry) o;
                TextDomainObject tempTextField = (TextDomainObject) entry.getValue();
                return StringUtils.isNotEmpty(tempTextField.getText());
            }
        };

        Transformer fromDomainToAPITransformer = new Transformer() {
            public Object transform(Object o) {
                TextDomainObject tempTextField = (TextDomainObject) o;
                return new TextField(tempTextField);
            }
        };

        Map textFieldsMap = getInternalTextDocument().getTexts();

        return filterAndConvertValues(textFieldsMap, predicate, fromDomainToAPITransformer);
    }

    /**
     * @return A SortedMap that contains the images index as keys, and instaces of Image as values. Only the
     *         Image that has an url is returned.
     */
    public SortedMap getImages() {
        Predicate predicate = new Predicate() {
            public boolean evaluate(Object o) {
                Map.Entry entry = (Map.Entry) o;
                ImageDomainObject tempImage = (ImageDomainObject) entry.getValue();
                return StringUtils.isNotBlank(tempImage.getUrl());
            }
        };

        Transformer fromDomainToAPITransformer = new Transformer() {
            public Object transform(Object o) {
                ImageDomainObject tempImage = (ImageDomainObject) o;
                return new Image(tempImage);
            }
        };

        Map imagesMap = getInternalTextDocument().getImages();

        return filterAndConvertValues(imagesMap, predicate, fromDomainToAPITransformer);

    }

    /**
     * @return A SortedMap that contains the index of the include as keys, and instaces of Document as values. Only the
     *         includes that has a document is returned.
     */
    public SortedMap getIncludes() {
        Predicate predicate = new Predicate() {
            public boolean evaluate(Object o) {
                Map.Entry entry = (Map.Entry) o;
                Integer tempMetaId = (Integer) entry.getValue();
                return null != tempMetaId;
            }
        };

        Transformer fromDomainToAPITransformer = new Transformer() {
            public Object transform(Object o) {
                Integer tempMetaId = (Integer) o;

                return documentService.wrapDocumentDomainObject(documentMapper.getDocument(tempMetaId.intValue()));
            }
        };

        Map includeMap = getInternalTextDocument().getIncludes();

        return filterAndConvertValues(includeMap, predicate, fromDomainToAPITransformer);

    }

    private SortedMap filterAndConvertValues(Map map, Predicate predicate, Transformer transformer) {
        Collection nonEmptyTextFields = CollectionUtils.select(map.entrySet(), predicate);
        final SortedMap sortedMap = TransformedSortedMap.decorate(new TreeMap(), CloneTransformer.INSTANCE, transformer);

        for (Iterator iterator = nonEmptyTextFields.iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public TextField getTextField(int textFieldIndexInDocument) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        TextDomainObject imcmsText = getInternalTextDocument().getText(textFieldIndexInDocument);
        TextField textField = new TextField(imcmsText);
        return textField;
    }

    private TextDocumentDomainObject getInternalTextDocument() {
        return ((TextDocumentDomainObject) getInternal());
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
        getInternalTextDocument().setText(textFieldIndexInDocument, imcmsText);
    }

    /**
     * @throws NoPermissionException
     * @deprecated Use {@link #setImage(int, java.lang.String, java.lang.String, int, int, int, int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)} instead. link_target is used instead of link_targetname.
     */
    public void setImage(int imageIndexInDocument, String image_src, String image_name,
                         int width, int heigth, int border, int v_space, int h_space, String align,
                         String link_target, String link_targetname, String link_href,
                         String alt_text, String low_src) throws NoPermissionException {
        setImage(imageIndexInDocument, image_src, image_name, width, heigth, border, v_space, h_space, align, link_target, link_href, alt_text, low_src);

    }

    public void setImage(int imageIndexInDocument, String image_src, String image_name, int width, int heigth,
                         int border, int v_space,
                         int h_space, String align, String link_target, String link_href, String alt_text,
                         String low_src) throws NoPermissionException {
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
        DocumentStoringVisitor.saveDocumentImage(this.getId(), imageIndexInDocument, internalImage );
    }

    public Image getImage(int imageIndexInDocument) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        ImageDomainObject internalImage = getInternalTextDocument().getImage(imageIndexInDocument);
        if (null != internalImage) {
            return new Image(internalImage);
        } else {
            return null;
        }
    }

    public Template getTemplate() {
        TemplateDomainObject template = getInternalTextDocument().getTemplate();
        Template result = new Template(template);
        return result;
    }

    public void setTemplate(TemplateGroup templateGroup, Template template) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        setTemplateInternal(template);
        getInternalTextDocument().setTemplateGroupId(templateGroup.getId());
    }

    /**
     * @param template
     * @throws NoPermissionException
     * @deprecated
     */
    public void setTemplate(Template template) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        // todo: check if the template is alowed to be set on this internalTextDocument
        setTemplateInternal(template);
    }

    private void setTemplateInternal(Template newTemplate) {
        TemplateDomainObject internalTemplate = newTemplate.getInternal();
        getInternalTextDocument().setTemplate(internalTemplate);
    }

    public Document getInclude(int includeIndexInDocument) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        Integer includedDocumentId = getInternalTextDocument().getIncludedDocumentId(includeIndexInDocument);
        if (null != includedDocumentId) {
            DocumentDomainObject includedDocument = documentMapper.getDocument(includedDocumentId.intValue());
            if (null != includedDocument) {
                return documentService.wrapDocumentDomainObject(includedDocument);
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
     * @param sortOrder One of {@link TextDocument.Menu#SORT_BY_HEADLINE},
     *                  {@link TextDocument.Menu#SORT_BY_MODIFIED_DATETIME_DESCENDING},
     *                  or {@link TextDocument.Menu#SORT_BY_MANUAL_ORDER_DESCENDING}.
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
     *         one of {@link TextDocument.Menu#SORT_BY_HEADLINE},
     *         {@link TextDocument.Menu#SORT_BY_MODIFIED_DATETIME_DESCENDING},
     *         or {@link TextDocument.Menu#SORT_BY_MANUAL_ORDER_DESCENDING}.
     * @deprecated Sort order is now per menu. Method Will be removed some time after version 1.8.4.
     */
    public int getMenuSortOrder() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission(this);
        //return ((TextDocumentDomainObject)internalTextDocument).getMenuSortOrder();
        return TextDocument.Menu.SORT_BY_HEADLINE;
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

    public SortedMap getMenus() {
        Map internalMenus = getInternalTextDocument().getMenus() ;
        SortedMap menus = new TreeMap();
        for ( Iterator iterator = internalMenus.keySet().iterator(); iterator.hasNext(); ) {
            Integer menuIndex = (Integer)iterator.next();
            menus.put( menuIndex, new Menu( this, menuIndex.intValue(), securityChecker)) ;
        }
        return menus ;
    }

    public static class TextField {
        private TextDomainObject imcmsText;

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
            child = documentService.wrapDocumentDomainObject(internalMenuItem.getDocument());
        }

        public Document getDocument() {
            return child;
        }

        public int getManualNumber() {
            Integer sortKey = internalMenuItem.getSortKey();
            if (null == sortKey) {
                return 0;
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
        public final static int SORT_BY_MANUAL_ORDER_DESCENDING = imcode.server.document.textdocument.MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED;
        /**
         * Menu sorted by datetime. *
         */
        public final static int SORT_BY_MODIFIED_DATETIME_DESCENDING = imcode.server.document.textdocument.MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED;
        /**
         * Menu sorted by tree sort order
         */
        public final static int SORT_BY_TREE_ORDER_DESCENDING = imcode.server.document.textdocument.MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER;

        private SecurityChecker securityChecker;
        private TextDocumentDomainObject internalTextDocument;
        private int menuIndex;

        private Menu(TextDocument document, int menuIndex, SecurityChecker securityChecker) {
            this.internalTextDocument = document.getInternalTextDocument();
            this.menuIndex = menuIndex;
            this.securityChecker = securityChecker;
        }

        public MenuItem[] getMenuItems() throws NoPermissionException {
            MenuItemDomainObject[] menuItemsDomainObjects = internalTextDocument.getMenu(menuIndex).getMenuItems();
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
            internalTextDocument.getMenu(menuIndex).addMenuItem(new MenuItemDomainObject(documentToAdd.getInternal()));
        }

        /**
         * Remove a internalTextDocument from the menu.
         *
         * @param documentToRemove the internalTextDocument to remove
         * @throws NoPermissionException If you lack permission to edit the menudocument.
         */
        public void removeDocument(Document documentToRemove) throws NoPermissionException {
            securityChecker.hasEditPermission(documentToRemove.getId());
            internalTextDocument.getMenu(menuIndex).removeMenuItem(new MenuItemDomainObject(documentToRemove.getInternal()));
        }

        public Document[] getDocuments() {
            MenuItemDomainObject[] menuItemDomainObjects = internalTextDocument.getMenu(menuIndex).getMenuItems();
            List documentList = new ArrayList();
            for (int i = 0; i < menuItemDomainObjects.length; i++) {
                MenuItemDomainObject menuItemDomainObject = menuItemDomainObjects[i];
                DocumentDomainObject documentDomainObject = menuItemDomainObject.getDocument();
                boolean documentIsVisibleInMenu = (documentDomainObject.isPublishedAndNotArchived()
                        && documentMapper.userHasAtLeastDocumentReadPermission(securityChecker.getCurrentLoggedInUser(), documentDomainObject))
                        || documentMapper.userHasMoreThanReadPermissionOnDocument(securityChecker.getCurrentLoggedInUser(), documentDomainObject);
                if (documentIsVisibleInMenu) {
                    Document document = documentService.wrapDocumentDomainObject(documentDomainObject);
                    documentList.add(document);
                }
            }
            return (Document[]) documentList.toArray(new Document[documentList.size()]);
        }

        public void setSortOrder( int sortOrder ) {
            internalTextDocument.getMenu( menuIndex ).setSortOrder( sortOrder ) ;
        }

        public int getSortOrder() {
            return internalTextDocument.getMenu( menuIndex ).getSortOrder();
        }
    }
}