package imcode.server.document;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;

public class DocumentSavingVisitor extends DocumentStoringVisitor {

    private final static Logger log = Logger.getLogger( DocumentSavingVisitor.class.getName() );

    private DocumentDomainObject oldDocument;

    public DocumentSavingVisitor( UserDomainObject user, DocumentDomainObject documentInDatabase ) {
        super( user );
        this.oldDocument = documentInDatabase;
    }

    public void visitBrowserDocument( BrowserDocumentDomainObject browserDocument ) {
        deleteBrowserDocument( browserDocument );
        saveNewBrowserDocument( browserDocument );
    }

    private void deleteBrowserDocument( BrowserDocumentDomainObject browserDocument ) {
        String sqlStr = "DELETE FROM browser_docs WHERE meta_id = ?";
        service.sqlUpdateQuery( sqlStr, new String[]{"" + browserDocument.getId()} );
    }

    public void saveNewBrowserDocument( BrowserDocumentDomainObject document ) {
        String[] browserDocumentColumns = {"meta_id", "to_meta_id", "browser_id"};

        String sqlBrowserDocsInsertStr = makeSqlInsertString( "browser_docs", browserDocumentColumns );

        Map browserDocumentMap = document.getBrowserDocumentIdMap();
        for ( Iterator iterator = browserDocumentMap.keySet().iterator(); iterator.hasNext(); ) {
            BrowserDocumentDomainObject.Browser browser = (BrowserDocumentDomainObject.Browser)iterator.next();
            Integer metaIdForBrowser = (Integer)browserDocumentMap.get( browser );
            service.sqlUpdateQuery( sqlBrowserDocsInsertStr, new String[]{
                "" + document.getId(), "" + metaIdForBrowser, "" + browser.getId()
            } );
        }
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
        String sqlStr = "UPDATE frameset_docs SET frame_set = ? WHERE meta_id = ?";
        service.sqlUpdateQuery( sqlStr, new String[]{htmlDocument.getHtml(), "" + htmlDocument.getId()} );
    }

    public void visitUrlDocument( UrlDocumentDomainObject urlDocument ) {
        String sqlStr = "UPDATE url_docs SET url_ref = ? WHERE meta_id = ?";
        service.sqlUpdateQuery( sqlStr, new String[]{urlDocument.getUrl(), "" + urlDocument.getId()} );
    }

    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        String sqlStr = "UPDATE text_docs SET template_id = ?, group_id = ?,\n"
                        + "default_template = ?, default_template_1 = ?, default_template_2 = ? WHERE meta_id = ?";
        TemplateDomainObject defaultTemplate = textDocument.getDefaultTemplate();
        TemplateDomainObject defaultTemplateForRestricted1 = ( (TextDocumentPermissionSetDomainObject)textDocument.getPermissionSetForRestrictedOneForNewDocuments() ).getDefaultTemplate();
        TemplateDomainObject defaultTemplateForRestricted2 = ( (TextDocumentPermissionSetDomainObject)textDocument.getPermissionSetForRestrictedTwoForNewDocuments() ).getDefaultTemplate();

        service.sqlUpdateQuery( sqlStr, new String[]{
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
            service.getDatabase().executeTransaction( new DatabaseCommand() {
                public void executeOn( DatabaseConnection connection ) {
                    updateTextDocumentMenus( connection, textDocument );
                }
            } );
        }
    }
}
