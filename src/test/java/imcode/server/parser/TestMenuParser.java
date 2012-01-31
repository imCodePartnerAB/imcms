package imcode.server.parser;

import com.imcode.imcms.api.Document;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.util.Date;

public class TestMenuParser extends TestCase {

    private MenuParser menuParser;
    private MockParserParameters parserParameters;
    private UserDomainObject user ;
    private TextDocumentDomainObject textDocument ;
    private RoleId editRole;
    private static final int MENU_INDEX = 1;

    protected void setUp() throws Exception {
        super.setUp();
        user = new UserDomainObject();
        textDocument = new TextDocumentDomainObject();
        editRole = new RoleId(3);
        textDocument.setDocumentPermissionSetTypeForRoleId(editRole, DocumentPermissionSetTypeDomainObject.FULL);
        parserParameters = new MockParserParameters(null);
        menuParser = new MenuParser(parserParameters);
    }

    public void testUserCantSeeUnpublishedDocumentInMenuWithoutPermissions() throws Exception {
        assertCanNotSeeDocumentInMenu();
    }

    public void testUserCantSeePublishedDocumentInMenuWithoutPermissions() {
        textDocument.setPublicationStartDatetime( new Date(0) );
        textDocument.setPublicationStatus( Document.PublicationStatus.APPROVED );
        assertCanNotSeeDocumentInMenu();
    }

    public void testUserCantSeeUnpublishedDocumentInMenuWithPermissions() {
        textDocument.setDocumentPermissionSetTypeForRoleId( RoleId.USERS, DocumentPermissionSetTypeDomainObject.READ );
        assertCanNotSeeDocumentInMenu();
    }

    public void testUserCanSeePublishedDocumentInMenuWithPermissions() {
        textDocument.setDocumentPermissionSetTypeForRoleId( RoleId.USERS, DocumentPermissionSetTypeDomainObject.READ );
        textDocument.setPublicationStartDatetime( new Date( 0 ) );
        assertCanNotSeeDocumentInMenu();
        textDocument.setPublicationStatus( Document.PublicationStatus.APPROVED );
        assertCanSeeDocumentInMenuWhenEditingMenu();
    }

    public void testAdminCantSeeUnpublishedDocumentWhenInMenuModeButNotEditingMenu() {
        user.addRoleId( editRole );
        parserParameters.setMenuMode( true );
        assertCanNotSeeDocumentInMenu();
    }

    public void testAdminCantSeeUnpublishedDocumentWhenNotInMenuModeButEditingMenu() {
        user.addRoleId( editRole );
        parserParameters.setEditingMenuIndex( new Integer( MENU_INDEX ) );
        assertCanNotSeeDocumentInMenu();
    }

    public void testAdminCanSeeUnpublishedDocumentWhenEditing() {
        user.addRoleId( editRole );
        parserParameters.setEditingMenuIndex( new Integer( MENU_INDEX ) );
        parserParameters.setMenuMode(true) ;
        assertCanSeeDocumentInMenuWhenEditingMenu();
    }

    public void testUserCantSeeUnpublishedDocumentInMenuWhenEditing() throws Exception {
        parserParameters.setEditingMenuIndex( new Integer( MENU_INDEX ) );
        parserParameters.setMenuMode( true );
        assertCanNotSeeDocumentInMenu();
    }

    public void testUserCanSeePublishedDocumentsVisibleForUnauthorizedUsers() {
        textDocument.setLinkedForUnauthorizedUsers( true );
        assertCanNotSeeDocumentInMenu();
        textDocument.setPublicationStartDatetime( new Date( 0 ) );
        textDocument.setPublicationStatus( Document.PublicationStatus.APPROVED );
        assertCanSeeDocumentInMenuWhenEditingMenu();
    }

    private void assertCanNotSeeDocumentInMenu() {
        assertFalse( user.canSeeDocumentInMenus(textDocument) );
    }

    private void assertCanSeeDocumentInMenuWhenEditingMenu() {
        assertTrue( user.canSeeDocumentWhenEditingMenus(textDocument) );
    }
}