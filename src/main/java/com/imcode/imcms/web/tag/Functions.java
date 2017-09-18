package com.imcode.imcms.web.tag;

import com.imcode.imcms.api.TextDocumentViewing;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class Functions {

    public static boolean canEditDocument(DocumentDomainObject document, PageContext pageContext) {
        UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());

        return user.canEdit(document);
    }

    public static boolean canEditDocumentInformation(DocumentDomainObject document, PageContext pageContext) {
        UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());

        return user.canEditDocumentInformationFor(document);
    }

    public static int getCurrentDocId(PageContext context) {
        return TextDocumentViewing.fromRequest(context.getRequest()).getTextDocument().getId();
    }
}
