package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.TextDocument;
import imcode.server.document.textdocument.MenuItemDomainObject;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.List;

/**
 * Created by Shadowgun on 29.01.2015.
 */
public class SubMenuTag extends BodyTagSupport {
    private List<MenuItemDomainObject> items;
    private boolean isClosed;
    @Override
    public int doStartTag() throws JspException {
        MenuTag menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class);
        return super.doStartTag();
    }

    @Override
    public void doInitBody() throws JspException {
        super.doInitBody();
    }

    @Override
    public int doAfterBody() throws JspException {
        return super.doAfterBody();
    }

    @Override
    public int doEndTag() throws JspException {
        return super.doEndTag();
    }
}
