package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.UrlReference;
import org.springframework.transaction.annotation.Transactional;

/**
 * Not a public API. Must not be used directly.
 */
public class DocumentCreatingVisitor extends DocumentStoringVisitor {
	
	private UserDomainObject currentUser;
    
    public DocumentCreatingVisitor(ImcmsServices services, UserDomainObject currentUser) {
        super(services);
        this.currentUser = currentUser;        
    }
    
    @Transactional
    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
    	HtmlReference reference = new HtmlReference();
    	
    	reference.setDocId(document.getMeta().getId());
        reference.setDocVersionNo(document.getVersionNo());
    	reference.setHtml(document.getHtml());
    	
    	MetaDao dao = services.getSpringBean(MetaDao.class);
    	
    	dao.saveHtmlReference(reference);
    }

    @Transactional
    public void visitUrlDocument( UrlDocumentDomainObject document ) {
    	UrlReference reference = new UrlReference();
    	
    	reference.setDocId(document.getMeta().getId());
        reference.setDocVersionNo(document.getVersionNo());
    	reference.setUrlTarget("");
    	reference.setUrlText("");
    	reference.setUrlLanguagePrefix("");
    	reference.setUrlFrameName("");
    	reference.setUrl(document.getUrl());
    	
    	MetaDao dao = services.getSpringBean(MetaDao.class);
    	
    	dao.saveUrlReference(reference);    	
    }

    @Transactional
    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        updateTextDocumentContentLoops(textDocument, currentUser);
        updateTextDocumentTemplateNames(textDocument, currentUser);
        updateTextDocumentTexts(textDocument, currentUser);
        updateTextDocumentImages(textDocument, currentUser);
        updateTextDocumentIncludes(textDocument);
        updateTextDocumentMenus(textDocument, currentUser);
    }
}
