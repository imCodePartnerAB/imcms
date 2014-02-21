package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocLanguage;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
public class DocLanguageDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<DocLanguage> getAll() {
        return entityManager.createQuery("SELECT l FROM Language l", DocLanguage.class).getResultList();
    }

    public DocLanguage getById(int id) {
        return entityManager.find(DocLanguage.class, id);
    }

    public DocLanguage getByCode(String code) {
        return entityManager.createQuery("SELECT l FROM Language l WHERE l.code = ?1", DocLanguage.class)
                .setParameter(1, code)
                .getSingleResult();
    }

    public DocLanguage save(DocLanguage language) {
        return entityManager.merge(language);
    }

    public int deleteById(int id) {
        return entityManager.createQuery("DELETE FROM DocLanguage l WHERE l.id = ?1")
                .setParameter(1, id)
                .executeUpdate();
    }

    public int deleteByCode(String code) {
        return entityManager.createQuery("DELETE FROM DocLanguage l WHERE l.code = ?1")
                .setParameter(1, code)
                .executeUpdate();
    }
}
