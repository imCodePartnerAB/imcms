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

public class TextTag extends TagSupport {

    private Properties attributes = new Properties();

    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
            ImcmsServices services = Imcms.getServices();

            TagParser tagParser = new TagParser(new TextDocumentParser(services), parserParameters, 0);
            String textTag = tagParser.tagText(attributes);
            pageContext.getOut().print(textTag);
        } catch ( Exception e ) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    public void setNo(int no) {
        attributes.setProperty("no", ""+no) ;
    }
    
    public void setLabel(String label) {
        attributes.setProperty("label", label) ;
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
