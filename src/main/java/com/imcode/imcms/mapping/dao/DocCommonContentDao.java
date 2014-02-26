package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocCommonContent;
import com.imcode.imcms.mapping.orm.DocLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public interface DocCommonContentDao extends JpaRepository<DocCommonContent, Integer>, DocCommonContentDaoCustom {

    List<DocCommonContent> findByDocId(int docId);

    DocCommonContent findByDocIdAndDocLanguage(int docId, DocLanguage language);

    DocCommonContent findByDocIdAndDocLanguageCode(int docId, String code);
}

interface DocCommonContentDaoCustom {

    void deleteByDocIdAndDocLanguage(int docId, DocLanguage docLanguage);

    void deleteByDocIdAndDocLanguageCode(int docId, String code);

    void deleteByDocId(int docId);
}

class DocCommonContentDaoImpl implements DocCommonContentDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void deleteByDocIdAndDocLanguageCode(int docId, String code) {
        List<DocCommonContent> dccList = entityManager.createQuery("SELECT d FROM DocCommonContent WHERE d.docId = ?1 AND d.docLanguage.code = ?2", DocCommonContent.class)
                .setParameter(1, docId)
                .setParameter(2, code)
                .getResultList();

        if (!dccList.isEmpty()) {
            entityManager.remove(dccList.get(0));
        }
    }

    @Override
    public void deleteByDocIdAndDocLanguage(int docId, DocLanguage docLanguage) {
        List<DocCommonContent> dccList = entityManager.createQuery("SELECT d FROM DocCommonContent WHERE d.docId = ?1 AND d.docLanguage = ?2", DocCommonContent.class)
                .setParameter(1, docId)
                .setParameter(2, docLanguage)
                .getResultList();

        if (!dccList.isEmpty()) {
            entityManager.remove(dccList.get(0));
        }
    }

    @Override
    public void deleteByDocId(int docId) {
        List<DocCommonContent> dccList = entityManager.createQuery("SELECT d FROM DocCommonContent WHERE d.docId = ?1", DocCommonContent.class)
                .setParameter(1, docId)
                .getResultList();

        for (DocCommonContent dcc : dccList) {
            entityManager.remove(dcc);
        }
    }
}