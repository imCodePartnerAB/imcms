package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.UrlReference;

public class DocumentCreatingVisitor extends DocumentStoringVisitor {
	
	private UserDomainObject currentUser;
    
    public DocumentCreatingVisitor(ImcmsServices services, UserDomainObject currentUser) {
        super(services);
        this.currentUser = currentUser;        
    }
    
    // requires transaction
    public void visitHtmlDocument( HtmlDocumentDomainObject document ) {
    	HtmlReference reference = new HtmlReference();
    	
    	reference.setMetaId(document.getId());
    	reference.setHtml(document.getHtml());
    	
    	MetaDao dao = (MetaDao)services.getSpringBean("metaDao");
    	
    	dao.saveHtmlReference(reference);
    }

    // requires transaction   
    public void visitUrlDocument( UrlDocumentDomainObject document ) {
    	UrlReference reference = new UrlReference();
    	
    	reference.setMetaId(document.getId());
    	reference.setUrlTarget("");
    	reference.setUrlText("");
    	reference.setUrlLanguagePrefix("");
    	reference.setUrlFrameName("");
    	reference.setUrl(document.getUrl());
    	
    	MetaDao dao = (MetaDao)services.getSpringBean("metaDao");
    	
    	dao.saveUrlReference(reference);    	
    }

    // requires transaction
    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {        
    	updateTextDocumentTemplateNames(textDocument, null, null);
        updateTextDocumentTexts( textDocument, null, currentUser);
        updateTextDocumentImages( textDocument, null, null);
        updateTextDocumentIncludes( textDocument );
        updateTextDocumentMenus( textDocument, null, null);
    }
}
