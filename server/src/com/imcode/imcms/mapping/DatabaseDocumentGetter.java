package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
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
