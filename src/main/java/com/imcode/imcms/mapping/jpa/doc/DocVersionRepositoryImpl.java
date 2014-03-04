package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.mapping.jpa.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created by ajosua on 26/02/14.
 */
@Transactional
class DocVersionRepositoryImpl implements DocVersionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates and returns a new version of a document.
     * If document does not have version creates version with number 0 otherwise creates version with next version number.
     *
     * @return new document version.
     */
    //todo: check locking
    @Override
    public Version create(int docId, int userId) {
        User creator = entityManager.getReference(User.class, userId);

        List<Version> latestVersionList = entityManager.createNamedQuery("DocVersion.findLatest", Version.class)
                .setParameter(1, docId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();

        Version latestVersion = latestVersionList.isEmpty() ? null : latestVersionList.get(0);

        int no = latestVersion != null ? latestVersion.getNo() + 1 : 0;
        Date now = new Date();
        Version version = new Version();

        version.setDocId(docId);
        version.setNo(no);
        version.setCreatedDt(now);
        version.setCreatedBy(creator);
        version.setModifiedDt(now);
        version.setModifiedBy(creator);

        entityManager.persist(version);
        entityManager.flush();

        return version;
    }

    @Override
    //todo: check locking
    public void setDefault(int docId, int docVersionNo, int userId) {
        Version version = entityManager.createNamedQuery("DocVersion.findByDocIdAndNo", Version.class)
                .setParameter(1, docId)
                .setParameter(2, docVersionNo)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getSingleResult();

        User user = entityManager.getReference(User.class, userId);

        entityManager.createNamedQuery("DocVersion.setDefault")
                .setParameter("docId", docId)
                .setParameter("defaultVersionNo", docVersionNo)
                .setParameter("publisherId", userId)
                .executeUpdate();
    }
}
