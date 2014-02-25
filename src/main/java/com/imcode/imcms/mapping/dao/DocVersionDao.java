package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocVersion;
import com.imcode.imcms.mapping.orm.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Repository
public interface DocVersionDao extends JpaRepository<DocVersion, Integer>, DocVersionDaoCustom {

    List<DocVersion> findByDocId(int docId);

    DocVersion findByDocIdAndNo(int docId, int no);

    @Query(name = "DocVersion.findDefault")
    DocVersion findDefault(int docId);

    @Query(name = "DocVersion.findLatest")
    DocVersion findLatest(int docId);
}

interface DocVersionDaoCustom {

    DocVersion create(int docId, int userId);

    void setDefault(int docId, int docVersionNo, int userId);
}

@Transactional
class DocVersionDaoImpl implements DocVersionDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates and returns a new version of a document.
     * If document does not have version creates version with number 0 otherwise creates version with next version number.
     *
     * @return new document version.
     */
    //fixme: check locking
    @Override
    public DocVersion create(int docId, int userId) {
        User creator = entityManager.getReference(User.class, userId);

        List<DocVersion> latestVersionList = entityManager.createNamedQuery("DocVersion.findLatest", DocVersion.class)
                .setParameter(1, docId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();

        DocVersion latestVersion = latestVersionList.isEmpty() ? null : latestVersionList.get(0);

        int no = latestVersion != null ? latestVersion.getNo() + 1 : 0;
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
    //fixme: check locking
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