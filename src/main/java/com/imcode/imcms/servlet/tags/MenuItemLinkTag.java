package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.servlet.tags.Editor.MenuItemEditor;
import com.imcode.imcms.servlet.tags.Editor.SupportEditor;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.parser.MenuParser;
import imcode.server.parser.ParserParameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class MenuItemLinkTag extends TagSupport implements IEditableTag {
    private SupportEditor editor;
    private ParserParameters parserParameters;
    private volatile String classes;

    public int doStartTag() throws JspException {
        MenuTag menuTag = (MenuTag) findAncestorWithClass(this, MenuTag.class);
        if (menuTag == null) {
            throw new JspTagException("menuitem must be enclosed in a menuloop or menu.");
        }
        MenuItemDomainObject menuItem = menuTag.getMenuItem();
        if (null == menuItem) {
            return SKIP_BODY;
        }
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        parserParameters = ParserParameters.fromRequest(request);
        DocumentDomainObject document = menuItem.getDocument();
        int docId = document.getId();
        String pathToDocument = MenuParser.getPathToDocument(request, document, menuTag.getTemplate(), parserParameters);
        editor = createEditor().setId(menuItem.getDocumentReference().getDocumentId())
                .setPosition(menuItem.getSortKey())
                .setTreePosition(menuItem.getTreeSortIndex())
                .setMenuName(document.getHeadline());
        try {
            if (parserParameters.isAnyMode())
                pageContext.getOut().print(editor.getWrapperPre());
            pageContext.getOut().print("<a id=\"" + docId + (classes == null ? "" : "\" class=\"" + classes) + "\" href=\"" + pathToDocument + "\" target=\"" + document.getTarget() + "\">");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print("</a>");
            if (parserParameters.isAnyMode())
                pageContext.getOut().print(editor.getWrapperPost());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    @Override
    public MenuItemEditor createEditor() {
        return new MenuItemEditor();
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }
}
