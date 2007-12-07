package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlUpdateCommand;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

public class DocumentCreatingVisitor extends DocumentStoringVisitor {
	
	private UserDomainObject currentUser;

    public DocumentCreatingVisitor(Database database, ImcmsServices services) {
        this(database, services, null);
    }
    
    public DocumentCreatingVisitor(Database database, ImcmsServices services, UserDomainObject currentUser) {
        super(database, services);
        this.currentUser = currentUser;        
    }
    

    public void visitHtmlDocument( HtmlDocumentDomainObject document ) {
        String[] htmlDocumentColumns = {"meta_id", "frame_set"};

        String sqlUrlDocsInsertStr = makeSqlInsertString( "frameset_docs", htmlDocumentColumns );

        final Object[] parameters = new String[] {
                                             "" + document.getId(), document.getHtml()
                                             };
        database.execute(new SqlUpdateCommand(sqlUrlDocsInsertStr, parameters));
    }

    public void visitUrlDocument( UrlDocumentDomainObject document ) {
        String[] urlDocumentColumns = {"meta_id", "frame_name", "target", "url_ref", "url_txt", "lang_prefix"};

        String sqlUrlDocsInsertStr = DocumentStoringVisitor.makeSqlInsertString( "url_docs", urlDocumentColumns );

        final Object[] parameters = new String[] {
                                             "" + document.getId(), "", "", document.getUrl(), "", ""
                                             };
        database.execute(new SqlUpdateCommand(sqlUrlDocsInsertStr, parameters));
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        String sqlTextDocsInsertStr = "INSERT INTO text_docs (meta_id, template_name, group_id, default_template, default_template_1, default_template_2) VALUES (?,?,?,?,?,?)";
        String templateName = textDocument.getTemplateName();
        String defaultTemplate = textDocument.getDefaultTemplateName();
        String defaultTemplateForRestricted1 = textDocument.getDefaultTemplateNameForRestricted1();
        String defaultTemplateForRestricted2 = textDocument.getDefaultTemplateNameForRestricted2();
        int templateGroupId = textDocument.getTemplateGroupId();
        int textDocumentId = textDocument.getId();
        final Object[] parameters = new String[] {
                "" + textDocumentId,
                "" + templateName,
                "" + templateGroupId,
                null != defaultTemplate ? "" + defaultTemplate : null,
                null != defaultTemplateForRestricted1 ? "" + defaultTemplateForRestricted1 : null,
                null != defaultTemplateForRestricted2 ? "" + defaultTemplateForRestricted2 : null,
                };
        database.execute(new SqlUpdateCommand(sqlTextDocsInsertStr, parameters));
        updateTextDocumentTexts( textDocument, null, currentUser);
        updateTextDocumentImages( textDocument, null, null);
        updateTextDocumentIncludes( textDocument );
        updateTextDocumentMenus( textDocument, null, null);
    }
}
