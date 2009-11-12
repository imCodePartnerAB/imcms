package com.imcode.imcms.dao;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.imcode.imcms.api.DocumentVersion;

import java.util.List;

/**
 *     TODO: implement get active version.
 */
public class DocumentVersionDao extends HibernateTemplate {

	/**
	 * Creates and returns a new version of a document.
     * If document does not have version creates version with number 0 otherwise creates version with next version number.
	 *
	 * @return new document version.
	 */
	@Transactional
	public synchronized DocumentVersion createVersion(Integer metaId, Integer userId) {
		DocumentVersion latestVersion = (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getLatestVersion")
			.setParameter("metaId", metaId)
			.uniqueResult();

        Integer versionNumber = latestVersion == null
                ? 0
                : latestVersion.getNo() + 1;

        DocumentVersion version =  new DocumentVersion(metaId, versionNumber, userId);

		save(version);

		return version;
	}


	/**
	 * Returns all versions for the document.
	 *
	 * @param metaId meta id.
	 * @return available versions for the document.
	 */
	@Transactional
	public List<DocumentVersion> getAllVersions(Integer metaId) {
		return findByNamedQueryAndNamedParam("DocumentVersion.getByMetaId",
				"metaId", metaId);
	}

    
    // TODO: refactor - hardcoded - returns v no 0.
	@Transactional
	public DocumentVersion getActiveVersion(Integer metaId) {
		return (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getActiveVersion")
		    .setParameter("metaId", metaId)
		    .uniqueResult();
	}


	@Transactional
	public DocumentVersion getVersion(Integer metaId, Integer no) {
		return (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getByMetaIdAndNo")
		    .setParameter("metaId", metaId)
            .setParameter("no", no)
		    .uniqueResult();
	}
}
