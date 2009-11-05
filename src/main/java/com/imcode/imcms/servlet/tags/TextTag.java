package com.imcode.imcms.servlet.tags;

import imcode.server.parser.TagParser;
import com.imcode.imcms.api.Content;

public class TextTag extends SimpleImcmsTag {
	

    protected String getContent(TagParser tagParser) {
        ContentLoopTag2 clTag = (ContentLoopTag2)findAncestorWithClass(this, ContentLoopTag2.class);
        Content content = clTag == null ? null : clTag.getContent();


        return tagParser.tagText(attributes, content);
    }

    public void setRows(int rows) {
        attributes.setProperty("rows", ""+rows) ;
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode) ;
    }

    public void setFormats(String formats) {
        attributes.setProperty("formats", formats) ;
    }

    public void setDocument(String documentName) {
        attributes.setProperty("document", documentName) ;
    }

}
