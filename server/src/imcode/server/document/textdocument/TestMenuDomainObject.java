package imcode.server.document.textdocument;

import imcode.server.Config;
import imcode.server.user.UserDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.db.MockDatabase;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentReference;
import imcode.server.document.DocumentDomainObject;
import junit.framework.TestCase;

import java.util.List;

public class TestMenuDomainObject extends TestCase {

    private MenuDomainObject menu;
    private UserDomainObject user;

    protected void setUp() throws Exception {
        super.setUp();
        user = new UserDomainObject();
        user.addRole( RoleDomainObject.SUPERADMIN );
        this.menu = new MenuDomainObject() ;
        MockDatabase database = new MockDatabase();
        DocumentMapper documentMapper = new DocumentMapper( null, database, null, null, null, null, new Config() ) {
            public DocumentDomainObject getDocument( int metaId ) {
                if (1002 == metaId) {
                    TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
                    return textDocument;
                } else {
                    return super.getDocument( metaId ) ;
                }
            }
        };
        menu.addMenuItem( new MenuItemDomainObject( new DocumentReference(1001, documentMapper) ) );
        menu.addMenuItem( new MenuItemDomainObject( new DocumentReference(1002, documentMapper) ) );
    }

    public void testGetMenuItems() {
        MenuItemDomainObject[] menuItems = menu.getMenuItems();
        assertEquals( 1, menuItems.length ) ;
    }

    public void testGetMenuItemsVisibleToUser() {
        List menuItemsVisibleToUser = menu.getMenuItemsVisibleToUser( user );
        assertEquals( 1, menuItemsVisibleToUser.size() ) ;
    }

}
