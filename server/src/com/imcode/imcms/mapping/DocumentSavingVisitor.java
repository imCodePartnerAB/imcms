package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import com.imcode.imcms.mapping.DocumentStoringVisitor;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.user.UserDomainObject;

public class DocumentSavingVisitor extends DocumentStoringVisitor {

    private DocumentDomainObject oldDocument;

    public DocumentSavingVisitor(UserDomainObject user, DocumentDomainObject documentInDatabase, Database database, ImcmsServices services) {
        super( user, database, services );
        this.oldDocument = documentInDatabase;
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
        String sqlStr = "UPDATE frameset_docs SET frame_set = ? WHERE meta_id = ?";
        database.executeUpdateQuery( sqlStr, new String[]{htmlDocument.getHtml(), "" + htmlDocument.getId()} );
    }

    public void visitUrlDocument( UrlDocumentDomainObject urlDocument ) {
        String sqlStr = "UPDATE url_docs SET url_ref = ? WHERE meta_id = ?";
        database.executeUpdateQuery( sqlStr, new String[]{urlDocument.getUrl(), "" + urlDocument.getId()} );
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        String sqlStr = "UPDATE text_docs SET template_id = ?, group_id = ?,\n"
                        + "default_template = ?, default_template_1 = ?, default_template_2 = ? WHERE meta_id = ?";
        TemplateDomainObject defaultTemplate = textDocument.getDefaultTemplate();
        TemplateDomainObject defaultTemplateForRestricted1 = ( (TextDocumentPermissionSetDomainObject)textDocument.getPermissionSetForRestrictedOneForNewDocuments() ).getDefaultTemplate();
        TemplateDomainObject defaultTemplateForRestricted2 = ( (TextDocumentPermissionSetDomainObject)textDocument.getPermissionSetForRestrictedTwoForNewDocuments() ).getDefaultTemplate();

        TemplateDomainObject template = textDocument.getTemplate();
        int templateGroupId = textDocument.getTemplateGroupId();
        int templateId = template.getId();

        database.executeUpdateQuery( sqlStr, new String[]{
            "" + templateId,
            "" + templateGroupId,
            (null != defaultTemplate ? "" + defaultTemplate.getId() : null),
            null != defaultTemplateForRestricted1 ? "" + defaultTemplateForRestricted1.getId() : "-1",
            null != defaultTemplateForRestricted2 ? "" + defaultTemplateForRestricted2.getId() : "-1",
            "" + textDocument.getId()
        } );

        updateTextDocumentTexts( textDocument );
        updateTextDocumentImages( textDocument );
        updateTextDocumentIncludes( textDocument );

        boolean menusChanged = !textDocument.getMenus().equals( ( (TextDocumentDomainObject)oldDocument ).getMenus() );

        if ( menusChanged ) {
            updateTextDocumentMenus( textDocument );
        }
    }
}
