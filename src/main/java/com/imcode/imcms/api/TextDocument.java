package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentGetter;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TextDocument extends Document {

    public final static int TYPE_ID = DocumentTypeDomainObject.TEXT_ID;
    private static final long serialVersionUID = -8289218319353617986L;

    TextDocument(TextDocumentDomainObject textDocument,
                 ContentManagementSystem contentManagementSystem) {
        super(textDocument, contentManagementSystem);
    }

    @Override
    public TextDocumentDomainObject getInternal() {
        return (TextDocumentDomainObject) super.getInternal();
    }

    /**
     * @return A SortedMap that contains the textFields index as keys, and instances of TextFields as values. Only the
     * TextFields that contains any text is returned.
     */
    public SortedMap<Integer, TextField> getTextFields() {
        Predicate<Entry<?, TextDomainObject>> predicate = entry -> StringUtils.isNotEmpty(entry.getValue().getText());
        Function<TextDomainObject, TextField> fromDomainToApiTransformer = TextField::new;
        Map<Integer, TextDomainObject> textFieldsMap = getInternalTextDocument().getTexts();

        return filterAndConvertValues(textFieldsMap, predicate, fromDomainToApiTransformer);
    }

    /**
     * @return A SortedMap that contains the images index as keys, and instances of Image as values. Only the
     * Image that has an url is returned.
     */
    public SortedMap<Integer, Image> getImages() {
        Predicate<Entry<?, ImageDomainObject>> predicate = entry -> !entry.getValue().isEmpty();
        Function<ImageDomainObject, Image> fromDomainToApiTransformer = Image::new;
        Map<Integer, ImageDomainObject> imagesMap = getInternalTextDocument().getImages();

        return filterAndConvertValues(imagesMap, predicate, fromDomainToApiTransformer);
    }

    /**
     * @return A SortedMap that contains the index of the include as keys, and instances of Document as values. Only the
     * includes that has a document is returned.
     */
    public SortedMap<Integer, Document> getIncludes() {
        Predicate<Entry<?, Integer>> predicate = entry -> null != entry.getValue();
        final DocumentGetter documentGetter = getDocumentGetter();

        Function<Integer, Document> fromDomainToApiTransformer =
                tempMetaId -> DocumentService.wrapDocumentDomainObject(
                        documentGetter.getDocument(tempMetaId), contentManagementSystem
                );

        Map<Integer, Integer> includeMap = getInternalTextDocument().getIncludesMap();

        return filterAndConvertValues(includeMap, predicate, fromDomainToApiTransformer);
    }

    private DocumentGetter getDocumentGetter() {
        return contentManagementSystem.getInternal().getDocumentMapper();
    }

    private <T, O> SortedMap<Integer, T> filterAndConvertValues(Map<Integer, O> map, Predicate<Entry<?, O>> predicate,
                                                                Function<O, T> transformer) {
        final Map<Integer, T> filteredConverted = map.entrySet()
                .stream()
                .filter(predicate)
                .sorted()
                .collect(Collectors.toMap(Entry::getKey, entry -> transformer.apply(entry.getValue())));

        return new TreeMap<>(filteredConverted);
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
        return getInternal();
    }

    public void setPlainTextField(int textFieldIndexInDocument, String newText) {
        setTextField(textFieldIndexInDocument, newText, TextField.Format.PLAIN);
    }

    public void setHtmlTextField(int textFieldIndexInDocument, String newText) {
        setTextField(textFieldIndexInDocument, newText, TextField.Format.HTML);
    }

    public void setCleanHtmlTextField(int textFieldIndexInDocument, String newText) {
        newText = Utility.getTextContentFilter().cleanText(newText);
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
        return contentManagementSystem.getTemplateService().getTemplate(templateName);
    }

    public void setTemplate(Template template) {
        setTemplate(null, template);
    }

    public void setTemplate(TemplateGroup templateGroup, Template template) {
        getInternalTextDocument().setTemplateName(template.getInternal().getName());
        if (null != templateGroup) {
            getInternalTextDocument().setTemplateGroupId(templateGroup.getId());
        }
    }

    public Document getInclude(int includeIndexInDocument) {
        Integer includedDocumentId = getInternalTextDocument().getIncludedDocumentId(includeIndexInDocument);
        if (null != includedDocumentId) {
            DocumentDomainObject includedDocument = getDocumentGetter().getDocument(includedDocumentId);
            if (null != includedDocument) {
                return DocumentService.wrapDocumentDomainObject(includedDocument, contentManagementSystem);
            }
        }
        return null;
    }

    public void setInclude(int includeIndexInDocument, TextDocument documentToBeIncluded) {
        if (null == documentToBeIncluded) {
            getInternalTextDocument().removeInclude(includeIndexInDocument);
        } else {
            getInternalTextDocument().setInclude(includeIndexInDocument, documentToBeIncluded.getId());
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

    public SortedMap<Integer, Menu> getMenus() {
        Map<Integer, MenuDomainObject> internalMenus = getInternalTextDocument().getMenus();
        SortedMap<Integer, Menu> menus = new TreeMap<>();
        for (Integer menuIndex : internalMenus.keySet()) {
            menus.put(menuIndex, new Menu(this, menuIndex));
        }
        return menus;
    }

    public void setImage(int imageIndex, Image image) {
        TextDocumentDomainObject textDocument = getInternal();
        textDocument.setImage(imageIndex, image.getInternal());
    }

    ContentManagementSystem getContentManagementSystem() {
        return contentManagementSystem;
    }

    public Loop getLoop(int no) {
        return getInternal().getLoop(no);
    }

    /**
     * Get text field from loop
     *
     * @param loopNo  loop index in document
     * @param entryNo number of loop entry
     * @param textNo  text index in loop
     * @return text field of document or empty text field if not exist
     */
    public TextField getLoopTextField(int loopNo, int entryNo, int textNo) {
        return getLoopTextField(TextDocumentDomainObject.LoopItemRef
                .of(loopNo, entryNo, textNo)
        );
    }

    /**
     * Get text field from loop
     *
     * @param loopItemRef loop item reference of text in loop
     * @return text field of document or empty text field if not exist
     */
    public TextField getLoopTextField(TextDocumentDomainObject.LoopItemRef loopItemRef) {
        TextDocumentDomainObject internalDoc = getInternal();
        TextDomainObject text = internalDoc.getText(loopItemRef);

        if (null == text) {
            text = new TextDomainObject("");
            internalDoc.setText(loopItemRef, text);
        }

        return new TextField(text);
    }

    /**
     * Get image from loop
     *
     * @param loopNo  loop index in document
     * @param entryNo number of loop entry
     * @param imageNo image index in loop
     * @return image of document or empty image if not exist
     */
    public Image getLoopImage(int loopNo, int entryNo, int imageNo) {
        return getLoopImage(TextDocumentDomainObject.LoopItemRef.of(loopNo, entryNo, imageNo));
    }

    /**
     * Get image from loop
     *
     * @param loopItemRef loop item reference of image in loop
     * @return image of document or empty image if not exist
     */
    public Image getLoopImage(TextDocumentDomainObject.LoopItemRef loopItemRef) {
        TextDocumentDomainObject internalDoc = getInternal();
        ImageDomainObject image = internalDoc.getImage(loopItemRef);

        return new Image(image);
    }

    public static class TextField {
        private final TextDomainObject imcmsText;

        private TextField(TextDomainObject imcmsText) {
            this.imcmsText = imcmsText;
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
    }

    public static class MenuItem {
        MenuItemDomainObject internalMenuItem;
        Document child;

        public MenuItem(MenuItemDomainObject internalMenuItem, ContentManagementSystem contentManagementSystem) {
            this.internalMenuItem = internalMenuItem;
            DocumentService.ApiWrappingDocumentVisitor visitor = new DocumentService.ApiWrappingDocumentVisitor(contentManagementSystem);
            internalMenuItem.getDocument().accept(visitor);
            child = visitor.getDocument();
        }

        public MenuItem(MenuItemDomainObject menuItem, DocumentDomainObject document, ContentManagementSystem contentManagementSystem) {
            this.internalMenuItem = menuItem;
            this.child = new Document(document, contentManagementSystem);
        }

        public Document getDocument() {
            return child;
        }

        public TreeKey getTreeKey() {
            return new TreeKey(internalMenuItem.getTreeSortKey());
        }

        public static class TreeKey {
            TreeSortKeyDomainObject internalTreeSortKey;

            TreeKey(TreeSortKeyDomainObject internalTreeSortKey) {
                this.internalTreeSortKey = internalTreeSortKey;
            }

            public String toString() {
                return internalTreeSortKey.toString();
            }
        }
    }

    public static class Menu {
        private final TextDocumentDomainObject internalTextDocument;
        private final int menuIndex;
        private final ContentManagementSystem contentManagementSystem;

        Menu(TextDocument document, int menuIndex) {
            internalTextDocument = document.getInternalTextDocument();
            this.menuIndex = menuIndex;
            contentManagementSystem = document.getContentManagementSystem();
        }

        /**
         * @return The visible menuitems in this menu.
         * @since 2.0
         */
        public MenuItem[] getVisibleMenuItems() {
            final UserDomainObject user = contentManagementSystem.getCurrentUser().getInternal();
            return getMenuItems(user::canSeeDocumentInMenus);
        }

        /**
         * @return the documents returned by {@link #getVisibleMenuItems()}.
         * @since 2.0
         */
        Document[] getVisibleDocuments() {
            MenuItem[] menuItems = getVisibleMenuItems();
            return getDocumentsFromMenuItems(menuItems);
        }

        /**
         * @return The menuitems in this menu that the user can see or edit, excluding archived documents.
         */
        public MenuItem[] getMenuItems() {
            final UserDomainObject user = contentManagementSystem.getCurrentUser().getInternal();
            return getMenuItems(document -> user.canSeeDocumentInMenus(document) || user.canEdit(document));
        }

        /**
         * @return the documents returned by {@link #getMenuItems()}.
         */
        public Document[] getDocuments() {
            MenuItem[] menuItems = getMenuItems();
            return getDocumentsFromMenuItems(menuItems);
        }

        private MenuItem[] getMenuItems(Predicate<DocumentDomainObject> documentPredicate) {
            MenuItemDomainObject[] menuItemsDomainObjects = internalTextDocument.getMenu(menuIndex).getMenuItems();
            List<MenuItem> menuItems = new ArrayList<>(menuItemsDomainObjects.length);
            for (MenuItemDomainObject menuItemDomainObject : menuItemsDomainObjects) {
                DocumentDomainObject document = menuItemDomainObject.getDocument();
                if (documentPredicate.test(document)) {
                    menuItems.add(new MenuItem(menuItemDomainObject, document, contentManagementSystem));
                }
            }
            return menuItems.toArray(new MenuItem[menuItems.size()]);
        }

        private Document[] getDocumentsFromMenuItems(MenuItem[] menuItems) {
            Document[] documents = new Document[menuItems.length];
            for (int i = 0; i < menuItems.length; i++) {
                MenuItem menuItem = menuItems[i];
                documents[i] = menuItem.getDocument();
            }
            return documents;
        }

    }
}