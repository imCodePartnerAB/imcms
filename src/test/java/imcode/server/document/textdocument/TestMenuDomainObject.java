package imcode.server.document.textdocument;

import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.imcms.mapping.MapDocumentGetter;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.GetterDocumentReference;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.util.List;
import java.util.Set;

public class TestMenuDomainObject extends TestCase {

    private MenuDomainObject menu;
    private UserDomainObject user;

    protected void setUp() throws Exception {
        super.setUp();
        user = new UserDomainObject();
        user.addRoleId( RoleId.SUPERADMIN );
        menu = new MenuDomainObject() ;
        DocumentGetter documentGetter = new MapDocumentGetter(new DocumentDomainObject[] {
                new TextDocumentDomainObject(1001),
                new TextDocumentDomainObject(1002),
                new TextDocumentDomainObject(1003),
        }) ;
        menu.addMenuItem( new MenuItemDomainObject(new GetterDocumentReference(1001, documentGetter)) );
        menu.addMenuItem( new MenuItemDomainObject(new GetterDocumentReference(1002, documentGetter)) );
        menu.addMenuItem( new MenuItemDomainObject(new GetterDocumentReference(1003, documentGetter)) );
        menu.addMenuItem( new MenuItemDomainObject(new GetterDocumentReference(1004, documentGetter)) );
    }

    public void testGetMenuItems() {
        MenuItemDomainObject[] menuItems = menu.getMenuItems();
        assertEquals( 3, menuItems.length ) ;
    }

    public void testGetMenuItemsVisibleToUser() {
        List menuItemsVisibleToUser = menu.getMenuItemsVisibleToUser( user );
        assertEquals( 3, menuItemsVisibleToUser.size() ) ;
    }

    public void testGetMenuItemsUnsorted() throws Exception {
        Set menuItems = menu.getMenuItemsUnsorted();
        assertEquals( 3, menuItems.size() ) ;
    }
}
