package imcode.server.document.textdocument;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DatabaseDocumentGetter;
import com.imcode.imcms.mapping.DefaultDocumentMapper;
import imcode.server.Config;
import imcode.server.MockImcmsServices;
import imcode.server.db.impl.MockDatabase;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentId;
import imcode.server.document.DocumentReference;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;

import java.util.List;

public class TestMenuDomainObject extends TestCase {

    private MenuDomainObject menu;
    private UserDomainObject user;

    protected void setUp() throws Exception {
        super.setUp();
        BasicConfigurator.configure(new NullAppender());
        user = new UserDomainObject();
        user.addRoleId( RoleId.SUPERADMIN );
        this.menu = new MenuDomainObject() ;
        MockDatabase database = new MockDatabase();
        final MockImcmsServices services = new MockImcmsServices();
        DefaultDocumentMapper documentMapper = new DefaultDocumentMapper( services, database, new DatabaseDocumentGetter(database, services), null, null, null, new Config(), new CategoryMapper(database)) {
            public DocumentDomainObject getDocument( DocumentId metaId ) {
                if (1002 == metaId.intValue()) {
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
