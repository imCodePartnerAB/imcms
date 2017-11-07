package com.imcode.imcms.web.tag;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.Utility;

import javax.servlet.jsp.PageContext;

public class Functions {

    public static TextDocumentDomainObject getTextDocumentDomainObject(String docIdentifier, PageContext pageContext) {
        return Utility.getCMS(pageContext).getDocumentService().getTextDocument(docIdentifier).getInternal();
    }
}
