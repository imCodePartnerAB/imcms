package imcode.server.parser;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.user.RoleDomainObject;
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
        assertFalse(menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX )) ;
    }

    public void testUserCantSeePublishedDocumentInMenuWithoutPermissions() {
        textDocument.setPublicationStartDatetime( new Date(0) );
        assertFalse( menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX ) );
    }

    public void testUserCantSeeUnpublishedDocumentInMenuWithPermissions() {
        textDocument.setPermissionSetIdForRole( RoleDomainObject.USERS, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        assertFalse( menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX ) );
    }

    public void testUserCanSeePublishedDocumentInMenuWithPermissions() {
        textDocument.setPermissionSetIdForRole( RoleDomainObject.USERS, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        textDocument.setPublicationStartDatetime( new Date( 0 ) );
        assertFalse( menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX ) );
    }

    public void testAdminCantSeeUnpublishedDocumentWhenInMenuModeButNotEditingMenu() {
        user.addRole( editRole );
        parserParameters.setMenuMode( true );
        assertFalse( menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX ) );
    }

    public void testAdminCantSeeUnpublishedDocumentWhenNotInMenuModeButEditingMenu() {
        user.addRole( editRole );
        parserParameters.setEditingMenuIndex( new Integer( MENU_INDEX ) );
        assertFalse( menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX ) );
    }

    public void testAdminCanSeeUnpublishedDocumentWhenInMenuModeAndEditingMenu() {
        user.addRole( editRole );
        parserParameters.setEditingMenuIndex( new Integer( MENU_INDEX ) );
        parserParameters.setMenuMode(true) ;
        assertTrue( menuParser.userCanSeeDocumentInMenu( user, textDocument, MENU_INDEX ) );
    }
}