package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.UrlReference;

/**
 * Not a public API. Must not be used directly.
 */
public class DocumentSavingVisitor extends DocumentStoringVisitor {

	/**
	 * Current version of a document.
	 */
    private DocumentDomainObject oldDocument;
    
    /**
     * An user performing save operation. 
     */
    private UserDomainObject savingUser;

    public DocumentSavingVisitor(DocumentDomainObject documentInDatabase,
                                 ImcmsServices services, UserDomainObject user) {
        super(services);
        oldDocument = documentInDatabase;
        savingUser = user;
    }

    // runs inside transaction   
    public void visitHtmlDocument( HtmlDocumentDomainObject document ) {
    	MetaDao dao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    	
    	HtmlReference htmlReference = new HtmlReference();
    	
    	htmlReference.setMetaId(document.getMeta().getId());
    	htmlReference.setHtml(document.getHtml());
    	    	
    	dao.saveHtmlReference(htmlReference);
    }

    // runs inside transaction   
    public void visitUrlDocument( UrlDocumentDomainObject document ) {
    	MetaDao dao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    	
    	UrlReference reference = new UrlReference();
    	reference.setMetaId(document.getMeta().getId());
    	reference.setUrl(document.getUrl());
    	
    	dao.saveUrlReference(reference);    	
    }

    // runs inside transaction 
    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        updateTextDocumentTemplateNames(textDocument, (TextDocumentDomainObject)oldDocument, savingUser);        
        updateTextDocumentTexts( textDocument, (TextDocumentDomainObject)oldDocument, savingUser);
        updateTextDocumentImages( textDocument, (TextDocumentDomainObject)oldDocument, savingUser);
        updateTextDocumentIncludes( textDocument );
        //updateTextDocumentContentLoops ( textDocument, null, null );

        if (oldDocument != null) {
	        boolean menusChanged = !textDocument.getMenus().equals( ( (TextDocumentDomainObject)oldDocument ).getMenus() );
	
	        if ( menusChanged ) {
	            updateTextDocumentMenus( textDocument, (TextDocumentDomainObject) oldDocument, savingUser);
	        }
        }
    }
}
