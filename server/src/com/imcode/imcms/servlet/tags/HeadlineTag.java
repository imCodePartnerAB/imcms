package com.imcode.imcms.servlet.tags;

import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;
import imcode.server.parser.TextDocumentParser;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;

public class HeadlineTag extends TagSupport {

    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
            DocumentDomainObject document = parserParameters.getDocumentRequest().getDocument();
            String headline = document.getHeadline();
            pageContext.getOut().print(StringEscapeUtils.escapeHtml(headline));
        } catch ( Exception e ) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }
}
