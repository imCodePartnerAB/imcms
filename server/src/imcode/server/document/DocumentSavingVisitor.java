package imcode.server.document;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.ExceptionUnhandlingDatabase;
import imcode.server.db.ExceptionUnhandlingDatabaseConnection;
import imcode.server.db.commands.TransactionDatabaseCommand;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

public class DocumentSavingVisitor extends DocumentStoringVisitor {

    private DocumentDomainObject oldDocument;

    public DocumentSavingVisitor( UserDomainObject user, DocumentDomainObject documentInDatabase, ExceptionUnhandlingDatabase database ) {
        super( user, database );
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

        database.executeUpdateQuery( sqlStr, new String[]{
            "" + textDocument.getTemplate().getId(),
            "" + textDocument.getTemplateGroupId(),
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
            database.executeCommand( new TransactionDatabaseCommand() {
                public Object executeInTransaction( DatabaseConnection connection ) {
                    updateTextDocumentMenus( new ExceptionUnhandlingDatabaseConnection(connection), textDocument );
                    return null ;
                }
            } );
        }
    }
}
