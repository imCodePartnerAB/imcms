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
class VersionRepositoryImpl implements VersionRepositoryCustom {

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
    public DocVersion create(int docId, int userId) {
        User creator = entityManager.getReference(User.class, userId);

        List<DocVersion> latestDocVersionList = entityManager.createNamedQuery("DocVersion.findLatest", DocVersion.class)
                .setParameter(1, docId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();

        DocVersion latestDocVersion = latestDocVersionList.isEmpty() ? null : latestDocVersionList.get(0);

        int no = latestDocVersion != null ? latestDocVersion.getNo() + 1 : 0;
        Date now = new Date();
        DocVersion docVersion = new DocVersion();

        docVersion.setDocId(docId);
        docVersion.setNo(no);
        docVersion.setCreatedDt(now);
        docVersion.setCreatedBy(creator);
        docVersion.setModifiedDt(now);
        docVersion.setModifiedBy(creator);

        entityManager.persist(docVersion);
        entityManager.flush();

        return docVersion;
    }

    @Override
    //todo: check locking
    public void setDefault(int docId, int docVersionNo, int userId) {
        DocVersion docVersion = entityManager.createNamedQuery("DocVersion.findByDocIdAndNo", DocVersion.class)
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
