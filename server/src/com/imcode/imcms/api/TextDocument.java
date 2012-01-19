package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentGetter;
import imcode.server.document.DirectDocumentReference;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPredicate;
import imcode.server.document.DocumentReference;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.CloneTransformer;
import org.apache.commons.collections.map.TransformedSortedMap;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Document that can contain texts, images, menus, includes.
 */
public class TextDocument extends Document {

    /**
     * TextDocument TYPE_ID
     */
    public final static int TYPE_ID = DocumentTypeDomainObject.TEXT_ID;

    TextDocument( TextDocumentDomainObject textDocument,
                  ContentManagementSystem contentManagementSystem ) {
        super(textDocument, contentManagementSystem);
    }

    /**
     * Returns this document's TextFields that are not empty.
     * @return A SortedMap that has {@link TextField} indices as keys, and instances of TextFields as values. Only the
     * TextFields that contain any text are mapped.
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
     * Returns this document's Images
     * @return A SortedMap that contains {@link Image} indices as keys, and instaces of {@link Image} as values. Only the
     * Images that have url(not empty) are returned.
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
                return new Image(tempImage, getInternalTextDocument().getId());
            }
        };

        Map imagesMap = getInternalTextDocument().getImages();

        return filterAndConvertValues(imagesMap, predicate, fromDomainToAPITransformer);

    }

    /**
     * Returns this document's includes
     * @return A SortedMap that contains indices of the includes as keys, and instaces of {@link Document} as values. Only the
     * includes that have a {@link Document} are returned.
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

        Map includeMap = getInternalTextDocument().getIncludes();

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

    /**
     * Returns a TextField with the given index.
     * @param textFieldIndexInDocument text field index in this document
     * @return TextField with the given index or an empty string TextField if the document doesn't have a TextField with the given index
     */
    public TextField getTextField(int textFieldIndexInDocument) {
        TextDomainObject imcmsText = getInternalTextDocument().getText(textFieldIndexInDocument);
        if (null == imcmsText) {
            imcmsText = new TextDomainObject("");
            getInternalTextDocument().setText(textFieldIndexInDocument, imcmsText);
        }
        return new TextField(imcmsText);
    }

    private TextDocumentDomainObject getInternalTextDocument() {
        return (TextDocumentDomainObject)getInternal();
    }

    /**
     * Sets the text of the TextField with the given index to the text provided, in {@link com.imcode.imcms.api.TextDocument.TextField.Format#PLAIN} format
     * @param textFieldIndexInDocument index of a TextField in this document
     * @param newText text do be set
     */
    public void setPlainTextField(int textFieldIndexInDocument, String newText) {
        setTextField(textFieldIndexInDocument, newText, TextField.Format.PLAIN);
    }

    /**
     * Sets the text of the TextField with the given index to the text provided, in {@link com.imcode.imcms.api.TextDocument.TextField.Format#HTML} format
     * @param textFieldIndexInDocument index of a TextField in this document
     * @param newText text do be set
     */
    public void setHtmlTextField(int textFieldIndexInDocument, String newText) {
        setTextField(textFieldIndexInDocument, newText, TextField.Format.HTML);
    }

    /**
     * Sets the TextField with the given index to the provided text and {@link com.imcode.imcms.api.TextDocument.TextField.Format}
     * @param textFieldIndexInDocument index of a TextField in this document
     * @param newText text do be set
     * @param format {@link com.imcode.imcms.api.TextDocument.TextField.Format}
     */
    public void setTextField(int textFieldIndexInDocument, String newText, TextField.Format format) {
        TextDomainObject imcmsText = new TextDomainObject(newText, format.getType());
        getInternalTextDocument().setText(textFieldIndexInDocument, imcmsText);
    }

    /**
     * Returns Image with the given index in this document.
     * @param imageIndexInDocument image index in this document
     * @return Image or null if the document does contain any with the given index
     */
    public Image getImage(int imageIndexInDocument) {
        ImageDomainObject internalImage = getInternalTextDocument().getImage(imageIndexInDocument);
        if (null != internalImage) {
            return new Image(internalImage, getInternalTextDocument().getId());
        } else {
            return null;
        }
    }

    /**
     * Returns this text document's template
     * @return the template used by this document or null if the {@link Template} doesn't exist.
     */
    public Template getTemplate() {
        String templateName = getInternalTextDocument().getTemplateName();
        return contentManagementSystem.getTemplateService().getTemplate(templateName) ;
    }

    /**
     * Sets template group and template to be used by this text document.
     * @param templateGroup can be null. {@link TemplateGroup}, note that the template group doesn't have to have the template provided
     * @param template not null, template to be used by this document
     */
    public void setTemplate(TemplateGroup templateGroup, Template template) {
        getInternalTextDocument().setTemplateName(template.getInternal().getName());
        if (null != templateGroup) {
            getInternalTextDocument().setTemplateGroupId(templateGroup.getId());
        }
    }

    /**
     * Sets the {@link Template} of this text document to the given one.
     * @param template a template to be set for this text document, null is used for template group
     */
    public void setTemplate(Template template) {
        setTemplate(null, template);
    }

    /**
     * Returns included Document by the given index in the document.
     * @param includeIndexInDocument index of an include in this document
     * @return included Document or null if no document include exists by the given index in this document
     * or the included document doesn't exist anymore.
     */
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

    /**
     * Sets or removes the given document with the given index in this document.
     * @param includeIndexInDocument index of an inlcude in this document
     * @param documentToBeIncluded TextDocument to be set or a null, if null is given, the include is removed.
     */
    public void setInclude(int includeIndexInDocument, TextDocument documentToBeIncluded) {
        if (null == documentToBeIncluded) {
            getInternalTextDocument().removeInclude( includeIndexInDocument );
        } else {
            getInternalTextDocument().setInclude( includeIndexInDocument, documentToBeIncluded.getId() );
        }
    }

    /**
     * Get the {@link Menu} with the given index in this document.
     * @param menuIndexInDocument the index of the menu in this document.
     * @return {@link Menu} with the given index in this document.
     */
    public Menu getMenu(int menuIndexInDocument) {
        return new Menu(this, menuIndexInDocument);
    }

    /**
     * Returns all menus in this document.
     * @return a SortedMap of menus in this document, with menu indices as keys and menus as values
     */
    public SortedMap getMenus() {
        Map<Integer, MenuDomainObject> internalMenus = getInternalTextDocument().getMenus() ;
        SortedMap menus = new TreeMap();
        for ( Integer menuIndex : internalMenus.keySet() ) {
            menus.put(menuIndex, new Menu(this, menuIndex.intValue()));
        }
        return menus ;
    }

    /**
     * Sets the given {@link Image} to the given index in this document.
     * @param imageIndex index in this document
     * @param image Image to set, not null
     */
    public void setImage( int imageIndex, Image image ) {
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)getInternal() ;
        textDocument.setImage( imageIndex, image.getInternal());
    }

    ContentManagementSystem getContentManagementSystem() {
        return contentManagementSystem;
    }

    /**
     * Class represents a text field inside TextDocument
     */
    public static class TextField {
        private final TextDomainObject imcmsText;

        /**
         * Format in which the text inside TextField is set or returned.
         * When {@link Format#PLAIN} format is used, the reserved HTML characters are replaced with character entities.
         */
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

        /**
         * Returns format set for this text field
         * @return Format of this text field
         */
        public Format getFormat() {
            return imcmsText.getType() == TextDomainObject.TEXT_TYPE_PLAIN ? Format.PLAIN : Format.HTML;
        }

        /**
         * Sets format of this text field
         * @param format Format to use by this text field
         */
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

    /**
     * Represents a menu item in a {@link Menu}.
     */
    public static class MenuItem {
        MenuItemDomainObject internalMenuItem;
        Document child;

        public MenuItem( MenuItemDomainObject internalMenuItem, ContentManagementSystem contentManagementSystem ) {
            this.internalMenuItem = internalMenuItem;
            DocumentService.ApiWrappingDocumentVisitor visitor = new DocumentService.ApiWrappingDocumentVisitor( contentManagementSystem );
            internalMenuItem.getDocument().accept( visitor );
            child = visitor.getDocument() ;
        }

        /**
         * Returns a {@link Document} contained by this menu item.
         * @return Document contained in the menu item
         */
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

        /**
         * Returns the manual sort key of this menu item
         * @return integer representing sort of key of this menu item
         */
        public Integer getSortKey() {
            return internalMenuItem.getSortKey();
        }

        /**
         * Sets the manual sort key of this menu item
         * @param sortKey sort key
         */
        public void setSortKey( Integer sortKey ) {
            internalMenuItem.setSortKey(sortKey);
        }

        /**
         * Returns the {@link TreeKey} of this menu item.
         * @return a TreeKey, which can be empty if the menu item doesn't have any set.
         */
        public TreeKey getTreeKey() {
            return new TreeKey(internalMenuItem.getTreeSortKey());
        }

        /**
         * Sets the tree key of this menu item to the given one
         * @param treeKey {@link TreeKey} to be used for this menu item
         */
        public void setTreeKey(TreeKey treeKey) {
            internalMenuItem.setTreeSortKey(treeKey.internalTreeSortKey);
        }

        /**
         * Represents hierarchical order key e.g. 1.3 is a child of 1
         */
        public static class TreeKey {
            TreeSortKeyDomainObject internalTreeSortKey;

            /**
             * Constructs TreeKey with TreeSortKeyDomainObject backing it.
             * @param internalTreeSortKey TreeSortKeyDomainObject to be used internally
             */
            public TreeKey(TreeSortKeyDomainObject internalTreeSortKey) {
                this.internalTreeSortKey = internalTreeSortKey;
            }

            /**
             * Constructs TreeKey from a String, not digit characters are treated as a separator, so
             * 1.3 and 1,3 produce the same key.
             * @param treeSortKey a String to construct TreeKey from
             */
            public TreeKey(String treeSortKey) {
                internalTreeSortKey = new TreeSortKeyDomainObject(treeSortKey);
            }

            /**
             * Returns the number of levels in this tree key.
             * @return int, number of levels
             */
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

            /**
             * Returns a string representation of this tree key in a form of numbers separated by periods('.')
             * @return a String representing tree key in a form of numbers separated by periods('.')
             */
            public String toString() {
                return internalTreeSortKey.toString();
            }
        }
    }

    /**
     * Represents a menu in a {@link TextDocument}.
     */
    public static class Menu {
        /**
         * Menu sort order by headline.
         */
        public final static int SORT_BY_HEADLINE = MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE;

        /**
         * Menu sort order by 'manual' order.
         */
        public final static int SORT_BY_MANUAL_ORDER_DESCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED;

        /**
         * Menu sort order by datetime.
         */
        public final static int SORT_BY_MODIFIED_DATETIME_DESCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED;

        /**
         * Menu sort order by {@link com.imcode.imcms.api.TextDocument.MenuItem.TreeKey} order.
         * @deprecated Wrong name, use {@link #SORT_BY_TREE_ORDER_ASCENDING}.
         */
        public final static int SORT_BY_TREE_ORDER_DESCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER;

        /**
         * Menu sort order by {@link com.imcode.imcms.api.TextDocument.MenuItem.TreeKey} order. Ascending.
         */
        public final static int SORT_BY_TREE_ORDER_ASCENDING = MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER;

        /**
         * Menu sort order by {@link com.imcode.imcms.api.Document#getPublicationStartDatetime()}. Descending.
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

        /**
         * Returns the sort order of this menu.
         * @return sort order of this menu
         */
        public int getSortOrder() {
            return internalTextDocument.getMenu( menuIndex ).getSortOrder();
        }

        /**
         * Returns menu items that current user can see.
         * @return array of visible {@link MenuItem} in this menu.
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
         * Returns documents the menu items hold in this menu that current user can see.
         * @return the documents returned by {@link #getVisibleMenuItems()}.
         * @since 2.0
         */
        public Document[] getVisibleDocuments() {
            MenuItem[] menuItems = getVisibleMenuItems() ;
            return getDocumentsFromMenuItems( menuItems ) ;
        }

        /**
         * Returns menu items in this menu that current user can see or edit, excluding archived documents.
         * The menu items that have a missing document(due to deletion etc) are not returned.
         * @return array of menuitems in this menu that the user can see or edit, excluding archived documents.
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
         * Returns menu items in this menu that current user can see or edit, excluding archived documents.
         * @return array of menuitems in this menu that the user can see or edit, including archived documents.
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
         * Returns documents contained in menu items returned by {@link #getMenuItems()}.
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