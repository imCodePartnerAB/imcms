package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.servlet.tags.Editor.MenuEditor;
import imcode.server.Imcms;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.MenuParser;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

public class MenuTag extends BodyTagSupport implements EditableTag {

    private volatile int no;
    private volatile int docId = -1;
    private volatile Properties attributes = new Properties();
    //private volatile Iterator<MenuItemDomainObject> menuItemIterator;
    private volatile LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> menuItemsCollection;
    private volatile MenuItemDomainObject menuItem;
    private volatile MenuDomainObject menu;
    private volatile String label;
    private volatile String template;

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        ParserParameters parserParameters = ParserParameters.fromRequest(request);
        TextDocumentDomainObject document;
        if (docId > -1)
            document = Imcms.getServices().getDocumentMapper().getDocument(docId);
        else
            document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
        menu = document.getMenu(no);
        //MenuItemDomainObject[] menuItems = menu.getMenuItems();
        menuItemsCollection = menu.getMenuItemsVisibleToUserAsTree();
        // menuItemIterator = new FilterIterator(new ArrayIterator(menuItems), new MenuParser.UserCanSeeMenuItemPredicate(parserParameters.getDocumentRequest().getUser()));
        if (menuItemsCollection.size() > 0) {
            // nextMenuItem();
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    public boolean nextMenuItem(MenuItemDomainObject menuItem) {
        if (menuItem != null) {
            this.menuItem = menuItem;
            pageContext.setAttribute("menuitem", new TextDocument.MenuItem(menuItem, ContentManagementSystem.fromRequest(pageContext.getRequest())));
            return true;
        } else {
            invalidateMenuItem();
            return false;
        }
    }

    public int doAfterBody() throws JspException {
        if (menuItemsCollection.size() > 0) {
            //nextMenuItem();
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }
    }

    public int doEndTag() throws JspException {
        try {
            String bodyContentString = null != getBodyContent() ? getBodyContent().getString() : "";
            bodyContent = null;
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
            /*bodyContentString = MenuParser.addMenuAdmin(no,
                    parserParameters.isMenuMode(),
                    bodyContentString, menu, request, response, label);*/
            if (parserParameters.isAnyMode())
                bodyContentString = createEditor().setNo(no).wrap(bodyContentString);
            bodyContentString = TagParser.addPreAndPost(attributes, bodyContentString);
            pageContext.getOut().write(bodyContentString);
        } catch (IOException e) {
            throw new JspException(e);
        } catch (RuntimeException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    public LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> getList() {
        return menuItemsCollection;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPre(String pre) {
        attributes.setProperty("pre", pre);
    }

    public void setPost(String post) {
        attributes.setProperty("post", post);
    }

   /* public Iterator<MenuItemDomainObject> getMenuItemIterator() {
        return menuItemIterator;
    }*/

    public MenuItemDomainObject getMenuItem() {
        if (null == menuItem) {
            nextMenuItem(null); //fixme: Hey, something wrong here
        }
        return menuItem;
    }

    public void invalidateMenuItem() {
        menuItem = null;
        pageContext.removeAttribute("menuitem");
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public MenuEditor createEditor() {
        return new MenuEditor();
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }
}
