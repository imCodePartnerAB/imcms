package com.imcode.imcms.servlet.tags;

import imcode.server.parser.TagParser;

public class IncludeTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
        return tagParser.tagInclude(attributes) ;
    }

    public void setPath(String path) {
        attributes.setProperty("path", path);
    }

    public void setUrl(String url) {
        attributes.setProperty("url", url);
    }

    public void setDocument(String documentId) {
        attributes.setProperty("document", documentId) ;
    }

    public void setFile(String filename) {
        attributes.setProperty("file", filename);
    }

}
