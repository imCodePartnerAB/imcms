package com.imcode.imcms.servlet.tags;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;
import imcode.server.parser.TextDocumentParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Properties;

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

}
