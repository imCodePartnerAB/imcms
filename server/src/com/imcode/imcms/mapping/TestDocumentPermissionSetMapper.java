package com.imcode.imcms.mapping;

import imcode.server.MockImcmsServices;
import imcode.server.db.impl.MockDatabase;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import junit.framework.TestCase;

public class TestDocumentPermissionSetMapper extends TestCase {

    private DocumentPermissionSetMapper documentPermissionSetMapper ;

    private TextDocumentPermissionSetDomainObject textDocumentPermissionSet;
    private MockDatabase database;
    private TextDocumentDomainObject textDocument;

    public void setUp() throws Exception {
        super.setUp();
        database = new MockDatabase();
        documentPermissionSetMapper = new DocumentPermissionSetMapper( database, new MockImcmsServices() );
        textDocument = new TextDocumentDomainObject();
        textDocumentPermissionSet = new TextDocumentPermissionSetDomainObject( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 );
    }

    public void testSetTextDocumentPermissionSetFromBits() throws Exception {
        database.addExpectedSqlCall( new MockDatabase.EqualsSqlCallPredicate( DocumentPermissionSetMapper.SQL_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS ), new String[0][0] );
        database.addExpectedSqlCall( new MockDatabase.StartsWithSqlCallPredicate( DocumentPermissionSetMapper.SQL_SELECT_PERMISSON_DATA__PREFIX ), new String[0] );
        documentPermissionSetMapper.setTextDocumentPermissionSetFromBits( textDocument, textDocumentPermissionSet, DocumentPermissionSetMapper.EDIT_DOCUMENT_PERMISSION_ID, false );
        assertTrue( textDocumentPermissionSet.getEditTexts() );
    }

    public void testSaveRestrictedTextDocumentPermissionSet() {
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSet( textDocument, textDocumentPermissionSet, false );
        database.assertCalled( new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate( DocumentPermissionSetMapper.TABLE_DOC_PERMISSION_SETS, "0"));
        textDocumentPermissionSet.setEditTexts( true );
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSet( textDocument, textDocumentPermissionSet, false );
        database.assertCalled( new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate( DocumentPermissionSetMapper.TABLE_DOC_PERMISSION_SETS, ""+DocumentPermissionSetMapper.EDIT_DOCUMENT_PERMISSION_ID));
    }

}