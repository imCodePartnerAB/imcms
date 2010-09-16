package com.imcode.imcms.dao;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

		List<Object[]> mimeTypes = getSession().createSQLQuery(sqlStr)
				.setParameter(0, languageIso639_2)
				.list();

		List<String[]> tmp = new ArrayList<String[]>();
		for (Object[] objects : mimeTypes) {
			if (null != objects && objects.length > 1) {
				String[] mimeType = new String[2];
				mimeType[0] = (String) objects[0];
				mimeType[1] = (String) objects[1];
				tmp.add(mimeType);
			}
		}

		return tmp;
	}

	@Transactional
	public List<Object[]> getParentDocumentAndMenuIdsForDocument(Integer documentId) {
		String sqlStr = "SELECT doc_id, no FROM imcms_text_doc_menu_items childs, imcms_text_doc_menus menus WHERE menus.id = childs.menu_id AND to_doc_id = ?";

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

		for (Object[] row : rows) {
			allDocumentTypeIdsAndNamesInUsersLanguage.put((Integer) row[0], (String) row[1]);
		}

		return allDocumentTypeIdsAndNamesInUsersLanguage;
	}

	@Transactional
	public List<Integer[]> getDocumentMenuPairsContainingDocument(Integer documentId) {
		String sqlSelectMenus = "SELECT doc_id, no FROM imcms_text_doc_menus menus, imcms_text_doc_menu_items childs WHERE menus.id = childs.menu_id AND childs.to_doc_id = ? ORDER BY doc_id, no";
        List<Object[]> items = getSession().createSQLQuery(sqlSelectMenus).setParameter(0, documentId).list();
        List<Integer[]> pairs = new LinkedList<Integer[]>();

        for (Object[] item: items) {
            pairs.add(new Integer[] {(Integer)item[0], (Integer)item[1]});
        }

		return pairs;
	}
}