package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.servlet.tags.Editor.BaseEditor;
import imcode.server.parser.TagParser;

import javax.servlet.jsp.tagext.TagAdapter;

public class ImageTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
//        LoopTag loopTag = (LoopTag)findAncestorWithClass(this, LoopTag.class);

        TagAdapter loopTagAdapter = (TagAdapter)findAncestorWithClass(this, TagAdapter.class);
        LoopTag loopTag = loopTagAdapter != null && loopTagAdapter.getAdaptee() instanceof LoopTag
                ? (LoopTag) loopTagAdapter.getAdaptee()
                : null;

        LoopEntryRef loopEntryRef = loopTag == null ? null : loopTag.getLoopEntryRef();

        return tagParser.tagImage(attributes, loopEntryRef);
    }

    @Override
    public BaseEditor createEditor() {
        return  null;
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode);
    }

    public void setStyle(String style) {
        attributes.setProperty("style", style);
    }

    public void setStyleClass(String styleClass) {
        attributes.setProperty("class", styleClass);
    }

    public void setUsemap(String usemap) {
        attributes.setProperty("usemap", usemap);
    }

    public void setStyleId(String id) {
        attributes.setProperty("id", id);
    }

    public void setDocument(String documentName) {
        attributes.setProperty("document", documentName);
    }
}
