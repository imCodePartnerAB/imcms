package com.imcode.imcms.api;

import imcode.server.Config;
import imcode.server.MockImcmsServices;
import imcode.server.document.*;
import imcode.server.document.DocumentGetter;
import imcode.server.document.DocumentReference;
import com.imcode.imcms.mapping.DefaultDocumentMapper;
import com.imcode.imcms.mapping.CategoryMapper;
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
    private MockImcmsServices imcmsServices;

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
        internalUser.addRole( editRole );
        otherTextDocumentDO.setPermissionSetIdForRole( editRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        assertGettersReturnDocuments();
    }

    public void testMenuGetVisible() {
        internalUser.addRole( readRole );
        otherTextDocumentDO.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        assertGetVisibleReturnDocuments();
    }

    public void testMenuGetVisibleWithArchived() {
        internalUser.addRole( readRole );
        otherTextDocumentDO.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        otherTextDocumentDO.setArchivedDatetime( new Date( 0 ) );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithNothing() {
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithRole() {
        internalUser.addRole( readRole );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithRoleAndPermission() {
        internalUser.addRole( readRole );
        otherTextDocumentDO.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithApprovedStatus() {
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublicationStart() {
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublished() {
        otherTextDocumentDO.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        otherTextDocumentDO.setPublicationStartDatetime( new Date( 0 ) );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithVisibleInMenusForUnauthorizedUsers() {
        otherTextDocumentDO.setVisibleInMenusForUnauthorizedUsers( true );
        assertGetVisibleDoNotReturnDocuments();
    }

    public void testMenuGetVisibleWithPublishedAndVisibleInMenusForUnauthorizedUsers() {
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

    public void testAddRemoveDocument() throws DocumentAlreadyInMenuException {
        DocumentGetter documentGetter = new DocumentGetter() {
            public DocumentDomainObject getDocument(DocumentId documentId) {
                if (documentId.intValue() == otherTextDocument.getId() ) {
                    return otherTextDocument.getInternal() ;
                }
                return null ;
            }
        };
        DefaultDocumentMapper documentMapper = new DefaultDocumentMapper(null,null,documentGetter,null,null,null,new Config(), new CategoryMapper(null));
        imcmsServices.setDocumentMapper( documentMapper );
        menu.addDocument( otherTextDocument );
        assertEquals(0, menu.getDocuments().length) ;
        otherTextDocumentDO.setPermissionSetIdForRole(readRole, DocumentPermissionSet.READ);
        assertEquals(0, menu.getDocuments().length) ;
        internalUser.addRole(readRole);
        assertEquals(0, menu.getDocuments().length) ;
        publish(otherTextDocument) ;
        assertEquals(1, menu.getDocuments().length) ;
        assertEquals(otherTextDocument, menu.getDocuments()[0]) ;
        menu.removeDocument( otherTextDocument );
        assertEquals(0, menu.getDocuments().length) ;
    }

    private void publish(Document document) {
        document.setStatus(DocumentDomainObject.STATUS_PUBLICATION_APPROVED);
        document.setPublicationStartDatetime(new Date(0));
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