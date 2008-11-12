package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.api.HtmlMeta;
import com.imcode.imcms.api.UrlMeta;

public class DocumentCreatingVisitor extends DocumentStoringVisitor {
	
	private UserDomainObject currentUser;

    public DocumentCreatingVisitor(Database database, ImcmsServices services) {
        this(database, services, null);
    }
    
    public DocumentCreatingVisitor(Database database, ImcmsServices services, UserDomainObject currentUser) {
        super(database, services);
        this.currentUser = currentUser;        
    }
    

    /**
     * Just set value(s) instead of SQL calls. 
     */
    public void visitHtmlDocument( HtmlDocumentDomainObject document ) {
    	/*
        String[] htmlDocumentColumns = {"meta_id", "frame_set"};

        String sqlUrlDocsInsertStr = makeSqlInsertString( "frameset_docs", htmlDocumentColumns );

        final Object[] parameters = new String[] {
                                             "" + document.getId(), document.getHtml()
                                             };
        database.execute(new SqlUpdateCommand(sqlUrlDocsInsertStr, parameters));
        */
    	HtmlMeta meta = (HtmlMeta)document.getMeta();
    	meta.setHtml(document.getHtml());    	
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
    	UrlMeta meta = (UrlMeta)document.getMeta();
    	
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
