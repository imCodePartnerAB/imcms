package imcode.server.document;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.db.Database;

public class DocumentCreatingVisitor extends DocumentStoringVisitor {

    public DocumentCreatingVisitor( UserDomainObject user, Database database ) {
        super(user, database );
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject document ) {
        String[] htmlDocumentColumns = {"meta_id", "frame_set"};

        String sqlUrlDocsInsertStr = makeSqlInsertString( "frameset_docs", htmlDocumentColumns );

        database.sqlUpdateQuery( sqlUrlDocsInsertStr, new String[]{
            "" + document.getId(), document.getHtml()
        } );

    }

    public void visitUrlDocument( UrlDocumentDomainObject document ) {
        String[] urlDocumentColumns = {"meta_id", "frame_name", "target", "url_ref", "url_txt", "lang_prefix"};

        String sqlUrlDocsInsertStr = DocumentStoringVisitor.makeSqlInsertString( "url_docs", urlDocumentColumns );

        database.sqlUpdateQuery( sqlUrlDocsInsertStr, new String[]{
            "" + document.getId(), "", "", document.getUrl(), "", ""
        } );

    }

    public void visitTextDocument( TextDocumentDomainObject textDocument ) {
        String sqlTextDocsInsertStr = "INSERT INTO text_docs (meta_id, template_id, group_id, default_template, default_template_1, default_template_2) VALUES (?,?,?,?,?,?)";
        TemplateDomainObject textDocumentTemplate = textDocument.getTemplate();
        TemplateDomainObject defaultTemplate = textDocument.getDefaultTemplate();
        TemplateDomainObject defaultTemplateForRestricted1 = ( (TextDocumentPermissionSetDomainObject)textDocument.getPermissionSetForRestrictedOneForNewDocuments() ).getDefaultTemplate();
        TemplateDomainObject defaultTemplateForRestricted2 = ( (TextDocumentPermissionSetDomainObject)textDocument.getPermissionSetForRestrictedTwoForNewDocuments() ).getDefaultTemplate();
        int templateId = textDocumentTemplate.getId();
        int templateGroupId = textDocument.getTemplateGroupId();
        int textDocumentId = textDocument.getId();
        database.sqlUpdateQuery( sqlTextDocsInsertStr,
                                new String[]{
                                    "" + textDocumentId,
                                    "" + templateId,
                                    "" + templateGroupId,
                                    null != defaultTemplate ? "" + defaultTemplate.getId() : null,
                                    null != defaultTemplateForRestricted1 ? "" + defaultTemplateForRestricted1.getId() : "-1",
                                    null != defaultTemplateForRestricted2 ? "" + defaultTemplateForRestricted2.getId() : "-1",
                                } );

        updateTextDocumentTexts( textDocument );
        updateTextDocumentImages( textDocument );
        updateTextDocumentIncludes( textDocument );

    }
}
