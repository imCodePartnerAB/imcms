package com.imcode.imcms.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Temporal native queries - moved from the DocumentMapper.
 * TODO: Rewrite native queries using HQL
 */
public class NativeQueriesDao extends HibernateTemplate {

	@Transactional
	public List<String> getAllMimeTypes() {
        String sqlStr = "SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id";
        
        return getSession().createSQLQuery(sqlStr).list();
	}
	
	@Transactional
	public List<String[]> getAllMimeTypesWithDescriptions(String languageIso639_2) {
	     String sqlStr = "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id";
	     
	     return getSession().createSQLQuery(sqlStr)
	     	.setParameter(0, languageIso639_2)
	     	.list();
	}
	
	@Transactional
    public List<String[]> getParentDocumentAndMenuIdsForDocument(Integer documentId) {
        String sqlStr = "SELECT meta_id,menu_index FROM childs, menus WHERE menus.menu_id = childs.menu_id AND to_meta_id = ?";
        
	     return getSession().createSQLQuery(sqlStr)
	     	.setParameter(0, documentId)
	     	.list();        
    }
    
    @Transactional
    public List<Integer> getDocumentsWithPermissionsForRole(Integer roleId) {
        String sqlStr = "SELECT meta_id FROM roles_rights WHERE role_id = ? ORDER BY meta_id";
        
        return getSession().createSQLQuery(sqlStr).setParameter(0, roleId).list();
    }   
    
    
    @Transactional
    public Map<Integer, String> getAllDocumentTypeIdsAndNamesInUsersLanguage(String languageIso639_2) {
    	String sql = "SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type";
    	
    	List<Object[]> rows = getSession().createSQLQuery(sql).setParameter(0, languageIso639_2).list();
    	Map<Integer, String> allDocumentTypeIdsAndNamesInUsersLanguage = new TreeMap<Integer, String>();
    	
    	for (Object[] row: rows) {
    		allDocumentTypeIdsAndNamesInUsersLanguage.put((Integer)row[0], (String)row[1]);
    	}
    		
        return allDocumentTypeIdsAndNamesInUsersLanguage;
    }
    
    @Transactional
    public List<Integer[]> getDocumentMenuPairsContainingDocument(Integer documentId) {
        String sqlSelectMenus = "SELECT meta_id, menu_index FROM menus, childs WHERE menus.menu_id = childs.menu_id AND childs.to_meta_id = ? ORDER BY meta_id, menu_index";
                
        return getSession().createSQLQuery(sqlSelectMenus).setParameter(0, documentId).list();
    }    
}