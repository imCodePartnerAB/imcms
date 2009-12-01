package com.imcode.imcms.dao;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.orm.DefaultDocumentVersion;

import java.util.List;
import java.util.Date;

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
	public synchronized DocumentVersion createVersion(Integer docId, Integer userId) {
		DocumentVersion latestVersion = (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getLatestVersion")
			.setParameter("docId", docId)
			.uniqueResult();

        Integer versionNumber = latestVersion == null
                ? 0
                : latestVersion.getNo() + 1;

        DocumentVersion version =  new DocumentVersion(docId, versionNumber, userId, new Date());

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
	public DefaultDocumentVersion getDefaultVersionORM(Integer docId) {
		return (DefaultDocumentVersion)getSession()
            .createQuery("SELECT v FROM DefaultDocumentVersion v WHERE v.docId = :docId")
		    .setParameter("docId", docId)
		    .uniqueResult();
	}

	@Transactional
	public void saveDefaultVersionORM(DefaultDocumentVersion version) {
        saveOrUpdate(version);
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
