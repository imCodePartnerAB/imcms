package imcode.server.document;

import junit.framework.TestCase;

public class TestTextDocumentPermissionSetDomainObject extends TestCase {

    TextDocumentPermissionSetDomainObject textDocumentPermissionSet = new TextDocumentPermissionSetDomainObject( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 );

    public void testEditPermissionIsSeparateFromTextsPermission() {
        textDocumentPermissionSet.setPermission( DocumentPermission.EDIT, true );
        assertTrue( textDocumentPermissionSet.getEdit() ) ;
        assertFalse( textDocumentPermissionSet.getEditTexts() ) ;
        textDocumentPermissionSet.setPermission( DocumentPermission.EDIT, false );
        assertFalse( textDocumentPermissionSet.getEdit() );
        assertFalse( textDocumentPermissionSet.getEditTexts() );
        textDocumentPermissionSet.setPermission( TextDocumentPermission.EDIT_TEXTS, true );
        assertFalse( textDocumentPermissionSet.getEdit() );
        assertTrue( textDocumentPermissionSet.getEditTexts() );
    }


}