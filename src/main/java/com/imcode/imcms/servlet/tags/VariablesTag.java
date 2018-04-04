package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.TextDocumentViewing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

public class VariablesTag extends TagSupport {

    public int doStartTag() {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        TextDocumentViewing viewing = TextDocumentViewing.fromRequest(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        pageContext.setAttribute("cms", cms);
        pageContext.setAttribute("user", cms.getCurrentUser());
        pageContext.setAttribute("viewing", viewing);
        pageContext.setAttribute("document", viewing.getTextDocument());
        return SKIP_BODY;
    }
}
