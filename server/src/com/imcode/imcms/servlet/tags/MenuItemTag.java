package com.imcode.imcms.servlet.tags;

import imcode.server.document.textdocument.MenuItemDomainObject;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.api.ContentManagementSystem;

public class MenuItemTag extends TagSupport {

    private MenuTag menuTag;

    public int doStartTag() throws JspException {
        menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class) ;
        if (menuTag == null) {
            throw new JspTagException("menuitem must be enclosed in a menuloop or menu.") ;
        }
        MenuItemDomainObject menuItem = menuTag.getMenuItem();
        pageContext.setAttribute(getId(), new TextDocument.MenuItem(menuItem, ContentManagementSystem.fromRequest(pageContext.getRequest()))) ;
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        menuTag.invalidateMenuItem();
        return EVAL_PAGE;
    }
}
