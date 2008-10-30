package com.imcode.imcms.dao

import org.dbunit.dataset.xml.FlatXmlDataSetimport org.testng.annotations.BeforeClassimport org.testng.annotations.Testimport org.testng.annotations.BeforeTest
import static org.testng.Assert.*import imcode.server.document.textdocument.TextDomainObjectimport org.testng.Assertimport com.imcode.imcms.api.Includeimport imcode.server.document.textdocument.MenuDomainObjectimport imcode.server.document.textdocument.MenuItemDomainObject

//todo: Test named queries
public class MenuDaoTest extends DaoTest {
	
	MenuDao dao;
	
	@BeforeClass void init() {
		dao = Context.getBean("menuDao")
	}		
		
	@Override
	def getDataSetFileName() {
		"dbunit-menus-data.xml"
	}
	

    @Test void getNonExistingMenu() {
        def menu = dao.getMenu(10001, 1)
        
        assertNull(menu)
    }
    

    @Test void getMenu() {
        def menu = dao.getMenu(1001, 1)
        
        assertNotNull(menu)
    } 
    
    @Test void getMenus() {
        def menus = dao.getMenus(1001)
        
        assertTrue(menus.size() == 1)
    }    
    
    
    @Test void insertDeleteMenu() {
        def menu = dao.getMenu(1001, 2)
        
        assertNull(menu)
        
        menu = new MenuDomainObject()
        menu.setMetaId(1001)
        menu.setIndex(2)
        menu.setSortOrder(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE)
              
        def mi = new MenuItemDomainObject()
        mi.sortKey = 2
        mi.treeSortIndex = 3 
                
        menu.getMenuItemz().put(1001, mi)
        
        dao.saveMenu(menu);
        
        menu = dao.getMenu(1001, 2)
        assertNotNull(menu)
        
        /*
        menu.menuItemz.clear();
        dao.saveMenu(menu);
        */
        dao.deleteMenu(menu);
        menu = dao.getMenu(1001, 2)
        assertNull(menu)        
    }
    
    @Test void updateMenu() {
        def menu = dao.getMenu(1001, 1)
        
        assertNotNull(menu)
        menu.setSortOrder(2)
        
        dao.saveMenu(menu);
        
        menu = dao.getMenu(1001, 1)
        assertNotNull(menu)
        assertEquals(menu.sortOrder, 2)
    }   
    
    
}