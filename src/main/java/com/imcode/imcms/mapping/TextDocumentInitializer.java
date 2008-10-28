package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.document.DirectDocumentReference;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.CopyableHashMap;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.db.Database;
import com.imcode.imcms.api.I18nException;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.Include;
import com.imcode.imcms.dao.IncludeDao;

public class TextDocumentInitializer {

    private final static Logger LOG = Logger.getLogger(TextDocumentInitializer.class);

    private final Collection documentIds;
    private final Database database;
    private final DocumentGetter documentGetter;
    private Map documentsMenuItems;
    private Map documentsTemplateIds;

    static final String SQL_GET_MENU_ITEMS = "SELECT meta_id, menus.menu_id, menu_index, sort_order, to_meta_id, manual_sort_order, tree_sort_index FROM menus,childs WHERE menus.menu_id = childs.menu_id AND meta_id ";

    public TextDocumentInitializer(Database database, DocumentGetter documentGetter, Collection documentIds) {
        this.database = database;
        this.documentGetter = documentGetter;
        this.documentIds = documentIds;
    }
    
    // TODO: refactor
    public void initialize(TextDocumentDomainObject document) {
        Integer documentId = new Integer(document.getId()) ;
        document.setLazilyLoadedMenus(new LazilyLoadedObject(new MenusLoader(documentId)));
        document.setLazilyLoadedTemplateIds(new LazilyLoadedObject(new TemplateIdsLoader(documentId)));
        
        // document.setTexts???
        // document.setImages???
        
        // init includes    	     	
   		IncludeDao dao = (IncludeDao)Imcms.getServices().getSpringBean("includeDao");
   		List<Include> includes = dao.getDocumentIncludes(documentId);
    				
		document.setIncludes(includes);
    }

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

    private class TemplateIdsLoader implements LazilyLoadedObject.Loader {

        private final Integer documentId;

        TemplateIdsLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsTemplateIds();
            TextDocumentDomainObject.TemplateNames templateNames = (TextDocumentDomainObject.TemplateNames) documentsTemplateIds.get(documentId) ;
            if (null == templateNames ) {
                templateNames = new TextDocumentDomainObject.TemplateNames();
            }
            return templateNames ;
        }

        private void initDocumentsTemplateIds() {
            if ( null == documentsTemplateIds ) {
                documentsTemplateIds = new HashMap();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id, template_name, group_id, default_template, default_template_1, default_template_2 FROM text_docs WHERE meta_id ", documentIds, new ResultSetHandler() {
                    public Object handle(ResultSet rs) throws SQLException {
                        while ( rs.next() ) {
                            Integer documentId = new Integer(rs.getInt("meta_id"));
                            TextDocumentDomainObject.TemplateNames templateNames = new TextDocumentDomainObject.TemplateNames();
                            templateNames.setTemplateName(rs.getString("template_name"));
                            templateNames.setTemplateGroupId(rs.getInt(3));
                            templateNames.setDefaultTemplateName(rs.getString("default_template"));
                            String defaultTemplateIdForR1 = rs.getString("default_template_1");
                            String defaultTemplateIdForR2 = rs.getString("default_template_2");
                            templateNames.setDefaultTemplateNameForRestricted1(defaultTemplateIdForR1);
                            templateNames.setDefaultTemplateNameForRestricted2(defaultTemplateIdForR2);
                            documentsTemplateIds.put(documentId, templateNames);
                        }
                        return null;
                    }
                });
            }
        }

    }

}
