package com.imcode.imcms.servlet.tags;

import imcode.server.parser.TagParser;
import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

public class TextTag extends SimpleImcmsTag {
	

    protected String getContent(TagParser tagParser) {
        ContentLoopTag2 clTag = (ContentLoopTag2)findAncestorWithClass(this, ContentLoopTag2.class);
        ContentLoop loop =  clTag == null ? null : clTag.getLoop();
        Content content = clTag == null ? null : clTag.getContent();


        return tagParser.tagText(attributes, loop, content);
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
