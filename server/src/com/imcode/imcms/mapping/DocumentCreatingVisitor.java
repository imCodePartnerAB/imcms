package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.db.DatabaseUtils;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
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
        DatabaseUtils.executeUpdate(database, sqlUrlDocsInsertStr, parameters);
    }

    public void visitUrlDocument( UrlDocumentDomainObject document ) {
        String[] urlDocumentColumns = {"meta_id", "frame_name", "target", "url_ref", "url_txt", "lang_prefix"};

        String sqlUrlDocsInsertStr = DocumentStoringVisitor.makeSqlInsertString( "url_docs", urlDocumentColumns );

        final Object[] parameters = new String[] {
                                             "" + document.getId(), "", "", document.getUrl(), "", ""
                                             };
        DatabaseUtils.executeUpdate(database, sqlUrlDocsInsertStr, parameters);
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        String sqlTextDocsInsertStr = "INSERT INTO text_docs (meta_id, template_id, group_id, default_template, default_template_1, default_template_2) VALUES (?,?,?,?,?,?)";
        TemplateDomainObject textDocumentTemplate = textDocument.getTemplate();
        TemplateDomainObject defaultTemplate = textDocument.getDefaultTemplate();
        TemplateDomainObject defaultTemplateForRestricted1 = ( (TextDocumentPermissionSetDomainObject)textDocument.getPermissionSetForRestrictedOneForNewDocuments() ).getDefaultTemplate();
        TemplateDomainObject defaultTemplateForRestricted2 = ( (TextDocumentPermissionSetDomainObject)textDocument.getPermissionSetForRestrictedTwoForNewDocuments() ).getDefaultTemplate();
        int templateId = textDocumentTemplate.getId();
        int templateGroupId = textDocument.getTemplateGroupId();
        int textDocumentId = textDocument.getId();
        final Object[] parameters = new String[] {
                "" + textDocumentId,
                "" + templateId,
                "" + templateGroupId,
                null != defaultTemplate ? "" + defaultTemplate.getId() : null,
                null != defaultTemplateForRestricted1
                ? "" + defaultTemplateForRestricted1.getId() : "-1",
                null != defaultTemplateForRestricted2
                ? "" + defaultTemplateForRestricted2.getId() : "-1",
                };
        DatabaseUtils.executeUpdate(database, sqlTextDocsInsertStr, parameters);
        updateTextDocumentTexts( textDocument );
        updateTextDocumentImages( textDocument );
        updateTextDocumentIncludes( textDocument );
        updateTextDocumentMenus( textDocument );
    }
}
