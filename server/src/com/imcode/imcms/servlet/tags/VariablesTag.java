package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.TextDocumentViewing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class VariablesTag extends TagSupport {

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        TextDocumentViewing viewing = TextDocumentViewing.fromRequest(request);
        pageContext.setAttribute("viewing", viewing) ;
        pageContext.setAttribute("document", viewing.getTextDocument()) ;
        return SKIP_BODY;
    }
}
