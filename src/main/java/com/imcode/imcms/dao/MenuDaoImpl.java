package com.imcode.imcms.dao;

import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;

import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

public class MenuDaoImpl extends HibernateTemplate implements MenuDao {

	@Transactional
	public MenuDomainObject getMenu(long id) {
		return (MenuDomainObject)get(MenuDomainObject.class, id);
	}
	
	@Transactional
	public MenuDomainObject getMenu(int metaId, int index) {
		String hql = "SELECT m FROM Menu m  WHERE m.metaId = :metaId AND m.index = :index";
		
		return (MenuDomainObject)getSession().createQuery(hql)
			.setParameter("metaId", metaId)
			.setParameter("index", index)
			.uniqueResult();
	}
	
	@Transactional
	public List<MenuDomainObject> getMenus(int metaId) {
		String hql = "SELECT m FROM Menu m  WHERE m.metaId = :metaId";
		
		return (List<MenuDomainObject>)findByNamedParam(hql, "metaId", metaId);
	}	
	
	
	@Transactional
	public Map<Integer, MenuDomainObject> saveMenu(int metaId, Map<Integer, MenuDomainObject> menusMap) {
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
