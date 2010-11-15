package com.imcode.imcms.dao;

import imcode.server.user.UserDomainObject;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.imcode.imcms.api.DocumentVersion;

import java.util.List;
import java.util.Date;

public class DocumentVersionDao extends HibernateTemplate {

	/**
	 * Creates and returns a new version of a document.
     * If document does not have version creates version with number 0 otherwise creates version with next version number.
	 *
	 * @return new document version.
	 */
	@Transactional
	public synchronized DocumentVersion createVersion(Integer docId, Integer userId) {
		DocumentVersion latestVersion = (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getLatestVersion")
			.setParameter("docId", docId)
			.uniqueResult();

        Integer versionNumber = latestVersion == null
                ? 0
                : latestVersion.getNo() + 1;

        Date now = new Date();
        DocumentVersion version =  new DocumentVersion(docId, versionNumber, userId, now);
        version.setModifiedBy(userId);
        version.setModifiedDt(now);

		save(version);

		return version;
	}


	/**
	 * Returns all versions for the document.
	 *
	 * @param docId meta id.
	 * @return available versions for the document.
	 */
	@Transactional
	public List<DocumentVersion> getAllVersions(Integer docId) {
		return findByNamedQueryAndNamedParam("DocumentVersion.getByDocId",
				"docId", docId);
	}

    
	@Transactional
	public DocumentVersion getDefaultVersion(Integer docId) {
		return (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getDefaultVersion")
		    .setParameter("docId", docId)
		    .uniqueResult();
	}

	@Transactional
	public void changeDefaultVersion(Integer docId, DocumentVersion version, UserDomainObject user) {
		getSession()
			.getNamedQuery("DocumentVersion.changeDefaultVersion")
            .setParameter("defaultVersionNo", version.getNo())
            .setParameter("modifiedDatetime", version.getModifiedDt())
            .setParameter("publisherId", user.getId())   
            .setParameter("docId", docId)    
		    .executeUpdate();
	}


	@Transactional
	public DocumentVersion getVersion(Integer docId, Integer no) {
		return (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getByDocIdAndNo")
		    .setParameter("docId", docId)
            .setParameter("no", no)
		    .uniqueResult();
	}
}
