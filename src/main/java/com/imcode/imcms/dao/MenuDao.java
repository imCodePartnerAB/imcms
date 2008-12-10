package com.imcode.imcms.dao;

import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TreeSortKeyDomainObject;

public class MenuDao extends HibernateTemplate {

	/*
	@Transactional
	public MenuDomainObject getMenu(long id) {
		return (MenuDomainObject)get(MenuDomainObject.class, id);
	}
	*/
	
	/*
	@Transactional
	public MenuDomainObject getMenu(int metaId, int index) {
		String hql = "SELECT m FROM Menu m  WHERE m.metaId = :metaId AND m.index = :index";
		
		return (MenuDomainObject)getSession().createQuery(hql)
			.setParameter("metaId", metaId)
			.setParameter("index", index)
			.uniqueResult();
	}
	*/
	
	@Transactional
	public List<MenuDomainObject> getMenus(Long metaId) {
		String hql = "SELECT m FROM Menu m  WHERE m.metaId = :metaId";
		
		List<MenuDomainObject> menus = (List<MenuDomainObject>)findByNamedParam(hql, "metaId", metaId);
		
		for (MenuDomainObject menu: menus) {
			for (MenuItemDomainObject item: menu.getItemsMap().values()) {
				item.setTreeSortKey(new TreeSortKeyDomainObject(item.getTreeSortIndex()));
			}
		}
		
		return menus;
	}	
	
	
	@Transactional
	public Map<Integer, MenuDomainObject> saveDocumentMenus(Long metaId, Map<Integer, MenuDomainObject> menusMap) {
		for (Map.Entry<Integer, MenuDomainObject> entry: menusMap.entrySet()) {
			MenuDomainObject menu = entry.getValue();
			
			menu.setMetaId(metaId);
			menu.setIndex(entry.getKey());
			
			for (Map.Entry<Integer, MenuItemDomainObject> itemEntry: menu.getItemsMap().entrySet()) {
				MenuItemDomainObject item = itemEntry.getValue();
				item.setTreeSortIndex(item.getTreeSortKey().toString());
			}
			
			saveOrUpdate(menu);			
		}
		
		return menusMap;
	}

	@Transactional	
	public void deleteMenu(MenuDomainObject menu) {
		delete(menu);
	}

}
