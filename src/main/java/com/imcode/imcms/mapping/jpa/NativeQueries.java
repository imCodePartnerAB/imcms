package com.imcode.imcms.mapping.jpa;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Native queries - moved from the DocumentMapper.
 * TODO: Rewrite native queries using QL
 * TODO: test
 */
@Component
@Transactional
public class NativeQueries {

    @PersistenceContext
    private EntityManager entityManager;

    public List<String> getAllMimeTypes() {
        return entityManager.createNativeQuery("SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id", String.class)
                .getResultList();
    }

    public List<Integer> getDocumentsWithPermissionsForRole(int roleId) {
        return entityManager.createNativeQuery("SELECT meta_id FROM roles_rights WHERE role_id = ? ORDER BY meta_id", Integer.class)
                .setParameter(1, roleId)
                .getResultList();
    }

    public List<Integer> getParentDocsIds(int docId) {
        return entityManager.createNativeQuery("SELECT doc_id FROM imcms_text_doc_menus menus, imcms_text_doc_menu_items childs WHERE menus.id = childs.menu_id AND childs.to_doc_id = ? ORDER BY doc_id, no", Integer.class)
                .setParameter(1, docId)
                .getResultList();

    }

    public List<String[]> getAllMimeTypesWithDescriptions(String languageIso639_2) {
        return entityManager.createNativeQuery("SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id", String[].class)
                .setParameter(1, languageIso639_2)
                .getResultList();
    }


    public List<Integer[]> getParentDocumentAndMenuIdsForDocument(int docId) {
        return entityManager.createNativeQuery("SELECT doc_id, no FROM imcms_text_doc_menu_items childs, imcms_text_doc_menus menus WHERE menus.id = childs.menu_id AND to_doc_id=?", Integer[].class)
                .setParameter(1, docId)
                .getResultList();
    }



    public Map<Integer, String> getAllDocumentTypeIdsAndNamesInUsersLanguage(String languageIso639_2) {
        List<Object[]> rows = entityManager.createNativeQuery("SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type")
                .setParameter(1, languageIso639_2)
                .getResultList();

        Map<Integer, String> result = new HashMap<>();

        for (Object[] row : rows) {
            result.put((Integer) row[0], (String) row[1]);
        }

        return result;
    }

    public List<Integer[]>  getDocumentMenuPairsContainingDocument(int docId) {
        return entityManager.createNativeQuery("SELECT doc_id, no FROM imcms_text_doc_menus menus, imcms_text_doc_menu_items childs WHERE menus.id = childs.menu_id AND childs.to_doc_id=? ORDER BY doc_id,no", Integer[].class)
                .setParameter(1, docId)
                .getResultList();
    }
}
