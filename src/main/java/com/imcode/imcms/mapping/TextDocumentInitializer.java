package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TemplateNames;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TreeSortKeyDomainObject;
import imcode.util.LazilyLoadedObject;
import imcode.util.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

import com.imcode.db.Database;
import com.imcode.imcms.api.Include;
import com.imcode.imcms.dao.IncludeDao;
import com.imcode.imcms.dao.MenuDao;
import com.imcode.imcms.dao.TemplateNamesDao;

public class TextDocumentInitializer {

    private final static Logger LOG = Logger.getLogger(TextDocumentInitializer.class);

    //private final Collection documentIds;
    private final Database database;
    private final DocumentGetter documentGetter;
    //private Map documentsMenuItems;

    static final String SQL_GET_MENU_ITEMS = "SELECT meta_id, menus.menu_id, menu_index, sort_order, to_meta_id, manual_sort_order, tree_sort_index FROM menus,childs WHERE menus.menu_id = childs.menu_id AND meta_id ";

    public TextDocumentInitializer(Database database, DocumentGetter documentGetter, Collection documentIds) {
        this.database = database;
        this.documentGetter = documentGetter;
        //this.documentIds = documentIds;
    }
    
    // TODO: refactor
    public void initialize(TextDocumentDomainObject document) {
        Integer documentId = new Integer(document.getId()) ;
        //document.setLazilyLoadedMenus(new LazilyLoadedObject(new MenusLoader(documentId)));
        
        // document.setTexts???
        // document.setImages???
        
        // init includes    	     	
   		IncludeDao includeDao = (IncludeDao)Imcms.getServices().getSpringBean("includeDao");
   		List<Include> includes = includeDao.getDocumentIncludes(documentId);
   		
   		TemplateNamesDao tnDao = (TemplateNamesDao)Imcms.getServices().getSpringBean("templateNamesDao");
   		TemplateNames templateNames = tnDao.getTemplateNames(documentId);  
    				
		document.setIncludes(includes);
		document.setTemplateNames(templateNames);
		
		MenuDao menuDao = (MenuDao)Imcms.getServices().getSpringBean("menuDao");
		List<MenuDomainObject> menus = menuDao.getMenus(documentId);
		
        Set destinationDocumentIds = new HashSet();
        BatchDocumentGetter batchDocumentGetter = new BatchDocumentGetter(destinationDocumentIds, documentGetter);
        Map<Integer, MenuDomainObject> menusMap = new HashMap<Integer, MenuDomainObject>();
        
        for (MenuDomainObject menu: menus) {
        	menusMap.put(menu.getIndex(), menu);
        	
        	for (Map.Entry<Integer, MenuItemDomainObject> entry: menu.getItemsMap().entrySet()) {
        		Integer destinationDocumentId = entry.getKey();
        		MenuItemDomainObject menuItem = entry.getValue();
        		
        		GetterDocumentReference gtr = new GetterDocumentReference(destinationDocumentId, batchDocumentGetter);
        		menuItem.setDocumentReference(gtr);     
        		
        		destinationDocumentIds.add(destinationDocumentId);
        	}
        }
        
        document.setMenusMap(menusMap);
    }

    /*
    private class MenusLoader implements LazilyLoadedObject.Loader {

        private final Integer documentId;

        MenusLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsMenuItems();
            DocumentMenusMap menusMap = (DocumentMenusMap) documentsMenuItems.get(documentId);
            if ( null == menusMap ) {
                menusMap = new DocumentMenusMap();
            }
            return menusMap;
        }

        void initDocumentsMenuItems() {
            if ( null == documentsMenuItems ) {
                documentsMenuItems = new HashMap();
                final Set destinationDocumentIds = new HashSet();
                final BatchDocumentGetter batchDocumentGetter = new BatchDocumentGetter(destinationDocumentIds, documentGetter);
                DocumentInitializer.executeWithAppendedIntegerInClause(database, SQL_GET_MENU_ITEMS, documentIds, new ResultSetHandler() {
                    public Object handle(ResultSet rs) throws SQLException {
                        while ( rs.next() ) {
                            int documentId = rs.getInt(1);
                            int menuId = rs.getInt(2);
                            int menuIndex = rs.getInt(3);
                            int menuSortOrder = rs.getInt(4);
                            Integer destinationDocumentId = new Integer(rs.getInt(5));
                            Integer sortKey = Utility.getInteger(rs.getObject(6));

                            destinationDocumentIds.add(destinationDocumentId);
                            Map documentMenus = (Map) documentsMenuItems.get(new Integer(documentId));
                            if ( null == documentMenus ) {
                                documentMenus = new DocumentMenusMap();
                                documentsMenuItems.put(new Integer(documentId), documentMenus);
                            }

                            MenuDomainObject menu = (MenuDomainObject) documentMenus.get(new Integer(menuIndex));
                            if ( null == menu ) {
                                menu = new MenuDomainObject(menuId, menuSortOrder);
                                documentMenus.put(new Integer(menuIndex), menu);
                            }
                            MenuItemDomainObject menuItem = new MenuItemDomainObject(new GetterDocumentReference(destinationDocumentId.intValue(), batchDocumentGetter), sortKey, new TreeSortKeyDomainObject(rs.getString(7)));
                            menu.addMenuItemUnchecked(menuItem);
                        }
                        return null;
                    }
                });
            }
        }
    }
	*/
}
