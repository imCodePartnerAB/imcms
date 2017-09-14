package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.servlet.tags.Editor.MenuEditor;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

public class MenuTag extends BodyTagSupport implements IEditableTag {

    private volatile int no;
    private volatile int docId = 1001;
    private volatile Properties attributes = new Properties();
    private volatile LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> menuItemsCollection;
    private volatile MenuItemDomainObject menuItem;
    private volatile String label;
    private volatile String template;
    private boolean editMode;
    private HttpServletRequest request;

    public int doStartTag() throws JspException {
        request = (HttpServletRequest) pageContext.getRequest();
        ParserParameters parserParameters = ParserParameters.fromRequest(request);

        TextDocumentDomainObject document = (docId >= 1001)
                ? Imcms.getServices().getDocumentMapper().getVersionedDocument(docId, request)
                : parserParameters.getDocumentRequest().getDocument();

        menuItemsCollection = document.getMenu(no).getMenuItemsVisibleToUserAsTree();
        editMode = TagParser.isEditable(attributes, parserParameters.isMenuMode());

        return (menuItemsCollection.size() > 0)
                ? EVAL_BODY_BUFFERED
                : SKIP_BODY;
    }

    public boolean nextMenuItem(MenuItemDomainObject menuItem) {
        if (menuItem != null) {
            DocumentDomainObject document = DocumentDomainObject.asDefaultUser(menuItem.getDocument());
            DocumentReference docIdentity = Imcms.getServices()
                    .getDocumentMapper()
                    .getDocumentReference(document);

            menuItem.setDocumentReference(docIdentity);

            this.menuItem = menuItem;
            TextDocument.MenuItem item = new TextDocument.MenuItem(
                    menuItem, document, Imcms.fromRequest(request)
            );
            pageContext.setAttribute("menuitem", item);
            return true;
        } else {
            invalidateMenuItem();
            return false;
        }
    }

    public int doAfterBody() throws JspException {
        return (menuItemsCollection.size() > 0)
                ? EVAL_BODY_AGAIN
                : SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        try {
            String bodyContentString = (null != getBodyContent())
                    ? getBodyContent().getString()
                    : "";
            bodyContent = null;

            if (editMode) {
                bodyContentString = createEditor().setNo(no)
                        .setDocumentId(docId)
                        .wrap(bodyContentString);
            }

            bodyContentString = TagParser.addPreAndPost(attributes, bodyContentString);
            pageContext.getOut().write(bodyContentString);

        } catch (IOException | RuntimeException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    public LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> getList() {
        return menuItemsCollection;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode);
    }

    public void setPre(String pre) {
        attributes.setProperty("pre", pre);
    }

    public void setPost(String post) {
        attributes.setProperty("post", post);
    }

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
