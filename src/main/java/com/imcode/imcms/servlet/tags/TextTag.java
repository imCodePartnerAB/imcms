package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.servlet.tags.Editor.TextEditor;
import imcode.server.Imcms;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.TagParser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.tagext.TagAdapter;

public class TextTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
        String result;
        TagAdapter loopTagAdapter = (TagAdapter) findAncestorWithClass(this, TagAdapter.class);
        LoopTag loopTag = loopTagAdapter != null && loopTagAdapter.getAdaptee() instanceof LoopTag
                ? (LoopTag) loopTagAdapter.getAdaptee()
                : null;

        LoopEntryRef loopEntryRef = loopTag == null ? null : loopTag.getLoopEntryRef();
        TextDocumentDomainObject doc = (TextDocumentDomainObject) (!StringUtils.isNotBlank(attributes.getProperty("document")) ?
                parserParameters.getDocumentRequest().getDocument() :
                Imcms.getServices().getDocumentMapper().getDocument(attributes.getProperty("document")));
        if (TagParser.isEditable(attributes,
                ((TextDocumentPermissionSetDomainObject) parserParameters.getDocumentRequest().getUser().getPermissionSetFor(doc)).getEditTexts())) {
            ((TextEditor) editor)
                    .setDocumentId(doc.getId())
                    .setContentType(attributes.getProperty("formats", "").contains("text") ? "text" : "html")
                    .setLocale(parserParameters.getDocumentRequest().getDocument().getLanguage().getCode())
                    .setLoopEntryRef(loopEntryRef)
                    .setNo(Integer.parseInt(attributes.getProperty("no")));
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
}
