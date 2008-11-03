package com.imcode.imcms.dao;

import java.util.List;
import java.util.Map;

import imcode.server.document.textdocument.MenuDomainObject;

public interface MenuDao {

	MenuDomainObject getMenu(long id);
	
	MenuDomainObject getMenu(int metaId, int index);
	
	List<MenuDomainObject> getMenus(int metaId);
	
	Map<Integer, MenuDomainObject> saveDocumentMenus(int metaId, Map<Integer, MenuDomainObject> menusMap);
	
	void deleteMenu(MenuDomainObject menu);
}
