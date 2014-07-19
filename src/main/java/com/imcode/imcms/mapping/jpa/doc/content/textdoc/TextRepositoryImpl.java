package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.Version;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

class TextRepositoryImpl implements TextRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Text findFirst(Version version, Language language, LoopEntryRef loopEntryRef) {
        return entityManager.createQuery(
                "SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.loopEntryRef = ?3",
                Text.class)
                .setParameter(1, version).setParameter(2, language).setParameter(3, loopEntryRef)
                .setMaxResults(1).getResultList().stream().findFirst().orElse(null);
    }
}
