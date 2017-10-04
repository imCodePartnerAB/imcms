package com.imcode.imcms.web.tag;

import com.imcode.imcms.api.TextDocument;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.util.Map;

public class Functions {

    public static boolean canEditDocument(DocumentDomainObject document, PageContext pageContext) {
        UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());

        return user.canEdit(document);
    }

    public static boolean canEditDocumentInformation(DocumentDomainObject document, PageContext pageContext) {
        UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());

        return user.canEditDocumentInformationFor(document);
    }

    public static TextDocument getDocument(String docIdentifier, PageContext pageContext) {
        return Utility.getCMS(pageContext).getDocumentService().getTextDocument(docIdentifier);
    }

    @SuppressWarnings("unchecked")
    public static TextDocument.LoopItem createLoopItem(Object entry, int loopNo,
                                                       TextDocumentDomainObject document) {
        // this cast have to be done since JSP processor doesn't understand interface in interface or something
        return new TextDocument.LoopItem(((Map.Entry<Integer, Boolean>) entry), loopNo, document);
    }
}
