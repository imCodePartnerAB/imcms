package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlUpdateCommand;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;

public class DocumentSavingVisitor extends DocumentStoringVisitor {

    private DocumentDomainObject oldDocument;
    private UserDomainObject savingUser;

    public DocumentSavingVisitor(DocumentDomainObject documentInDatabase, Database database,
                                 ImcmsServices services, UserDomainObject user) {
        super(database, services );
        oldDocument = documentInDatabase;
        savingUser = user;
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
        String sqlStr = "UPDATE frameset_docs SET frame_set = ? WHERE meta_id = ?";
        final Object[] parameters = new String[]{htmlDocument.getHtml(), "" + htmlDocument.getId()};
        database.execute(new SqlUpdateCommand(sqlStr, parameters));
    }

    public void visitUrlDocument( UrlDocumentDomainObject urlDocument ) {
        String sqlStr = "UPDATE url_docs SET url_ref = ? WHERE meta_id = ?";
        final Object[] parameters = new String[]{urlDocument.getUrl(), "" + urlDocument.getId()};
        database.execute(new SqlUpdateCommand(sqlStr, parameters));
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        String sqlStr = "UPDATE text_docs SET template_name = ?, group_id = ?,\n"
                        + "default_template = ?, default_template_1 = ?, default_template_2 = ? WHERE meta_id = ?";
        String defaultTemplateId = textDocument.getDefaultTemplateName();
        String defaultTemplateIdForRestricted1 = textDocument.getDefaultTemplateNameForRestricted1();
        String defaultTemplateIdForRestricted2 = textDocument.getDefaultTemplateNameForRestricted2();

        String templateId = textDocument.getTemplateName();
        int templateGroupId = textDocument.getTemplateGroupId();

        final Object[] parameters = new String[]{
            "" + templateId,
            "" + templateGroupId,
            (null != defaultTemplateId ? "" + defaultTemplateId : null),
            null != defaultTemplateIdForRestricted1 ? "" + defaultTemplateIdForRestricted1 : null,
            null != defaultTemplateIdForRestricted2 ? "" + defaultTemplateIdForRestricted2 : null,
            "" + textDocument.getId()
        };
        database.execute(new SqlUpdateCommand(sqlStr, parameters));

        updateTextDocumentTexts( textDocument, (TextDocumentDomainObject)oldDocument, savingUser);
        updateTextDocumentImages( textDocument, (TextDocumentDomainObject)oldDocument, savingUser);
        updateTextDocumentIncludes( textDocument );

        boolean menusChanged = !textDocument.getMenus().equals( ( (TextDocumentDomainObject)oldDocument ).getMenus() );

        if ( menusChanged ) {
            updateTextDocumentMenus( textDocument, (TextDocumentDomainObject) oldDocument, savingUser);
        }
    }
}
