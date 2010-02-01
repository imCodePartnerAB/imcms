package com.imcode.imcms.dao;

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
		String hql = "SELECT m FROM Menu m  WHERE m.metaId = :docId AND m.docVersionNo = :docVersionNo";

		return (List<MenuDomainObject>)findByNamedParam(hql,
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
	public void deleteMenu(MenuDomainObject menu) {
		delete(menu);
	}
}
