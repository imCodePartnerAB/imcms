package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlUpdateCommand;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;

public class DocumentSavingVisitor extends DocumentStoringVisitor {

    private DocumentDomainObject oldDocument;

    public DocumentSavingVisitor(DocumentDomainObject documentInDatabase, Database database,
                                 ImcmsServices services) {
        super(database, services );
        this.oldDocument = documentInDatabase;
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
        String sqlStr = "UPDATE frameset_docs SET frame_set = ? WHERE meta_id = ?";
        final Object[] parameters = new String[]{htmlDocument.getHtml(), "" + htmlDocument.getId()};
        ((Integer)database.execute( new SqlUpdateCommand( sqlStr, parameters ) )).intValue();
    }

    public void visitUrlDocument( UrlDocumentDomainObject urlDocument ) {
        String sqlStr = "UPDATE url_docs SET url_ref = ? WHERE meta_id = ?";
        final Object[] parameters = new String[]{urlDocument.getUrl(), "" + urlDocument.getId()};
        ((Integer)database.execute( new SqlUpdateCommand( sqlStr, parameters ) )).intValue();
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        String sqlStr = "UPDATE text_docs SET template_id = ?, group_id = ?,\n"
                        + "default_template = ?, default_template_1 = ?, default_template_2 = ? WHERE meta_id = ?";
        Integer defaultTemplateId = textDocument.getDefaultTemplateId();
        Integer defaultTemplateIdForRestricted1 = textDocument.getDefaultTemplateIdForRestricted1();
        Integer defaultTemplateIdForRestricted2 = textDocument.getDefaultTemplateIdForRestricted2();

        int templateId = textDocument.getTemplateId();
        int templateGroupId = textDocument.getTemplateGroupId();

        final Object[] parameters = new String[]{
            "" + templateId,
            "" + templateGroupId,
            (null != defaultTemplateId ? "" + defaultTemplateId : null),
            null != defaultTemplateIdForRestricted1 ? "" + defaultTemplateIdForRestricted1 : "-1",
            null != defaultTemplateIdForRestricted2 ? "" + defaultTemplateIdForRestricted2 : "-1",
            "" + textDocument.getId()
        };
        ((Integer)database.execute( new SqlUpdateCommand( sqlStr, parameters ) )).intValue();

        updateTextDocumentTexts( textDocument );
        updateTextDocumentImages( textDocument );
        updateTextDocumentIncludes( textDocument );

        boolean menusChanged = !textDocument.getMenus().equals( ( (TextDocumentDomainObject)oldDocument ).getMenus() );

        if ( menusChanged ) {
            updateTextDocumentMenus( textDocument );
        }
    }
}
