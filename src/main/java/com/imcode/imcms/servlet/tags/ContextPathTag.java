package com.imcode.imcms.servlet.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

// todo: remove after new tag implementation
@Deprecated
public class ContextPathTag extends TagSupport {

    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().print(((HttpServletRequest) pageContext.getRequest()).getContextPath());
        } catch (Exception e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

}
