package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import com.imcode.db.Database;
import com.imcode.imcms.api.orm.OrmHtmlDocument;
import com.imcode.imcms.api.orm.OrmUrlDocument;

public class DocumentCreatingVisitor extends DocumentStoringVisitor {
	
	private UserDomainObject currentUser;
    
    public DocumentCreatingVisitor(Database database, ImcmsServices services, UserDomainObject currentUser) {
        super(database, services);
        this.currentUser = currentUser;        
    }
    

    /**
     * Just set value(s) instead of SQL calls. 
     */
    // requires transaction
    public void visitHtmlDocument( HtmlDocumentDomainObject document ) {
    	/*
        String[] htmlDocumentColumns = {"meta_id", "frame_set"};

        String sqlUrlDocsInsertStr = makeSqlInsertString( "frameset_docs", htmlDocumentColumns );

        final Object[] parameters = new String[] {
                                             "" + document.getId(), document.getHtml()
                                             };
        database.execute(new SqlUpdateCommand(sqlUrlDocsInsertStr, parameters));
        */
    	OrmHtmlDocument meta = (OrmHtmlDocument)document.getMeta().getOrmDocument();
    	meta.setHtml(document.getHtml());
    	
    	// hibernateTemplate.save(meta) <- AOP-ed or saved before
    	// clone document if fields shoud be modified
    	// hibernateTemplate.save(
    }

    /**
     * Just set value(s) instead of SQL calls. 
     */    
    public void visitUrlDocument( UrlDocumentDomainObject document ) {
    	/*
        String[] urlDocumentColumns = {"meta_id", "frame_name", "target", "url_ref", "url_txt", "lang_prefix"};

        String sqlUrlDocsInsertStr = DocumentStoringVisitor.makeSqlInsertString( "url_docs", urlDocumentColumns );

        final Object[] parameters = new String[] {
                                             "" + document.getId(), "", "", document.getUrl(), "", ""
                                             };
        database.execute(new SqlUpdateCommand(sqlUrlDocsInsertStr, parameters));
        */
    	OrmUrlDocument meta = (OrmUrlDocument)document.getMeta().getOrmDocument();
    	
    	meta.setUrlTarget("");
    	meta.setUrlText("");
    	meta.setUrlLanguagePrefix("");
    	meta.setUrlFrameName("");
    	meta.setUrl(document.getUrl());
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {        
    	updateTextDocumentTemplateNames(textDocument, null, null);
        updateTextDocumentTexts( textDocument, null, currentUser);
        updateTextDocumentImages( textDocument, null, null);
        updateTextDocumentIncludes( textDocument );
        updateTextDocumentMenus( textDocument, null, null);
    }
}
