package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;
import imcode.server.parser.TagParser;

public class ImageTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
        ContentLoopTag2 clTag = (ContentLoopTag2)findAncestorWithClass(this, ContentLoopTag2.class);
        ContentLoop loop =  clTag == null ? null : clTag.getLoop();
        Content content = clTag == null ? null : clTag.getCurrentContent();


        return tagParser.tagImage(attributes, loop, content);
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode) ;
    }

    public void setStyle(String style) {
        attributes.setProperty("style", style) ;
    }

    public void setStyleClass(String styleClass) {
        attributes.setProperty("class", styleClass) ;
    }

    public void setUsemap(String usemap) {
        attributes.setProperty("usemap", usemap) ;
    }
    
    public void setStyleId(String id) {
        attributes.setProperty("id", id) ;
    }

    public void setDocument(String documentName) {
        attributes.setProperty("document", documentName) ;
    }
}
