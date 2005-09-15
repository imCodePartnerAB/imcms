package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentGetter;
import imcode.server.document.DocumentId;

public class DatabaseDocumentGetter implements DocumentGetter {
    private Database database;
    private ImcmsServices services;

    public DatabaseDocumentGetter(Database database, ImcmsServices services) {
        this.database = database;
        this.services = services;
    }

    public DocumentDomainObject getDocument(final DocumentId metaId) {
        DatabaseCommand getDocumentDatabaseCommand = new DocumentGetterDatabaseCommand(metaId);
        return (DocumentDomainObject) database.executeCommand(getDocumentDatabaseCommand);
    }


    private class DocumentGetterDatabaseCommand implements DatabaseCommand {
        private final DocumentId documentId;

        DocumentGetterDatabaseCommand(DocumentId documentId) {
            this.documentId = documentId;
        }

        public Object executeOn(DatabaseConnection connection) throws DatabaseException {
            ConnectionDocumentGetter connectionDocumentGetter = new ConnectionDocumentGetter(connection, services);
            return connectionDocumentGetter.getDocument(documentId);
        }
    }
}
