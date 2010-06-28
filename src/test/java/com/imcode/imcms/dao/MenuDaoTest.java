package com.imcode.imcms.dao;

import static org.junit.Assert.*;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.MenuHistory;
import com.imcode.imcms.api.TextHistory;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.mapping.orm.Include;
import com.imcode.imcms.mapping.orm.TemplateNames;
import com.imcode.imcms.util.Factory;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

//todo: Test named queries
public class MenuDaoTest {
	
	MenuDao menuDao;

    static UserDomainObject ADMIN = new UserDomainObject(0);


	@BeforeClass
    public static void init() {
        Script.recreateDB();
	}


    @Before
    public void resetDBData() {

        SessionFactory sf = Script.createHibernateSessionFactory(
                new Class[] {MenuDomainObject.class, MenuHistory.class},
                "src/main/resources/Menu.hbm.xml");

        menuDao = new MenuDao();
        menuDao.setSessionFactory(sf);

        Script.runDBScripts("menu_dao.sql");
    }
    
    
    @Test public void getMenus() {
        List<MenuDomainObject> menus = menuDao.getMenus(1001, 0);
        
        assertEquals(menus.size(), 3);
    }    
    
    
    @Test public void insertMenu() {
        List<MenuDomainObject> menus = menuDao.getMenus(1001, 0);
        assertEquals(menus.size(), 3);  

        MenuDomainObject menu = new MenuDomainObject();
        menu.setDocId(1001);
        menu.setDocVersionNo(0);
        menu.setNo(4);
        menu.setSortOrder(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE);
              
        MenuItemDomainObject mi = new MenuItemDomainObject();
        mi.setSortKey(2);
        mi.setTreeSortKey(new TreeSortKeyDomainObject("3"));
                
        menu.getItemsMap().put(1001, mi);

        menuDao.saveMenu(menu);
                
        menus = menuDao.getMenus(1001, 0);
                
        assertEquals(menus.size(), 4);

        MenuHistory menuHistory = new MenuHistory(menu, ADMIN);
        menuDao.saveMenuHistory(menuHistory);
    }


    @Test public void deleteMenu() {
        List<MenuDomainObject> menus = menuDao.getMenus(1001, 0);
        assertEquals(menus.size(), 3);

        menuDao.deleteMenu(menus.get(0));

        menus = menuDao.getMenus(1001, 0);
        assertEquals(menus.size(), 2);
    }

    
    @Test public void deleteMenus() {
        List<MenuDomainObject> menus = menuDao.getMenus(1001, 0);
        assertEquals(menus.size(), 3);

        menuDao.deleteMenus(1001, 0);

        menus = menuDao.getMenus(1001, 0);
        assertEquals(menus.size(), 0);        
    }
}