package com.imcode.imcms.api;

import junit.framework.TestCase;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.user.RoleDomainObject;

import java.util.Date;

public class TestTextDocument extends TestCase {

    TextDocument.Menu menu ;
    private UserDomainObject internalUser;
    private TextDocumentDomainObject textDocumentDomainObject;
    private RoleDomainObject readRole;

    protected void setUp() throws Exception {
        super.setUp();
        textDocumentDomainObject = new TextDocumentDomainObject();
        textDocumentDomainObject.setId( 1001 );
        readRole = new RoleDomainObject( "Test" );
        textDocumentDomainObject.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        int menuIndex = 1;
        DocumentReference documentReference = new MockDocumentReference( textDocumentDomainObject );
        MenuDomainObject menu = textDocumentDomainObject.getMenu( menuIndex );
        menu.addMenuItem( new MenuItemDomainObject( documentReference ) );
        MockContentManagementSystem contentManagementSystem = new MockContentManagementSystem();
        internalUser = new UserDomainObject();
        contentManagementSystem.setCurrentUser( new User( internalUser ) );
        TextDocument textDocument = new TextDocument( textDocumentDomainObject, contentManagementSystem );
        this.menu = new TextDocument.Menu( textDocument, menuIndex );
    }

    public void testMenuGetDocumentsIsSecure() throws Exception {
        assertFalse(menu.getDocuments().length > 0);
        internalUser.addRole( readRole );
        assertTrue( menu.getDocuments().length > 0 );
    }

    public void testMenuGetDocumentsIncludesArchived() throws Exception {
        internalUser.addRole( readRole );
        textDocumentDomainObject.setArchivedDatetime( new Date( 0 ) );
        assertFalse(internalUser.canEdit( textDocumentDomainObject )) ;
        assertTrue( internalUser.canAccess( textDocumentDomainObject ) );
        assertTrue( menu.getDocuments().length > 0 );
    }

    public void testMenuGetDocumentsIncludesVisibleForUnauthorizedUsers() throws Exception {
        assertFalse( menu.getDocuments().length > 0 );
        textDocumentDomainObject.setVisibleInMenusForUnauthorizedUsers( true );
        assertTrue( menu.getDocuments().length > 0 );
    }

    public void testMenuGetMenuItemsIsSecure() throws Exception {
        assertFalse( menu.getMenuItems().length > 0 );
        internalUser.addRole( readRole );
        assertTrue( menu.getMenuItems().length > 0 );
    }

    private static class MockDocumentReference extends DocumentReference {

        private final DocumentDomainObject document;

        public MockDocumentReference( DocumentDomainObject document ) {
            super( document.getId(), null );
            this.document = document;
        }

        public DocumentDomainObject getDocument() {
            return document ;
        }
    }
}