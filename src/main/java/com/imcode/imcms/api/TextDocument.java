package com.imcode.imcms.api;

import imcode.server.document.DirectDocumentReference;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPredicate;
import imcode.server.document.DocumentReference;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.TreeSortKeyDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.CloneTransformer;
import org.apache.commons.collections.map.TransformedSortedMap;
import org.apache.commons.lang.StringUtils;

import com.imcode.imcms.mapping.DocumentGetter;

public class TextDocument extends Document {

    public final static int TYPE_ID = DocumentTypeDomainObject.TEXT_ID;

    TextDocument( TextDocumentDomainObject textDocument,
                  ContentManagementSystem contentManagementSystem ) {
        super(textDocument, contentManagementSystem);
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
                return !tempImage.isEmpty();
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
                return DocumentService.wrapDocumentDomainObject(getDocumentGetter().getDocument(tempMetaId), contentManagementSystem );
            }
        };

        Map includeMap = getInternalTextDocument().getIncludesMap();

        return filterAndConvertValues(includeMap, predicate, fromDomainToAPITransformer);

    }

    private DocumentGetter getDocumentGetter() {
        return contentManagementSystem.getInternal().getDocumentMapper() ;
    }

    private SortedMap filterAndConvertValues(Map map, Predicate predicate, Transformer transformer) {
        Collection<Map.Entry> nonEmptyTextFields = CollectionUtils.select(map.entrySet(), predicate);
        final SortedMap sortedMap = TransformedSortedMap.decorate(new TreeMap(), CloneTransformer.INSTANCE, transformer);

        for ( Map.Entry entry : nonEmptyTextFields ) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public TextField getTextField(int textFieldIndexInDocument) {
        TextDomainObject imcmsText = getInternalTextDocument().getText(textFieldIndexInDocument);
        if (null == imcmsText) {
            imcmsText = new TextDomainObject("");
            getInternalTextDocument().setText(textFieldIndexInDocument, imcmsText);
        }
        return new TextField(imcmsText);
    }

    public TextDocumentDomainObject getInternalTextDocument() {
        return (TextDocumentDomainObject)getInternal();
    }

    public void setPlainTextField(int textFieldIndexInDocument, String newText) {
        setTextField(textFieldIndexInDocument, newText, TextField.Format.PLAIN);
    }

    public void setHtmlTextField(int textFieldIndexInDocument, String newText) {
        setTextField(textFieldIndexInDocument, newText, TextField.Format.HTML);
    }

    public void setTextField(int textFieldIndexInDocument, String newText, TextField.Format format) {
        TextDomainObject imcmsText = new TextDomainObject(newText, format.getType());
        getInternalTextDocument().setText(textFieldIndexInDocument, imcmsText);
    }

    public Image getImage(int imageIndexInDocument) {
        ImageDomainObject internalImage = getInternalTextDocument().getImage(imageIndexInDocument);
        if (null != internalImage) {
            return new Image(internalImage);
        } else {
            return null;
        }
    }

    public Template getTemplate() {
        String templateName = getInternalTextDocument().getTemplateName();
        return contentManagementSystem.getTemplateService().getTemplate(templateName) ;
    }

    public void setTemplate(TemplateGroup templateGroup, Template template) {
        getInternalTextDocument().setTemplateName(template.getInternal().getName());
        if (null != templateGroup) {
            getInternalTextDocument().setTemplateGroupId(templateGroup.getId());
        }
    }

    public void setTemplate(Template template) {
        setTemplate(null, template);
    }

    public Document getInclude(int includeIndexInDocument) {
        Integer includedDocumentId = getInternalTextDocument().getIncludedDocumentId(includeIndexInDocument);
        if (null != includedDocumentId) {
            DocumentDomainObject includedDocument = getDocumentGetter().getDocument(new Integer(includedDocumentId.intValue()));
            if (null != includedDocument) {
                return DocumentService.wrapDocumentDomainObject(includedDocument, contentManagementSystem );
            }
        }
        return null;
    }

    public void setInclude(int includeIndexInDocument, TextDocument documentToBeIncluded) {
        if (null == documentToBeIncluded) {
            getInternalTextDocument().removeInclude( includeIndexInDocument );
        } else {
            getInternalTextDocument().setInclude( includeIndexInDocument, documentToBeIncluded.getId() );
        }
    }

    /**
     * Get the menu with the given index in the owner.
     *
     * @param menuIndexInDocument the index of the menu in the owner.
     * @return the menu with the given index in the owner.
     */
    public Menu getMenu(int menuIndexInDocument) {
        return new Menu(this, menuIndexInDocument);
    }

    public SortedMap getMenus() {
        Map<Integer, MenuDomainObject> internalMenus = getInternalTextDocument().getMenus() ;
        SortedMap menus = new TreeMap();
        for ( Integer menuIndex : internalMenus.keySet() ) {
            menus.put(menuIndex, new Menu(this, menuIndex.intValue()));
        }
        return menus ;
    }

    public void setImage( int imageIndex, Image image ) {
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)getInternal() ;
        textDocument.setImage( imageIndex, image.getInternal());
    }

    ContentManagementSystem getContentManagementSystem() {
        return contentManagementSystem;
    }

    public static class TextField {
        private final TextDomainObject imcmsText;

        public enum Format {
            PLAIN(TextDomainObject.TEXT_TYPE_PLAIN),
            HTML(TextDomainObject.TEXT_TYPE_HTML);
            private final int type;

            Format(int type) {
                this.type = type;
            }

            public int getType() {
                return type;
            }
        }

        private TextField(TextDomainObject imcmsText) {
            this.imcmsText = imcmsText;
        }

        /**
         * @deprecated Use {@link #setFormat(Format)}
         */
        public void setHtmlFormat() {
            imcmsText.setType(TextDomainObject.TEXT_TYPE_HTML);
        }

        /**
         * @deprecated Use {@link #setFormat(Format)}
         */
        public void setPlainFormat() {
            imcmsText.setType(TextDomainObject.TEXT_TYPE_PLAIN);
        }

        public Format getFormat() {
            return imcmsText.getType() == TextDomainObject.TEXT_TYPE_PLAIN ? Format.PLAIN : Format.HTML;
        }

        public void setFormat(Format format) {
            imcmsText.setType(format.getType());
        }

        /**
         * Get the text of this textfield.
         *
         * @return the text of this textfield.
         */
        public String getText() {
            return imcmsText.getText();
        }

        /**
         * Get the text of this textfield as a html-formatted string,
         * suitable for displaying in a html-page.
         *
         * @return the text of this textfield as a html-formatted string, suitable for displaying in a html-page.
         */
        public String getHtmlFormattedText() {
            return imcmsText.toHtmlString();
        }
    }

    public static class MenuItem {
        MenuItemDomainObject internalMenuItem;
        Document child;

        public MenuItem( MenuItemDomainObject internalMenuItem, ContentManagementSystem contentManagementSystem ) {
            this.internalMenuItem = internalMenuItem;
            DocumentService.ApiWrappingDocumentVisitor visitor = new DocumentService.ApiWrappingDocumentVisitor( contentManagementSystem );
            internalMenuItem.getDocument().accept( visitor );
            child = visitor.getDocument() ;
        }

        public Document getDocument() {
            return child;
        }


        /**
            @deprecated Use {@link #getSortKey()}
       **/
        public int getManualNumber() {
            Integer sortKey = internalMenuItem.getSortKey();
            if (null == sortKey) {
                return 0;
            }
            return sortKey.intValue();
        }

        public Integer getSortKey() {
            return internalMenuItem.getSortKey();
        }

        public void setSortKey( Integer sortKey ) {
            internalMenuItem.setSortKey(sortKey);
        }

        public TreeKey getTreeKey() {
            return new TreeKey(internalMenuItem.getTreeSortKey());
        }

        public void setTreeKey(TreeKey treeKey) {
            internalMenuItem.setTreeSortKey(treeKey.internalTreeSortKey);
        }

        public static class TreeKey {
            TreeSortKeyDomainObject internalTreeSortKey;

            public TreeKey(TreeSortKeyDomainObject internalTreeSortKey) {
                this.internalTreeSortKey = internalTreeSortKey;
            }

            public TreeKey(String treeSortKey) {
                internalTreeSortKey = new TreeSortKeyDomainObject(treeSortKey);
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

    public static class Menu {
        /**
         * Menu sorted by headline.
         */
        public final static int SORT_BY_HEADLINE = MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE;
        /**
         * Menu sorted by 'manual' order.
         */
        public final static int SORT_BY_MANUAL_ORDER_DESCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED;
        /**
         * Menu sorted by datetime.
         */
        public final static int SORT_BY_MODIFIED_DATETIME_DESCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED;
        /**
         * Menu sorted by tree sort order.
         * @deprecated Wrong name, use {@link #SORT_BY_TREE_ORDER_ASCENDING}.
         */
        public final static int SORT_BY_TREE_ORDER_DESCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER;
        /**
         * Menu sorted by tree sort order.
         */
        public final static int SORT_BY_TREE_ORDER_ASCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER;
        /**
         * Menu sorted by tree sort order.
         */
        public final static int SORT_BY_PUBLISHED_DATETIME_DESCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED;

        private final TextDocumentDomainObject internalTextDocument;
        private final int menuIndex;
        private final ContentManagementSystem contentManagementSystem;

        Menu(TextDocument document, int menuIndex) {
            internalTextDocument = document.getInternalTextDocument();
            this.menuIndex = menuIndex;
            contentManagementSystem = document.getContentManagementSystem() ;
        }

        /**
         * Add a internalTextDocument to the menu.
         *
         * @param documentToAdd the document to add
         */
        public void addDocument(Document documentToAdd) {
            DocumentReference documentReference = new DirectDocumentReference( documentToAdd.getInternal() );
            internalTextDocument.getMenu(menuIndex).addMenuItem(new MenuItemDomainObject(documentReference));
        }

        /**
         * Remove a internalTextDocument from the menu.
         *
         * @param documentToRemove the document to remove
         */
        public void removeDocument(Document documentToRemove) {
            internalTextDocument.getMenu(menuIndex).removeMenuItemByDocumentId(documentToRemove.getId());
        }

        /**
         * @param sortOrder One of {@link #SORT_BY_HEADLINE}, {@link #SORT_BY_MANUAL_ORDER_DESCENDING},
         *                  {@link #SORT_BY_MODIFIED_DATETIME_DESCENDING}, or {@link #SORT_BY_TREE_ORDER_DESCENDING}
         */
        public void setSortOrder( int sortOrder ) {
            internalTextDocument.getMenu( menuIndex ).setSortOrder( sortOrder );
        }

        public int getSortOrder() {
            return internalTextDocument.getMenu( menuIndex ).getSortOrder();
        }

        /**
         * @return The visible menuitems in this menu.
         * @since 2.0
         */
        public MenuItem[] getVisibleMenuItems() {
            final UserDomainObject user = contentManagementSystem.getCurrentUser().getInternal();
            DocumentPredicate documentPredicate = new DocumentPredicate() {
                public boolean evaluateDocument( DocumentDomainObject document ) {
                    return user.canSeeDocumentInMenus( document ) ;
                }
            };
            return getMenuItems( documentPredicate );
        }

        /**
         * @return the documents returned by {@link #getVisibleMenuItems()}.
         * @since 2.0
         */
        public Document[] getVisibleDocuments() {
            MenuItem[] menuItems = getVisibleMenuItems() ;
            return getDocumentsFromMenuItems( menuItems ) ;
        }

        /**
         * @return The menuitems in this menu that the user can see or edit, excluding archived documents.
         */
        public MenuItem[] getMenuItems() {
            final UserDomainObject user = contentManagementSystem.getCurrentUser().getInternal();
            return getMenuItems( new DocumentPredicate() {
                public boolean evaluateDocument( DocumentDomainObject document ) {
                    return user.canSeeDocumentInMenus( document ) || user.canEdit( document ) ;
                }
            } ) ;
        }

        /**
         * @return The menuitems in this menu that the user can see or edit, including archived documents.
         */
        public MenuItem[] getPublishedMenuItems() {
            final UserDomainObject user = contentManagementSystem.getCurrentUser().getInternal();
            return getMenuItems( new DocumentPredicate() {
                public boolean evaluateDocument( DocumentDomainObject document ) {
                    return document.isPublished() && user.canSeeDocumentWhenEditingMenus( document ) ;
                }
            } ) ;
        }

        /**
         * @return the documents returned by {@link #getMenuItems()}.
         */
        public Document[] getDocuments() {
            MenuItem[] menuItems = getMenuItems();
            return getDocumentsFromMenuItems( menuItems );
        }

        private MenuItem[] getMenuItems( DocumentPredicate documentPredicate ) {
            MenuItemDomainObject[] menuItemsDomainObjects = internalTextDocument.getMenu( menuIndex ).getMenuItems();
            List menuItems = new ArrayList( menuItemsDomainObjects.length );
            for ( MenuItemDomainObject menuItemDomainObject : menuItemsDomainObjects ) {
                DocumentDomainObject document = menuItemDomainObject.getDocument();
                if ( documentPredicate.evaluateDocument(document) ) {
                    menuItems.add(new MenuItem(menuItemDomainObject, contentManagementSystem));
                }
            }
            return (MenuItem[])menuItems.toArray( new MenuItem[menuItems.size()] );
        }

        private Document[] getDocumentsFromMenuItems( MenuItem[] menuItems ) {
            Document[] documents = new Document[menuItems.length];
            for ( int i = 0; i < menuItems.length; i++ ) {
                MenuItem menuItem = menuItems[i];
                documents[i] = menuItem.getDocument();
            }
            return documents;
        }

    }

}