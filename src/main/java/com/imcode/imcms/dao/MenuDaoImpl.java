package com.imcode.imcms.dao;

import java.util.List;

import imcode.server.document.textdocument.MenuDomainObject;

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
	public MenuDomainObject saveMenu(MenuDomainObject menu) {
		saveOrUpdate(menu);
		
		return menu;
	}

	@Transactional	
	public void deleteMenu(MenuDomainObject menu) {
		delete(menu);
	}

}
