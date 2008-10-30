package com.imcode.imcms.dao;

import java.util.List;

import imcode.server.document.textdocument.MenuDomainObject;

public interface MenuDao {

	MenuDomainObject getMenu(long id);
	
	MenuDomainObject getMenu(int metaId, int index);
	
	List<MenuDomainObject> getMenus(int metaId);
	
	MenuDomainObject saveMenu(MenuDomainObject menu);
	
	void deleteMenu(MenuDomainObject menu);
}
