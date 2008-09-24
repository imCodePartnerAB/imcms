package com.imcode.imcms.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class MenuLoopTag extends TagSupport {

    public int doStartTag() throws JspException {
        MenuTag menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class);
        if (null == menuTag) {
            throw new JspTagException("menuloop must be enclosed in menu.") ;
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doAfterBody() throws JspException {
        MenuTag menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class);
        if (!menuTag.nextMenuItem()) {
            return SKIP_BODY;
        }
        return EVAL_BODY_AGAIN;
    }

}
