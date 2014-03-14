package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.mapping.jpa.User;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Transactional
class VersionRepositoryImpl implements VersionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private VersionRepository versionRepository;

    /**
     * Creates and returns a new version of a document.
     * If document does not have version creates version with number 0 otherwise creates version with next version number.
     *
     * @return new document version.
     */
    @Override
    public Version create(int docId, int userId) {
        User creator = entityManager.getReference(User.class, userId);
        Integer latestNo = versionRepository.findLatestNoForUpdate(docId);
        int no = latestNo == null ? 0 : latestNo + 1;
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
}
