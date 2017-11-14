package com.imcode.imcms.mapping.jpa;

import org.springframework.stereotype.Component;
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

    @SuppressWarnings("unchecked")
    public List<Integer> getDocumentsWithPermissionsForRole(int roleId) {
        return entityManager.createNativeQuery("SELECT meta_id FROM roles_rights WHERE role_id = ? ORDER BY meta_id")
                .setParameter(1, roleId)
                .getResultList();
    }

    public Map<Integer, String> getAllDocumentTypeIdsAndNamesInUsersLanguage(String languageIso639_2) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery("SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type")
                .setParameter(1, languageIso639_2)
                .getResultList();

        Map<Integer, String> result = new HashMap<>();

        for (Object[] row : rows) {
            result.put((Integer) row[0], (String) row[1]);
        }

        return result;
    }
}
