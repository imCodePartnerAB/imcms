package com.imcode.imcms.dao;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionTag;

import java.util.Date;
import java.util.List;

/**
 * 
 */
public class DocumentVersionDao extends HibernateTemplate {

	/**
	 * Creates and returns a new working version of a document.
	 *
	 * Tags existing working version as postponed if it is already present.
	 *
	 * @return next document version.
	 */
	@Transactional
	public DocumentVersion createWorkingVersion(Integer metaId, Integer userId) {
		DocumentVersion workingVersion;

		DocumentVersion latestVersion = (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getLastVersion")
			.setParameter("metaId", metaId)
			.uniqueResult();

		if (latestVersion == null) {
			workingVersion = new DocumentVersion(metaId, 1, DocumentVersionTag.WORKING);
		} else {
			// This must always evaluates to true
			if (latestVersion.getTag() == DocumentVersionTag.WORKING) {
				latestVersion.setTag(DocumentVersionTag.POSTPONED);
				update(latestVersion);
			}

			workingVersion = new DocumentVersion(metaId,
					latestVersion.getNumber() + 1,
					DocumentVersionTag.WORKING);
		}

		workingVersion.setUserId(userId);
		workingVersion.setCreatedDt(new Date());

		save(workingVersion);

		return workingVersion;
	}


	/**
	 * Returns all versions for the document.
	 *
	 * @param metaId meta id.
	 * @return available versions for the document.
	 */
	@Transactional
	public List<DocumentVersion> getDocumentVersions(Integer metaId) {
		return findByNamedQueryAndNamedParam("DocumentVersion.getByMetaId",
				"metaId", metaId);
	}


	@Transactional
	public DocumentVersion getPublishedVersion(Integer metaId) {
		return (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getPublishedVersion")
		    .setParameter("metaId", metaId)
		    .uniqueResult();
	}


	@Transactional
	public DocumentVersion getWorkingVersion(Integer metaId) {
		return (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getPublishedVersion")
		    .setParameter("metaId", metaId)
		    .uniqueResult();
	}


	@Transactional
	public DocumentVersion getVersion(Integer metaId, Integer versionNumber) {
		return (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getByMetaIdAndVersionNumber")
		    .setParameter("metaId", metaId)
            .setParameter("versionNumber", versionNumber)
		    .uniqueResult();
	}
}
