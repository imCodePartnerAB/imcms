package com.imcode.imcms.api;

import imcode.server.MockImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.util.Date;

public class TestTextDocument extends TestCase {

    TextDocument.Menu menu ;
    private UserDomainObject internalUser;
    private TextDocumentDomainObject textDocumentDO;
    private TextDocumentDomainObject otherTextDocumentDO;
    private RoleId readRole;
    private RoleId editRole;
    private TextDocument textDocument;
    private MockContentManagementSystem contentManagementSystem;
    private TextDocument otherTextDocument;
    private MockImcmsServices imcmsServices;

    protected void setUp() throws Exception {
        super.setUp();
        internalUser = new UserDomainObject();
        readRole = new RoleId( 3);
        editRole = new RoleId( 4);
        textDocumentDO = new TextDocumentDomainObject();
        textDocumentDO.setId( 1001 );
        textDocumentDO.setDocumentPermissionSetTypeForRoleId( readRole, DocumentPermissionSetTypeDomainObject.READ );
        textDocumentDO.setDocumentPermissionSetTypeForRoleId( editRole, DocumentPermissionSetTypeDomainObject.FULL );
        otherTextDocumentDO = new TextDocumentDomainObject();
        otherTextDocumentDO.setId( 1002 );
        otherTextDocumentDO.setLinkableByOtherUsers( true );
        int menuIndex = 1;
        DocumentReference documentReference = new DirectDocumentReference( otherTextDocumentDO );
        MenuDomainObject menuDO = textDocumentDO.getMenu( menuIndex );
        menuDO.addMenuItem( new MenuItemDomainObject(documentReference) );
        contentManagementSystem = new MockContentManagementSystem();

        imcmsServices = new MockImcmsServices();
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
        internalUser.addRoleId( editRole );
        otherTextDocumentDO.setDocumentPermissionSetTypeForRoleId( editRole, DocumentPermissionSetTypeDomainObject.FULL );
        assertGettersReturnDocuments();
    }

    public void testMenuGetVisible() {
        internalUser.addRoleId( readRole );
        otherTextDocumentDO.setDocumentPermissionSetTypeForRoleId( readRole, DocumentPermissionSetTypeDomainObject.READ );
        publish(otherTextDocumentDO);
        assertGetVisibleReturnDocuments();
    }

    public void testMenuGetVisibleWithArchived() {
        internalUser.addRoleId( readRole );
        otherTextDocumentDO.setDocumentPermissionSetTypeForRoleId( readRole, DocumentPermissionSetTypeDomainObject.READ );
        publish(otherTextDocumentDO);
        otherTextDocumentDO.setArchivedDatetime( new Date( 0 ) );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithNothing() {
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithRole() {
        internalUser.addRoleId( readRole );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithRoleAndPermission() {
        internalUser.addRoleId( readRole );
        otherTextDocumentDO.setDocumentPermissionSetTypeForRoleId( readRole, DocumentPermissionSetTypeDomainObject.READ );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithApprovedStatus() {
        otherTextDocumentDO.setPublicationStatus(Document.PublicationStatus.APPROVED );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublicationStart() {
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublished() {
        publish(otherTextDocumentDO);
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithVisibleInMenusForUnauthorizedUsers() {
        otherTextDocumentDO.setLinkedForUnauthorizedUsers( true );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublishedAndVisibleInMenusForUnauthorizedUsers() {
        publish(otherTextDocumentDO);
        otherTextDocumentDO.setLinkedForUnauthorizedUsers( true );
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

    public void testAddRemoveDocument() throws DocumentAlreadyInMenuException {
        menu.addDocument( otherTextDocument );
        assertEquals(0, menu.getDocuments().length) ;
        otherTextDocumentDO.setDocumentPermissionSetTypeForRoleId(readRole, DocumentPermissionSetTypeDomainObject.READ);
        assertEquals(0, menu.getDocuments().length) ;
        internalUser.addRoleId(readRole);
        assertEquals(0, menu.getDocuments().length) ;
        publish(otherTextDocument) ;
        assertEquals(1, menu.getDocuments().length) ;
        assertEquals(otherTextDocument, menu.getDocuments()[0]) ;
        menu.removeDocument( otherTextDocument );
        assertEquals(0, menu.getDocuments().length) ;
    }

    private void publish(Document document) {
        publish(document.getInternal()) ;
    }

    private void publish(DocumentDomainObject document) {
        document.setPublicationStatus( Document.PublicationStatus.APPROVED );
        document.setPublicationStartDatetime( new Date( 0 ) );
    }

}