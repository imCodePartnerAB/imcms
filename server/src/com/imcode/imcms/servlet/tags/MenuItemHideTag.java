package com.imcode.imcms.servlet.tags;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class MenuItemHideTag extends TagSupport {

    public int doStartTag() throws JspException {
        MenuTag menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class) ;
        if (menuTag == null) {
            throw new JspTagException("menuitem must be enclosed in a menuloop or menu.") ;
        }
        if ( null == menuTag.getMenuItem() ) {
            return SKIP_BODY;
        }
        return EVAL_BODY_INCLUDE;
    }
}
