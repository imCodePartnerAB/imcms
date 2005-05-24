package imcode.server.document;

import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

class DatabaseDocumentGetter implements DocumentGetter {
    private Database database;
    private ImcmsServices services;

    Logger log = Logger.getLogger(DatabaseDocumentGetter.class) ;

    public DatabaseDocumentGetter(Database database, ImcmsServices services) {
        this.database = database;
        this.services = services;
    }

    public DocumentDomainObject getDocument(final DocumentId metaId) {
        DatabaseCommand getDocumentDatabaseCommand = new DocumentGetterDatabaseCommand(metaId);
        DocumentDomainObject document = (DocumentDomainObject) database.executeCommand(getDocumentDatabaseCommand) ;
        return document;
    }


    private class DocumentGetterDatabaseCommand implements DatabaseCommand {
        private final DocumentId documentId;

        public DocumentGetterDatabaseCommand(DocumentId documentId) {
            this.documentId = documentId;
        }

        public Object executeOn(DatabaseConnection connection) throws DatabaseException {
            ConnectionDocumentGetter connectionDocumentGetter = new ConnectionDocumentGetter(connection, services);
            return connectionDocumentGetter.getDocument(documentId);
        }
    }
}
