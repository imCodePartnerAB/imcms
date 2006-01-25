package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlUpdateCommand;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;

public class DocumentCreatingVisitor extends DocumentStoringVisitor {

    public DocumentCreatingVisitor(Database database, ImcmsServices services) {
        super(database, services );
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject document ) {
        String[] htmlDocumentColumns = {"meta_id", "frame_set"};

        String sqlUrlDocsInsertStr = makeSqlInsertString( "frameset_docs", htmlDocumentColumns );

        final Object[] parameters = new String[] {
                                             "" + document.getId(), document.getHtml()
                                             };
        ((Integer)database.execute( new SqlUpdateCommand( sqlUrlDocsInsertStr, parameters ) )).intValue();
    }

    public void visitUrlDocument( UrlDocumentDomainObject document ) {
        String[] urlDocumentColumns = {"meta_id", "frame_name", "target", "url_ref", "url_txt", "lang_prefix"};

        String sqlUrlDocsInsertStr = DocumentStoringVisitor.makeSqlInsertString( "url_docs", urlDocumentColumns );

        final Object[] parameters = new String[] {
                                             "" + document.getId(), "", "", document.getUrl(), "", ""
                                             };
        ((Integer)database.execute( new SqlUpdateCommand( sqlUrlDocsInsertStr, parameters ) )).intValue();
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        String sqlTextDocsInsertStr = "INSERT INTO text_docs (meta_id, template_id, group_id, default_template, default_template_1, default_template_2) VALUES (?,?,?,?,?,?)";
        int templateId = textDocument.getTemplateId();
        Integer defaultTemplate = textDocument.getDefaultTemplateId();
        Integer defaultTemplateForRestricted1 = textDocument.getDefaultTemplateIdForRestricted1();
        Integer defaultTemplateForRestricted2 = textDocument.getDefaultTemplateIdForRestricted2();
        int templateGroupId = textDocument.getTemplateGroupId();
        int textDocumentId = textDocument.getId();
        final Object[] parameters = new String[] {
                "" + textDocumentId,
                "" + templateId,
                "" + templateGroupId,
                null != defaultTemplate ? "" + defaultTemplate : null,
                null != defaultTemplateForRestricted1
                ? "" + defaultTemplateForRestricted1 : "-1",
                null != defaultTemplateForRestricted2
                ? "" + defaultTemplateForRestricted2 : "-1",
                };
        ((Integer)database.execute( new SqlUpdateCommand( sqlTextDocsInsertStr, parameters ) )).intValue();
        updateTextDocumentTexts( textDocument );
        updateTextDocumentImages( textDocument );
        updateTextDocumentIncludes( textDocument );
        updateTextDocumentMenus( textDocument );
    }
}
