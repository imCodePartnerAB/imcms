package com.imcode.imcms.api;

import imcode.server.Config;
import imcode.server.MockImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.util.Date;

public class TestTextDocument extends TestCase {

    TextDocument.Menu menu ;
    private UserDomainObject internalUser;
    private TextDocumentDomainObject textDocumentDO;
    private TextDocumentDomainObject otherTextDocumentDO;
    private RoleDomainObject readRole;
    private RoleDomainObject editRole;
    private TextDocument textDocument;
    private MockContentManagementSystem contentManagementSystem;
    private TextDocument otherTextDocument;

    protected void setUp() throws Exception {
        super.setUp();
        internalUser = new UserDomainObject();
        readRole = new RoleDomainObject( 3, "Read", 0 );
        editRole = new RoleDomainObject( 4, "Edit", 0 );
        textDocumentDO = new TextDocumentDomainObject();
        textDocumentDO.setId( 1001 );
        textDocumentDO.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        textDocumentDO.setPermissionSetIdForRole( editRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        otherTextDocumentDO = new TextDocumentDomainObject();
        otherTextDocumentDO.setId( 1002 );
        otherTextDocumentDO.setLinkableByOtherUsers( true );
        int menuIndex = 1;
        DocumentReference documentReference = new MockDocumentReference( otherTextDocumentDO );
        MenuDomainObject menuDO = textDocumentDO.getMenu( menuIndex );
        menuDO.addMenuItem( new MenuItemDomainObject( documentReference ) );
        contentManagementSystem = new MockContentManagementSystem();
        MockImcmsServices imcmsServices = new MockImcmsServices();
        imcmsServices.setDocumentMapper( new DocumentMapper(null,null,null,null,null,null,new Config() ));
        contentManagementSystem.setInternal( imcmsServices );
        contentManagementSystem.setCurrentUser( new User( internalUser ) );
        textDocument = new TextDocument( this.textDocumentDO, contentManagementSystem );
        otherTextDocument = new TextDocument( otherTextDocumentDO, contentManagementSystem );
        this.menu = new TextDocument.Menu( textDocument, menuIndex );
    }

    public void testMenuGetDocumentsAndMenuItemsWithNothing() {
        assertGettersDoNotReturnDocuments();
    }

    public void testMenuGetDocumentsAndMenuItems() {
        internalUser.addRole( editRole );
        otherTextDocumentDO.setPermissionSetIdForRole( editRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        assertGettersReturnDocuments();
    }

    public void testMenuGetVisible() throws Exception {
        internalUser.addRole( readRole );
        otherTextDocumentDO.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        assertGetVisibleReturnDocuments();
    }

    public void testMenuGetVisibleWithArchived() throws Exception {
        internalUser.addRole( readRole );
        otherTextDocumentDO.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        otherTextDocumentDO.setArchivedDatetime( new Date( 0 ) );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithNothing() throws Exception {
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithRole() throws Exception {
        internalUser.addRole( readRole );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithRoleAndPermission() throws Exception {
        internalUser.addRole( readRole );
        otherTextDocumentDO.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithApprovedStatus() throws Exception {
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublicationStart() throws Exception {
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublished() throws Exception {
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithVisibleInMenusForUnauthorizedUsers() throws Exception {
        otherTextDocumentDO.setVisibleInMenusForUnauthorizedUsers( true );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublishedAndVisibleInMenusForUnauthorizedUsers() throws Exception {
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        otherTextDocumentDO.setVisibleInMenusForUnauthorizedUsers( true );
        assertGetVisibleReturnDocuments();
    }

    private void assertGettersDoNotReturnDocuments() {
        assertFalse( menu.getDocuments().length > 0 );
        assertFalse( menu.getMenuItems().length > 0 );
    }

    private void assertGettersReturnDocuments() {
        assertTrue( menu.getDocuments().length > 0 );
        assertTrue( menu.getMenuItems().length > 0 );
    }

    private void assertGetVisibleReturnDocuments() {
        assertTrue( menu.getVisibleDocuments().length > 0 );
        assertTrue( menu.getVisibleMenuItems().length > 0 );
    }

    private void assertGetVisibleDoNotReturnDocuments() {
        assertFalse( menu.getVisibleDocuments().length > 0 );
        assertFalse( menu.getVisibleMenuItems().length > 0 );
    }

    public void testAddDocument() throws DocumentAlreadyInMenuException, NoPermissionException {
        try {
            menu.addDocument( otherTextDocument );
            fail();
        } catch( NoPermissionException npe ) {}
        internalUser.addRole( readRole );
        otherTextDocumentDO.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        try {
            menu.addDocument( otherTextDocument );
            fail() ;
        } catch( NoPermissionException npe ) {}
        internalUser.addRole( editRole );
        menu.addDocument( otherTextDocument );
    }

    public void testRemoveDocument() throws NoPermissionException {
        try {
            menu.removeDocument( otherTextDocument );
            fail() ;
        } catch( NoPermissionException npe ) {}
        internalUser.addRole( editRole );
        menu.removeDocument( otherTextDocument );
    }

    private static class MockDocumentReference extends DocumentReference {

        private final DocumentDomainObject document;

        MockDocumentReference( DocumentDomainObject document ) {
            super( document.getId(), null );
            this.document = document;
        }

        public DocumentDomainObject getDocument() {
            return document ;
        }
    }
}