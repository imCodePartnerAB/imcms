package com.imcode.imcms.dao;

import com.imcode.imcms.api.MenuHistory;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TreeSortKeyDomainObject;

import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

public class MenuDao extends HibernateTemplate {

	@Transactional
	public List<MenuDomainObject> getMenus(Integer docId, Integer docVersionNo) {
		return (List<MenuDomainObject>)findByNamedQueryAndNamedParam("Menu.getMenus",
                new String[] {"docId", "docVersionNo"},
                new Object[] {docId, docVersionNo});
	}


    @Transactional
	public void saveMenu(MenuDomainObject menu) {
        for (Map.Entry<Integer, MenuItemDomainObject> itemEntry: menu.getItemsMap().entrySet()) {
            MenuItemDomainObject item = itemEntry.getValue();
            item.setTreeSortIndex(item.getTreeSortKey().toString());
        }
        
	    saveOrUpdate(menu);
	}


    @Transactional
    public void saveMenuHistory(MenuHistory menuHistory) {
        saveOrUpdate(menuHistory);	     
    }



    @Transactional
	public int deleteMenus(Integer docId, Integer docVersionNo) {
        return getSession().getNamedQuery("Menu.deleteMenus")
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .executeUpdate();
	}


	@Transactional
	public void deleteMenu(MenuDomainObject menu) {
		delete(menu);
	}
}
