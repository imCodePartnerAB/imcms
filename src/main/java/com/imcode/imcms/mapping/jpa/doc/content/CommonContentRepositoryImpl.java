package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.doc.Language;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by ajosua on 26/02/14.
 */
class CommonContentRepositoryImpl implements CommonContentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void deleteByDocIdAndDocLanguageCode(int docId, String code) {
        List<CommonContent> dccList = entityManager.createQuery("SELECT d FROM DocCommonContent WHERE d.docId = ?1 AND d.docLanguage.code = ?2", CommonContent.class)
                .setParameter(1, docId)
                .setParameter(2, code)
                .getResultList();

        if (!dccList.isEmpty()) {
            entityManager.remove(dccList.get(0));
        }
    }

    @Override
    public void deleteByDocIdAndDocLanguage(int docId, Language language) {
        List<CommonContent> dccList = entityManager.createQuery("SELECT d FROM DocCommonContent WHERE d.docId = ?1 AND d.docLanguage = ?2", CommonContent.class)
                .setParameter(1, docId)
                .setParameter(2, language)
                .getResultList();

        if (!dccList.isEmpty()) {
            entityManager.remove(dccList.get(0));
        }
    }

    @Override
    public void deleteByDocId(int docId) {
        List<CommonContent> dccList = entityManager.createQuery("SELECT d FROM DocCommonContent WHERE d.docId = ?1", CommonContent.class)
                .setParameter(1, docId)
                .getResultList();

        for (CommonContent dcc : dccList) {
            entityManager.remove(dcc);
        }
    }
}
