package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.ContentManagementSystem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class VariablesTag extends TagSupport {

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        pageContext.setAttribute("cms", cms);
        pageContext.setAttribute("user", cms.getCurrentUser());
        return SKIP_BODY;
    }
}
