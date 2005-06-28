package com.imcode.imcms.mapping;

import junit.framework.TestCase;
import com.imcode.imcms.api.SaveException;
import com.imcode.imcms.api.NoPermissionException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.*;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.db.impl.MockDatabase;
import imcode.util.SystemClock;

public class TestDocumentSaver extends TestCase {
    private DefaultDocumentMapper documentMapper;
    private DocumentSaver documentSaver;

    protected void setUp() throws Exception {
        super.setUp();
        documentMapper = new DefaultDocumentMapper() {
            public void invalidateDocument(DocumentDomainObject document) {
            }
        };
        MockDatabase database = new MockDatabase();
        documentMapper.setDocumentPermissionSetMapper(new DocumentPermissionSetMapper(database, null));
        documentMapper.setDatabase(database);
        documentMapper.setClock(new SystemClock()) ;
        documentMapper.setCategoryMapper(new CategoryMapper(database)) ;
        documentSaver = new DocumentSaver(documentMapper);
    }

    public void testRemoveOwnPermissionsFromDocument() throws SaveException, NoPermissionException {
        final TextDocumentDomainObject oldDocument = createDocument(1001);
        documentMapper.setDocumentGetter(new DocumentGetter() {
            public DocumentDomainObject getDocument(DocumentId documentId) {
                return oldDocument;
            }
        });

        TextDocumentDomainObject document = createDocument(1001);

        try {
            documentSaver.saveDocument(document, oldDocument, new UserDomainObject());
            fail("Expected exception.") ;
        } catch ( NoPermissionToEditDocumentException e) {
        }
        oldDocument.setPermissionSetIdForRole(RoleDomainObject.USERS, DocumentPermissionSetDomainObject.TYPE_ID__FULL);
        documentSaver.saveDocument(document, oldDocument, new UserDomainObject());
    }

    private TextDocumentDomainObject createDocument(int documentId) {
        TextDocumentDomainObject document = new TextDocumentDomainObject();
        documentMapper.makeDocumentLookNew(document, null) ;
        document.setId(documentId);
        document.setTemplate(new TemplateDomainObject(1, "Test", "Test"));
        return document;
    }


}