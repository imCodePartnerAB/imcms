package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.tags.Editor.MenuEditor;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;
import org.apache.commons.lang3.BooleanUtils;

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

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		ParserParameters parserParameters = ParserParameters.fromRequest(request);

        TextDocumentDomainObject document;

        if (isValidDocId()) {
            if (isWorkingDocumentVersion(request)) {
                document = Imcms.getServices().getDocumentMapper().getWorkingDocument(docId);

            } else {
                document = Imcms.getServices().getDocumentMapper().getDocument(docId);
            }
        } else {
            document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
        }

		menuItemsCollection = document.getMenu(no).getMenuItemsVisibleToUserAsTree();

        return (menuItemsCollection.size() > 0)
                ? EVAL_BODY_BUFFERED
                : SKIP_BODY;
	}

    private boolean isWorkingDocumentVersion(HttpServletRequest request) {
        return ("" + ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS).equals(request.getParameter("flags"))
                || BooleanUtils.toBoolean(request.getParameter(ImcmsConstants.REQUEST_PARAM__WORKING_PREVIEW));
    }

    private boolean isValidDocId() {
        return (docId >= 1001);
    }

	public boolean nextMenuItem(MenuItemDomainObject menuItem) {
		if (menuItem != null) {
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            DocumentDomainObject document = DocumentDomainObject.asDefaultUser(menuItem.getDocument());
            DocumentReference docIdentity = documentMapper.getDocumentReference(document);
            menuItem.setDocumentReference(docIdentity);

			this.menuItem = menuItem;
			pageContext.setAttribute("menuitem", new TextDocument.MenuItem(menuItem, document, Imcms.fromRequest(pageContext.getRequest())));
			return true;
		} else {
			invalidateMenuItem();
			return false;
		}
	}

	public int doAfterBody() throws JspException {
		if (menuItemsCollection.size() > 0) {
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
			ParserParameters parserParameters = ParserParameters.fromRequest(request);
			if (TagParser.isEditable(attributes, parserParameters.isMenuMode()))
				bodyContentString = createEditor().setNo(no).setDocumentId(docId).wrap(bodyContentString);
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

	public void setNo(int no) {
		this.no = no;
	}

	public int getNo() {
		return no;
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
