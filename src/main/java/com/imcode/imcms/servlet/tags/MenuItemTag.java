package com.imcode.imcms.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class MenuItemTag extends TagSupport {

    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        MenuTag menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class) ;
        if (menuTag == null) {
            throw new JspTagException("menuitem must be enclosed in a menuloop or menu.") ;
        }
        menuTag.invalidateMenuItem();
        return EVAL_PAGE;
    }
}
