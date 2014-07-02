package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.mapping.container.LoopEntryRef;
import imcode.server.parser.TagParser;

public class TextTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
        LoopTag loopTag = (LoopTag)findAncestorWithClass(this, LoopTag.class);
        LoopEntryRef loopEntryRef = loopTag == null ? null : (LoopEntryRef) pageContext.getAttribute("loopEntryRef");

        return tagParser.tagText(attributes, loopEntryRef);
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
