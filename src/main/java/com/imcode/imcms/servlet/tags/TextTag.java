package com.imcode.imcms.servlet.tags;

import imcode.server.parser.TagParser;

public class TextTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
        return tagParser.tagText(attributes);
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
