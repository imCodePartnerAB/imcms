package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocVersion;
import imcode.server.user.UserDomainObject;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Transactional
public class DocVersionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public DocVersion getByDocIdAndNo(int docId, int no) {
        return entityManager.createNamedQuery("DocVersion.getByDocIdAndNo", DocVersion.class)
                .setParameter("docId", docId)
                .setParameter("no", no)
                .getSingleResult();
    }

    /**
     * Creates and returns a new version of a document.
     * If document does not have version creates version with number 0 otherwise creates version with next version number.
     *
     * @return new document version.
     */
    // fixme: use db lock
    // fixme: created by, modified by
    public DocVersion createVersion(int docId, int userId) {
        DocVersion latestVersion = getLatestVersion(docId);
        int no = latestVersion != null ? latestVersion.getNo() + 1 : 0;
        Date now = new Date();
        DocVersion docVersion = new DocVersion();

        docVersion.setDocId(docId);
        docVersion.setNo(no);
        docVersion.setCreatedDt(now);
        //docVersion.setCreatedBy(userId);
        docVersion.setModifiedDt(now);
        //docVersion.setModifiedBy(userId);

        entityManager.persist(docVersion);
        entityManager.flush();

        return docVersion;
    }


    public DocVersion getLatestVersion(int docId) {
        return entityManager.createNamedQuery("DocVersion.getLatestVersion", DocVersion.class)
                .setParameter("docId", docId)
                .getSingleResult();
    }

    /**
     * Returns all versions for the document.
     *
     * @param docId meta id.
     * @return available versions for the document.
     */
    public List<DocVersion> getAllVersions(int docId) {
        return entityManager.createNamedQuery("DocVersion.getByDocId", DocVersion.class)
                .setParameter("docId", docId)
                .getResultList();
    }


    public DocVersion getVersion(int docId, int no) {
        return entityManager.createNamedQuery("DocVersion.getByDocIdAndNo", DocVersion.class)
                .setParameter("docId", docId)
                .setParameter("no", no)
                .getSingleResult();
    }


    public DocVersion getDefaultVersion(int docId) {
        return entityManager.createNamedQuery("DocVersion.getDefaultVersion", DocVersion.class)
                .setParameter("docId", docId)
                .getSingleResult();
    }

    // fixme: use db lock
    public void changeDefaultVersion(DocVersion newDefaultVersion, UserDomainObject publisher) {
        int result = entityManager.createNamedQuery("DocVersion.changeDefaultVersion")
                .setParameter("docId", newDefaultVersion.getDocId())
                .setParameter("defaultVersionNo", newDefaultVersion.getNo())
                .setParameter("publisherId", publisher.getId())
                .executeUpdate();

        if (result == 0) {
            throw new RuntimeException(
                    String.format(
                            "Default document version can not be changed. Version %s does not exists.",
                            newDefaultVersion
                    )
            );
        }
    }
}
