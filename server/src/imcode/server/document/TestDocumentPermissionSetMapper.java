package imcode.server.document;

import imcode.server.db.MockDatabase;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import junit.framework.TestCase;

public class TestDocumentPermissionSetMapper extends TestCase {

    private DocumentPermissionSetMapper documentPermissionSetMapper ;

    private TextDocumentPermissionSetDomainObject textDocumentPermissionSet;
    private MockDatabase database;

    public void setUp() throws Exception {
        super.setUp();
        database = new MockDatabase();
        documentPermissionSetMapper = new DocumentPermissionSetMapper( database );
        textDocumentPermissionSet = new TextDocumentPermissionSetDomainObject( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 );
    }

    public void testSetTextDocumentPermissionSetFromBits() throws Exception {
        TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
        database.addExpectedSqlCall( new MockDatabase.EqualsSqlCallPredicate( DocumentPermissionSetMapper.SPROC_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS ), new String[0][0] );
        database.addExpectedSqlCall( new MockDatabase.StartsWithSqlCallPredicate( DocumentPermissionSetMapper.SQL_SELECT_PERMISSON_DATA__PREFIX ), new String[0] );
        documentPermissionSetMapper.setTextDocumentPermissionSetFromBits( textDocument, textDocumentPermissionSet, DocumentPermissionSetMapper.EDIT_DOCUMENT_PERMISSION_ID, false );
        assertTrue( textDocumentPermissionSet.getEdit() );
        assertTrue( textDocumentPermissionSet.getEditTexts() );
    }

}