package com.imcode.imcms.web.tag;

import com.imcode.imcms.api.TextDocument;
import imcode.util.Utility;

import javax.servlet.jsp.PageContext;

public class Functions {

    public static TextDocument getDocument(String docIdentifier, PageContext pageContext) {
        return Utility.getCMS(pageContext).getDocumentService().getTextDocument(docIdentifier);
    }
}
