package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.servlet.tags.Editor.BaseEditor;
import com.imcode.imcms.servlet.tags.Editor.TextEditor;
import imcode.server.parser.TagParser;

import javax.servlet.jsp.tagext.TagAdapter;

public class TextTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
//        LoopTag loopTag = (LoopTag)findAncestorWithClass(this, LoopTag.class);

        TagAdapter loopTagAdapter = (TagAdapter) findAncestorWithClass(this, TagAdapter.class);
        LoopTag loopTag = loopTagAdapter != null && loopTagAdapter.getAdaptee() instanceof LoopTag
                ? (LoopTag) loopTagAdapter.getAdaptee()
                : null;

        LoopEntryRef loopEntryRef = loopTag == null ? null : loopTag.getLoopEntryRef();
        ((TextEditor) editor)
                .setLocale("en")
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
