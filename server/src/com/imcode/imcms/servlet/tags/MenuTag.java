package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.TextDocument;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.MenuParser;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.FilterIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class MenuTag extends BodyTagSupport {

    private int no;
    private Properties attributes = new Properties();
    private Iterator<MenuItemDomainObject> menuItemIterator;
    private MenuItemDomainObject menuItem ;
    private String template;
    private MenuDomainObject menu;
    private String label;

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        ParserParameters parserParameters = ParserParameters.fromRequest(request);
        TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
        menu = document.getMenu(no);
        MenuItemDomainObject[] menuItems = menu.getMenuItems();
        menuItemIterator = new FilterIterator(new ArrayIterator(menuItems), new MenuParser.UserCanSeeMenuItemPredicate(parserParameters.getDocumentRequest().getUser())) ;
        if (menuItemIterator.hasNext()) {
            nextMenuItem();
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    public boolean nextMenuItem() {
        if (menuItemIterator.hasNext()) {
            menuItem = menuItemIterator.next();
            pageContext.setAttribute("menuitem", new TextDocument.MenuItem(menuItem, ContentManagementSystem.fromRequest(pageContext.getRequest()))) ;
            return true ;
        } else {
            invalidateMenuItem();
            return false;
        }
    }

    public int doAfterBody() throws JspException {
        if ( menuItemIterator.hasNext() ) {
            nextMenuItem();
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }
    }

    public int doEndTag() throws JspException {
        try {
            String bodyContentString = null != getBodyContent() ? getBodyContent().getString() : "";
            bodyContent = null ;
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
            bodyContentString = MenuParser.addMenuAdmin(no,
                                                        parserParameters.isMenuMode(),
                                                        bodyContentString, menu, request,response,label);
            bodyContentString = TagParser.addPreAndPost(attributes, bodyContentString);
            pageContext.getOut().write(bodyContentString);
        } catch ( IOException e ) {
            throw new JspException(e);
        } catch ( ServletException e ) {
            throw new JspException(e);
        } catch ( RuntimeException e ) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    public void setNo(int no) {
        this.no = no ;
    }

    public int getNo() {
        return no ;
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode) ;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPre(String pre) {
        attributes.setProperty("pre", pre) ;
    }

    public void setPost(String post) {
        attributes.setProperty("post", post) ;
    }

    public Iterator<MenuItemDomainObject> getMenuItemIterator() {
        return menuItemIterator;
    }

    public MenuItemDomainObject getMenuItem() {
        if (null == menuItem) {
            nextMenuItem();
        }
        return menuItem;
    }

    public void invalidateMenuItem() {
        menuItem = null ;
        pageContext.removeAttribute("menuitem");
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
