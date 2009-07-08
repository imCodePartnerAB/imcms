package com.imcode.imcms.web.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

public class Functions {
	
	public static boolean canEditDocument(DocumentDomainObject document, PageContext pageContext) {
		UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());
		
		return user.canEdit(document);
	}
	
	public static boolean canEditDocumentInformation(DocumentDomainObject document, PageContext pageContext) {
	    UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());
	    
	    return user.canEditDocumentInformationFor(document);
	}
}
