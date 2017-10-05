//package com.imcode.imcms.servlet.tags;
//
//import imcode.server.document.textdocument.MenuItemDomainObject;
//
//import javax.servlet.jsp.JspException;
//import javax.servlet.jsp.JspTagException;
//import javax.servlet.jsp.tagext.TagSupport;
//import java.util.LinkedList;
//
//public class MenuLoopTag extends TagSupport {
//    private volatile MenuItemDomainObject.TreeMenuItemDomainObject currentItem;
//    private LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> itemsCollection;
//    private MenuTag menuTag;
//
//    public int doStartTag() throws JspException {
//        menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class);
//        MenuLoopTag parentMenuLoop = (MenuLoopTag) findAncestorWithClass(this, MenuLoopTag.class);
//
//        if (null == menuTag) {
//            throw new JspTagException("menuloop must be enclosed in menu.");
//        }
//
//        itemsCollection = (parentMenuLoop != null)
//                ? parentMenuLoop.getList()
//                : menuTag.getList();
//
//        return (nextMenuItem() && menuTag.nextMenuItem(currentItem.getMenuItem()))
//                ? EVAL_BODY_INCLUDE
//                : SKIP_BODY;
//    }
//
//    public int doAfterBody() throws JspException {
//        return (!nextMenuItem() || !menuTag.nextMenuItem(currentItem.getMenuItem()))
//                ? SKIP_BODY
//                : EVAL_BODY_AGAIN;
//    }
//
//    public LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> getList() {
//        return currentItem.getSubMenuItems();
//    }
//
//    public boolean nextMenuItem() {
//        currentItem = itemsCollection.poll();
//        return (currentItem != null);
//    }
//}
