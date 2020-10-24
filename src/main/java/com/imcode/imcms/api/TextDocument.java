package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.DocumentStatus;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.*;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Version.WORKING_VERSION_INDEX;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TextDocument extends Document {

    public final static int TYPE_ID = DocumentTypeDomainObject.TEXT_ID;
    private static final long serialVersionUID = -8289218319353617986L;
    private final static Logger LOGGER = Logger.getLogger(TextDocument.class);

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
     * @return A SortedMap that contains the images index as keys, and instances of ImageJPA as values. Only the
     * ImageJPA that has an url is returned.
     */
    public SortedMap<Integer, Image> getImages() {
        Predicate<Entry<?, ImageDomainObject>> predicate = entry -> !entry.getValue().isEmpty();
        Function<ImageDomainObject, Image> fromDomainToApiTransformer = Image::new;
        Map<Integer, ImageDomainObject> imagesMap = getInternalTextDocument().getImages();

        return filterAndConvertValues(imagesMap, predicate, fromDomainToApiTransformer);
    }

    private DocumentGetter getDocumentGetter() {
        return contentManagementSystem.getInternal().getDocumentMapper();
    }

    private <T, O> SortedMap<Integer, T> filterAndConvertValues(Map<Integer, O> map, Predicate<Entry<?, O>> predicate,
                                                                Function<O, T> transformer) {
        final Map<Integer, T> filteredConverted = map.entrySet()
                .stream()
                .filter(predicate)
                .collect(Collectors.toMap(Entry::getKey, entry -> transformer.apply(entry.getValue())));

        return new TreeMap<>(filteredConverted);
    }

    public TextField getTextField(int textFieldIndexInDocument) {
        TextService textService = Imcms.getServices().getManagedBean(TextService.class);
        Text text;
        if (getInternalTextDocument().getVersionNo() == WORKING_VERSION_INDEX) {
            text = textService.getText(getInternalTextDocument().getId(),
                    textFieldIndexInDocument, Imcms.getLanguage().getCode(), null);
        } else {
            text = textService.getPublicText(getInternalTextDocument().getId(),
                    textFieldIndexInDocument, Imcms.getLanguage().getCode(), null);
        }

        TextDomainObject imcmsText = new TextDomainObject("");
        if (null == text.getText()) {
            getInternalTextDocument().setText(textFieldIndexInDocument, imcmsText);
        } else {
            imcmsText.setText(text.getText());
            imcmsText.setType(text.getType().ordinal());
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
        setTextField(textFieldIndexInDocument, newText, TextField.Format.EDITOR);
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
        return contentManagementSystem.getTemplateService()
                .get(templateName)
                .orElse(null);
    }

    public void setTemplate(Template template) {
        setTemplate(null, template);
    }

    public void setTemplate(TemplateGroup templateGroup, Template template) {
        getInternalTextDocument().setTemplateName(template.getName());
        if (null != templateGroup) {
            getInternalTextDocument().setTemplateGroupId(templateGroup.getId());
        }
    }

    public SortedMap<Integer, MenuDTO> getMenus() {
        return new TreeMap<>(getInternalTextDocument().getMenus());
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

    @Deprecated
    public Menu getMenu(int no) {
        return new Menu(this, no);
    }

    public static class TextField {
        private final TextDomainObject imcmsText;

        private TextField(TextDomainObject imcmsText) {
            this.imcmsText = imcmsText;
        }

        public Format getFormat() {
            return Format.fromType(imcmsText.getType());
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
            HTML(TextDomainObject.TEXT_TYPE_HTML),
            EDITOR(TextDomainObject.TEXT_TYPE_EDITOR),
            ;

            private final int type;

            Format(int type) {
                this.type = type;
            }

            public static Format fromType(int type) {
                switch (type) {
                    case TextDomainObject.TEXT_TYPE_HTML:
                        return Format.HTML;
                    case TextDomainObject.TEXT_TYPE_EDITOR:
                        return Format.EDITOR;
                    default:
                        return Format.PLAIN;
                }
            }

            public int getType() {
                return type;
            }
        }
    }

    @Deprecated
    public static class Menu {

        private TextDocumentDomainObject internalTextDocument;
        private int menuIndex;
        private ContentManagementSystem contentManagementSystem;

        Menu(TextDocument document, int menuIndex) {
            this.internalTextDocument = document.getInternalTextDocument();
            this.menuIndex = menuIndex;
            this.contentManagementSystem = document.getContentManagementSystem();
        }

        @Deprecated
        public void addDocument(Document documentToAdd) {
            final int docId = documentToAdd.getId();

            final MenuItemDTO menuItemDTO = new MenuItemDTO();
            menuItemDTO.setDocumentId(docId);
            menuItemDTO.setTitle(documentToAdd.getHeadline());
            menuItemDTO.setTarget(documentToAdd.getTarget());
            menuItemDTO.setType(Meta.DocumentType.values()[documentToAdd.getInternal().getMeta().getDocumentTypeId()]);
            menuItemDTO.setDocumentStatus(DocumentStatus.IN_PROCESS);
            menuItemDTO.setLink("/" + (documentToAdd.getAlias() == null ? docId : documentToAdd.getAlias()));

            internalTextDocument.getMenu(this.menuIndex).getMenuItems().add(menuItemDTO);
        }

        @Deprecated
        public void removeDocument(Document documentToRemove) {
            internalTextDocument.getMenu(this.menuIndex)
                    .getMenuItems()
                    .removeIf(menuItemDTO -> menuItemDTO.getDocumentId() == documentToRemove.getId());
        }

        @Deprecated
        public TextDocument.MenuItem[] getVisibleMenuItems() {
            final List<MenuItemDTO> visibleMenuItems = contentManagementSystem.getInternal()
                    .getMenuService()
                    .getVisibleMenuItems(internalTextDocument.getId(), menuIndex, Imcms.getUser().getLanguage(), true);

            return convertToMenuItem(visibleMenuItems);
        }

        @Deprecated
        public Document[] getVisibleDocuments() {
            TextDocument.MenuItem[] menuItems = this.getVisibleMenuItems();
            return this.getDocumentsFromMenuItems(menuItems);
        }

        @Deprecated
        public TextDocument.MenuItem[] getMenuItems() {
            final List<MenuItemDTO> menuItems = contentManagementSystem.getInternal()
                    .getMenuService()
                    .getMenuItems(internalTextDocument.getId(), menuIndex, Imcms.getUser().getLanguage(), true, null);

            return convertToMenuItem(menuItems);
        }

        @Deprecated
        public TextDocument.MenuItem[] getPublishedMenuItems() {
            final List<MenuItemDTO> publicMenuItems = contentManagementSystem.getInternal()
                    .getMenuService()
                    .getPublicMenuItems(internalTextDocument.getId(), menuIndex, Imcms.getUser().getLanguage(), true);

            return convertToMenuItem(publicMenuItems);
        }

        @Deprecated
        public Document[] getDocuments() {
            TextDocument.MenuItem[] menuItems = this.getMenuItems();
            return this.getDocumentsFromMenuItems(menuItems);
        }

        private TextDocument.MenuItem[] convertToMenuItem(List<MenuItemDTO> menuItems) {
            return menuItems.stream()
                    .map(menuItemDTO -> {
                        final GetterDocumentReference documentReference = new GetterDocumentReference(
                                menuItemDTO.getDocumentId(), contentManagementSystem.getInternal().getDocumentMapper()
                        );

                        final MenuItemDomainObject menuItemDomainObject = new MenuItemDomainObject(documentReference);

                        return new MenuItem(menuItemDomainObject, contentManagementSystem);
                    })
                    .toArray(TextDocument.MenuItem[]::new);
        }

        private Document[] getDocumentsFromMenuItems(TextDocument.MenuItem[] menuItems) {
            Document[] documents = new Document[menuItems.length];

            for (int i = 0; i < menuItems.length; ++i) {
                TextDocument.MenuItem menuItem = menuItems[i];
                documents[i] = menuItem.getDocument();
            }

            return documents;
        }
    }

    @Deprecated
    public static class MenuItem {
        MenuItemDomainObject internalMenuItem;
        Document child;

        @Deprecated
        public MenuItem(MenuItemDomainObject internalMenuItem, ContentManagementSystem contentManagementSystem) {
            this.internalMenuItem = internalMenuItem;
            DocumentService.ApiWrappingDocumentVisitor visitor = new DocumentService.ApiWrappingDocumentVisitor(contentManagementSystem);
            internalMenuItem.getDocument().accept(visitor);
            this.child = visitor.getDocument();
        }

        @Deprecated
        public MenuItem(MenuItemDomainObject menuItem, DocumentDomainObject document, ContentManagementSystem contentManagementSystem) {
            this.internalMenuItem = menuItem;
            this.child = new Document(document, contentManagementSystem);
        }

        @Deprecated
        public Document getDocument() {
            return this.child;
        }

        @Deprecated
        public Integer getSortKey() {
            return this.internalMenuItem.getSortKey();
        }

        @Deprecated
        public void setSortKey(Integer sortKey) {
            this.internalMenuItem.setSortKey(sortKey);
        }

        @Deprecated
        public TextDocument.MenuItem.TreeKey getTreeKey() {
            return new TextDocument.MenuItem.TreeKey(this.internalMenuItem.getTreeSortKey());
        }

        @Deprecated
        public void setTreeKey(TextDocument.MenuItem.TreeKey treeKey) {
            this.internalMenuItem.setTreeSortKey(treeKey.internalTreeSortKey);
        }

        @Deprecated
        public static class TreeKey {
            TreeSortKeyDomainObject internalTreeSortKey;

            @Deprecated
            public TreeKey(TreeSortKeyDomainObject internalTreeSortKey) {
                this.internalTreeSortKey = internalTreeSortKey;
            }

            @Deprecated
            public TreeKey(String treeSortKey) {
                this.internalTreeSortKey = new TreeSortKeyDomainObject(treeSortKey);
            }

            @Deprecated
            public int getLevelCount() {
                return this.internalTreeSortKey.getLevelCount();
            }

            @Deprecated
            public int getLevelKey(int level) {
                return this.internalTreeSortKey.getLevelKey(level - 1);
            }

            @Deprecated
            public String toString() {
                return this.internalTreeSortKey.toString();
            }
        }
    }
}