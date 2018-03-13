package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.Template;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
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
        return contentManagementSystem.getTemplateService()
                .getTemplateOptional(templateName)
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
}