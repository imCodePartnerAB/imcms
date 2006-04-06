package com.imcode.imcms.mapping;

import com.imcode.db.mock.MockDatabase;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import junit.framework.TestCase;

public class TestDocumentPermissionSetMapper extends TestCase {

    private DocumentPermissionSetMapper documentPermissionSetMapper ;

    private TextDocumentPermissionSetDomainObject textDocumentPermissionSet;
    private MockDatabase database;
    private TextDocumentDomainObject textDocument;

    public void setUp() throws Exception {
        super.setUp();
        database = new MockDatabase();
        documentPermissionSetMapper = new DocumentPermissionSetMapper( database);
        textDocument = new TextDocumentDomainObject();
        textDocumentPermissionSet = new TextDocumentPermissionSetDomainObject( DocumentPermissionSetTypeDomainObject.RESTRICTED_1 );
    }

    public void testSetTextDocumentPermissionSetFromBits() throws Exception {
        database.addExpectedSqlCall( new MockDatabase.StartsWithSqlCallPredicate( DatabaseDocumentGetter.SQL_SELECT_PERMISSON_DATA__PREFIX ), null );
        textDocumentPermissionSet.setEditDocumentInformation(0 != ( DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & DocumentPermissionSetDomainObject.EDIT_DOCINFO_PERMISSION_ID ));
        textDocumentPermissionSet.setEditPermissions(0 != ( DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & DocumentPermissionSetDomainObject.EDIT_PERMISSIONS_PERMISSION_ID ));
        textDocumentPermissionSet.setEdit(0 != ( DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID ));
        textDocumentPermissionSet.setEditTexts(0 != ( DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEXTS_PERMISSION_ID ));
        textDocumentPermissionSet.setEditImages(0 != ( DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_IMAGES_PERMISSION_ID ));
        textDocumentPermissionSet.setEditMenus(0 != ( DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_MENUS_PERMISSION_ID ));
        textDocumentPermissionSet.setEditIncludes(0
                                                   != ( DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_INCLUDES_PERMISSION_ID ));
        textDocumentPermissionSet.setEditTemplates(0
                                                    != ( DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID ));
        assertTrue( textDocumentPermissionSet.getEditTexts() );
    }

    public void testSaveRestrictedTextDocumentPermissionSet() {
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSet( textDocument, textDocumentPermissionSet, false );
        database.assertCalled( new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate( DocumentPermissionSetMapper.TABLE_DOC_PERMISSION_SETS, "0"));
        textDocumentPermissionSet.setEditTexts( true );
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSet( textDocument, textDocumentPermissionSet, false );
        database.assertCalled( new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate( DocumentPermissionSetMapper.TABLE_DOC_PERMISSION_SETS, ""+DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID));
    }

}