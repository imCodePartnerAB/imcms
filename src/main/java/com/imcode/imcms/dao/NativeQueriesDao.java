package com.imcode.imcms.dao;

import imcode.server.document.DocumentDomainObject;
import imcode.util.Utility;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.db.commands.SqlQueryCommand;

/**
 * Temporal native queries - moved from the DocumentMapper.
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
    public String[][] getParentDocumentAndMenuIdsForDocument(DocumentDomainObject document) {
        //String sqlStr = "SELECT meta_id,menu_index FROM childs, menus WHERE menus.menu_id = childs.menu_id AND to_meta_id = ?";
        //String[] parameters = new String[]{"" + document.getId()};
        //return (String[][]) getDatabase().execute(new SqlQueryCommand(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
		return null;
    }	
}