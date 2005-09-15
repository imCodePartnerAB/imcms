package com.imcode.imcms.mapping;

import com.imcode.db.mock.MockDatabase;
import com.imcode.imcms.api.NoPermissionException;
import com.imcode.imcms.api.SaveException;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.SystemClock;
import junit.framework.TestCase;

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
        oldDocument.setDocumentPermissionSetTypeForRoleId(RoleId.USERS, DocumentPermissionSetTypeDomainObject.FULL);
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