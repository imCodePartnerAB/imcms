package imcode.server.document;

import imcode.server.db.Database;
import imcode.server.db.MockDatabase;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import junit.framework.TestCase;

public class TestDocumentPermissionSetMapper extends TestCase {

    private DocumentPermissionSetMapper documentPermissionSetMapper ;

    private TextDocumentPermissionSetDomainObject textDocumentPermissionSet;

    public void setUp() throws Exception {
        super.setUp();
        Database database = new MockDatabase();
        documentPermissionSetMapper = new DocumentPermissionSetMapper( database );
        textDocumentPermissionSet = new TextDocumentPermissionSetDomainObject( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 );
    }

    public void testSetTextDocumentPermissionSetFromBits() throws Exception {
        TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
        documentPermissionSetMapper.setTextDocumentPermissionSetFromBits( textDocument, textDocumentPermissionSet, DocumentPermissionSetMapper.EDIT_DOCUMENT_PERMISSION_ID, false );
        assertTrue( textDocumentPermissionSet.getEdit() );
        assertTrue( textDocumentPermissionSet.getEditTexts() );
    }

}