package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.servlet.tags.Editor.BaseEditor;
import com.imcode.imcms.servlet.tags.Editor.TextEditor;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.TagParser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.tagext.TagAdapter;

public class TextTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
        TagAdapter loopTagAdapter = (TagAdapter) findAncestorWithClass(this, TagAdapter.class);
        LoopTag loopTag = loopTagAdapter != null && loopTagAdapter.getAdaptee() instanceof LoopTag
                ? (LoopTag) loopTagAdapter.getAdaptee()
                : null;

        LoopEntryRef loopEntryRef = loopTag == null ? null : loopTag.getLoopEntryRef();
        TextDocumentDomainObject doc = (TextDocumentDomainObject) (!StringUtils.isNotBlank(attributes.getProperty("document")) ?
                parserParameters.getDocumentRequest().getDocument() :
                Imcms.getServices().getDocumentMapper().getDocument(attributes.getProperty("document")));
        ((TextEditor) editor)
                .setDocumentId(doc.getId())
                .setLocale(parserParameters.getDocumentRequest().getDocument().getLanguage().getCode())
                .setLoopEntryRef(loopEntryRef)
                .setNo(Integer.parseInt(attributes.getProperty("no")));
        return tagParser.tagText(attributes, loopEntryRef);
    }

    @Override
    public BaseEditor createEditor() {
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

}
