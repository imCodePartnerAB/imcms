package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.servlet.tags.Editor.TextEditor;
import imcode.server.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.parser.TagParser;
import org.apache.commons.lang3.StringUtils;

public class TextTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
        String result;
        LoopTag loopTag = (LoopTag) findAncestorWithClass(this, LoopTag.class);

        LoopEntryRef loopEntryRef = loopTag == null ? null : loopTag.getLoopEntryRef();
        String documentProp = attributes.getProperty("document");
        DocumentRequest documentRequest = parserParameters.getDocumentRequest();

        DocumentDomainObject doc = !StringUtils.isNotBlank(documentProp)
                ? documentRequest.getDocument()
                : Imcms.getServices().getDocumentMapper().getDocument(documentProp);

        boolean hasEditTexts = ((TextDocumentPermissionSetDomainObject) documentRequest.getUser().getPermissionSetFor(doc)).getEditTexts();

        if (TagParser.isEditable(attributes, hasEditTexts)) {
            String locale = documentRequest.getDocument().getLanguage().getCode();
            int textNo = Integer.parseInt(attributes.getProperty("no"));
            String contentType = "html";

            if (attributes.getProperty("formats", "").contains("text")) {
                contentType = "text";

            } else {
                TextDocumentDomainObject textDoc = (TextDocumentDomainObject) doc;
                TextDomainObject textDO = (loopTag == null)
                        ? textDoc.getText(textNo)
                        : textDoc.getText(TextDocumentDomainObject.LoopItemRef.of(loopEntryRef, textNo));

                if (textDO != null) {
                    contentType = textDO.getType() == TextDomainObject.TEXT_TYPE_PLAIN
                            ? "from-html"
                            : "html";
                }
            }

            String label = attributes.getProperty("label", "");
            String showLabel = attributes.getProperty("showlabel", "false");

            ((TextEditor) editor)
                    .setDocumentId(doc.getId())
                    .setContentType(contentType)
                    .setLabel(label)
                    .setShowlabel(showLabel)
                    .setLocale(locale)
                    .setLoopEntryRef(loopEntryRef)
                    .setNo(textNo);
        } else {
            editor = null;
        }

        result = tagParser.tagText(attributes, loopEntryRef);

        return result;
    }

    @Override
    public TextEditor createEditor() {
        return new TextEditor();
    }

    public void setRows(int rows) {
        attributes.setProperty("rows", "" + rows);
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode);
    }

    public void setFormats(String formats) {
        attributes.setProperty("formats", formats);
    }

    public void setDocument(String documentName) {
        attributes.setProperty("document", documentName);
    }

    public void setDocument(Integer documentName) {
        attributes.setProperty("document", documentName.toString());
    }

    public void setPlaceholder(String placeholder) {
        attributes.setProperty("placeholder", placeholder);
    }

    public void setShowlabel(boolean showLabel) {
        attributes.setProperty("showlabel", "" + showLabel);
    }
}
