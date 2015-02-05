package com.imcode.imcms.servlet.tags;

import imcode.server.document.textdocument.MenuItemDomainObject;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.LinkedList;

public class MenuLoopTag extends TagSupport {
    private volatile MenuItemDomainObject.TreeMenuItemDomainObject currentItem;
    private LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> itemsCollection;

    public int doStartTag() throws JspException {
        MenuTag menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class);
        MenuLoopTag parentMenuLoop = (MenuLoopTag) findAncestorWithClass(this, MenuLoopTag.class);
        if (null == menuTag) {
            throw new JspTagException("menuloop must be enclosed in menu.");
        }
        itemsCollection = parentMenuLoop != null ? parentMenuLoop.getList() : menuTag.getList();
        if (nextMenuItem() && menuTag.nextMenuItem(currentItem.getMenuItem()))
            return EVAL_BODY_INCLUDE;
        return SKIP_BODY;
    }

    public int doAfterBody() throws JspException {
        MenuTag menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class);
        if (!nextMenuItem() || !menuTag.nextMenuItem(currentItem.getMenuItem())) {
            return SKIP_BODY;
        }
        return EVAL_BODY_AGAIN;
    }

    public LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> getList() {
        return currentItem.getSubMenuItems();
    }

    public boolean nextMenuItem() {
        currentItem = itemsCollection.poll();
        return currentItem != null;
    }

}
