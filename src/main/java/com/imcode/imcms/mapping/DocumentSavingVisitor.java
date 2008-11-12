package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.api.HtmlMeta;
import com.imcode.imcms.api.UrlMeta;

public class DocumentSavingVisitor extends DocumentStoringVisitor {

    private DocumentDomainObject oldDocument;
    private UserDomainObject savingUser;

    public DocumentSavingVisitor(DocumentDomainObject documentInDatabase, Database database,
                                 ImcmsServices services, UserDomainObject user) {
        super(database, services );
        oldDocument = documentInDatabase;
        savingUser = user;
    }

    /**
     * Just set value(s) instead of SQL calls. 
     */
    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
    	/*
        String sqlStr = "UPDATE frameset_docs SET frame_set = ? WHERE meta_id = ?";
        final Object[] parameters = new String[]{htmlDocument.getHtml(), "" + htmlDocument.getId()};
        database.execute(new SqlUpdateCommand(sqlStr, parameters));
        */
    	HtmlMeta meta = (HtmlMeta)htmlDocument.getMeta();
    	meta.setHtml(htmlDocument.getHtml());       	
    }

    /**
     * Just set value(s) instead of SQL calls. 
     */    
    public void visitUrlDocument( UrlDocumentDomainObject urlDocument ) {
    	/*
        String sqlStr = "UPDATE url_docs SET url_ref = ? WHERE meta_id = ?";
        final Object[] parameters = new String[]{urlDocument.getUrl(), "" + urlDocument.getId()};
        database.execute(new SqlUpdateCommand(sqlStr, parameters));
        */
    	UrlMeta meta = (UrlMeta)urlDocument.getMeta();    	
    	meta.setUrl(urlDocument.getUrl());    	
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        updateTextDocumentTemplateNames(textDocument, (TextDocumentDomainObject)oldDocument, savingUser);
        
        updateTextDocumentTexts( textDocument, (TextDocumentDomainObject)oldDocument, savingUser);
        updateTextDocumentImages( textDocument, (TextDocumentDomainObject)oldDocument, savingUser);
        updateTextDocumentIncludes( textDocument );

        boolean menusChanged = !textDocument.getMenus().equals( ( (TextDocumentDomainObject)oldDocument ).getMenus() );

        if ( menusChanged ) {
            updateTextDocumentMenus( textDocument, (TextDocumentDomainObject) oldDocument, savingUser);
        }
    }
}
