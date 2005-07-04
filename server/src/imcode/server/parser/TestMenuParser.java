package imcode.server.parser;

import com.imcode.imcms.api.Document;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.util.Date;

public class TestMenuParser extends TestCase {

    private MenuParser menuParser;
    private MockParserParameters parserParameters;
    private UserDomainObject user ;
    private TextDocumentDomainObject textDocument ;
    private RoleDomainObject editRole;
    private static final int MENU_INDEX = 1;

    protected void setUp() throws Exception {
        user = new UserDomainObject();
        textDocument = new TextDocumentDomainObject();
        editRole = new RoleDomainObject( "Editrole" );
        textDocument.setPermissionSetIdForRole( editRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        parserParameters = new MockParserParameters( null );
        menuParser = new MenuParser( parserParameters );
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
        textDocument.setPermissionSetIdForRole( RoleDomainObject.USERS, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        assertCanNotSeeDocumentInMenu();
    }

    public void testUserCanSeePublishedDocumentInMenuWithPermissions() {
        textDocument.setPermissionSetIdForRole( RoleDomainObject.USERS, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        textDocument.setPublicationStartDatetime( new Date( 0 ) );
        assertCanNotSeeDocumentInMenu();
        textDocument.setPublicationStatus( Document.PublicationStatus.APPROVED );
        assertCanSeeDocumentInMenu();
    }

    public void testAdminCantSeeUnpublishedDocumentWhenInMenuModeButNotEditingMenu() {
        user.addRole( editRole );
        parserParameters.setMenuMode( true );
        assertCanNotSeeDocumentInMenu();
    }

    public void testAdminCantSeeUnpublishedDocumentWhenNotInMenuModeButEditingMenu() {
        user.addRole( editRole );
        parserParameters.setEditingMenuIndex( new Integer( MENU_INDEX ) );
        assertCanNotSeeDocumentInMenu();
    }

    public void testAdminCanSeeUnpublishedDocumentWhenEditing() {
        user.addRole( editRole );
        parserParameters.setEditingMenuIndex( new Integer( MENU_INDEX ) );
        parserParameters.setMenuMode(true) ;
        assertCanSeeDocumentInMenu();
    }

    public void testUserCantSeeUnpublishedDocumentInMenuWhenEditing() throws Exception {
        parserParameters.setEditingMenuIndex( new Integer( MENU_INDEX ) );
        parserParameters.setMenuMode( true );
        assertCanNotSeeDocumentInMenu();
    }

    public void testUserCanSeePublishedDocumentsVisibleForUnauthorizedUsers() {
        textDocument.setVisibleInMenusForUnauthorizedUsers( true );
        assertCanNotSeeDocumentInMenu();
        textDocument.setPublicationStartDatetime( new Date( 0 ) );
        textDocument.setPublicationStatus( Document.PublicationStatus.APPROVED );
        assertCanSeeDocumentInMenu();
    }

    private void assertCanNotSeeDocumentInMenu() {
        assertFalse( menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX ) );
    }

    private void assertCanSeeDocumentInMenu() {
        assertTrue( menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX ) );
    }
}