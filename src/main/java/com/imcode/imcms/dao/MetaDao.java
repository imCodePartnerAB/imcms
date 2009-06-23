package com.imcode.imcms.dao;

import imcode.server.document.DocumentDomainObject;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.db.commands.DeleteWhereColumnsEqualDatabaseCommand;
import com.imcode.db.commands.SqlUpdateDatabaseCommand;
import com.imcode.imcms.api.DocumentProperty;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionSelector;
import com.imcode.imcms.api.DocumentVersionTag;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.orm.FileReference;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.Include;
import com.imcode.imcms.mapping.orm.TemplateNames;
import com.imcode.imcms.mapping.orm.UrlReference;

public class MetaDao extends HibernateTemplate {

	/**
	 * @return Meta.
	 */
	@Transactional
	private Meta getMeta(Integer documentId) {
		Meta meta = (Meta)get(Meta.class, documentId);
		
		return initI18nMetas(meta);
	}
	
	
	/**
	 * Creates and returns a new working version of a document.
	 * 
	 * Tags existing working version as postponed if it is already present.
	 * 
	 * @return next document version.
	 * 
	 * @see DocumentMapper.saveNewDocument
	 * @see DocumentMapper.publishWorkingDocument
	 * @see DocumentMapper.createWorkingDocumentFromExisting 
	 * 
	 * @return new working version of a document
	 */
	@Transactional
	public DocumentVersion createWorkingVersion(Integer documentId, Integer userId) {
		DocumentVersion workingVersion;
		
		DocumentVersion latestVersion = (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getLastVersion")
			.setParameter("documentId", documentId)
			.uniqueResult();
		
		if (latestVersion == null) {
			workingVersion = new DocumentVersion(documentId, 1, DocumentVersionTag.WORKING);			
		} else {
			if (latestVersion.getTag() == DocumentVersionTag.WORKING) {
				latestVersion.setTag(DocumentVersionTag.POSTPONED);
				update(latestVersion);
			}
			
			workingVersion = new DocumentVersion(documentId, 
					latestVersion.getNumber() + 1, 
					DocumentVersionTag.WORKING);			
		}
		
		workingVersion.setUserId(userId);
		workingVersion.setCreatedDt(new Date());
				
		save(workingVersion);
		
		return workingVersion;
	}	
	
	
	/**
	 * Returns meta with version data. 
	 * 
	 * @return Meta
	 */
	@Transactional
	public Meta getMeta(Integer id, Integer versionNumber) {
		Meta meta = getMeta(id);
		
		if (meta != null) {
			DocumentVersion documentVersion = (DocumentVersion)getSession().getNamedQuery("DocumentVersion.getByDocumentIdAndVersionNumber")
				.setParameter("documentId", id)
				.setParameter("versionNumber", versionNumber)
				.uniqueResult();			
			if (documentVersion != null) {
				meta.setVersion(documentVersion);
			} else {
				meta = null;
			}
		}
				
		return initI18nMetas(meta);
	}	

	/**
	 * Returns meta for given document id and and version tag. 
	 * 
	 * Checks and adds if necessary missing i18n-ed parts to meta. 
	 * 
	 * @return Meta
	 */
	@Transactional
	public Meta getMeta(Integer documentId, DocumentVersionSelector versionSelector) {
		switch (versionSelector.getType()) {
			case PUBLISHED:
				return getPublishedMeta(documentId);
			case WORKING:
				return getWorkingMeta(documentId);	
			default: // CUSTOM:
				return getMeta(documentId, versionSelector.getVersionNumber());
				
		}
	}	
	
	/**
	 * Returns published document Meta for given document id. 
	 * 
	 * @param documentId document id.
	 * 
	 * @return published document meta.
	 */
	// TODO: refactor
	@Transactional
	public Meta getPublishedMeta(Integer documentId) {
		Meta meta = getMeta(documentId);
		
		if (meta != null) {
			DocumentVersion publishedVersion = (DocumentVersion)getSession()
				.getNamedQuery("DocumentVersion.getPublishedVersion")
			    .setParameter("documentId", documentId)
			    .uniqueResult();
			
			if (publishedVersion == null) return null;
			
			meta.setVersion(publishedVersion);
		}
		
		return initI18nMetas((meta));
	}
	
	@Transactional
	public DocumentVersion getPublishedVersion(Integer documentId) {
		return (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getPublishedVersion")
		    .setParameter("documentId", documentId)
		    .uniqueResult();
	}	
	
	
	/**
	 * Returns working document Meta for given document id. 
	 * 
	 * @param documentId document id.
	 * 
	 * @return published document meta.
	 */
	// TODO: refactor
	@Transactional
	public Meta getWorkingMeta(Integer documentId) {
		Meta meta = getMeta(documentId);
		
		if (meta != null) {
			DocumentVersion workingVersion = (DocumentVersion)getSession()
				.getNamedQuery("DocumentVersion.getWorkingVersion")
			    .setParameter("documentId", documentId)
			    .uniqueResult();
			
			if (workingVersion == null) return null;
			
			meta.setVersion(workingVersion);
		}
		
		return initI18nMetas((meta));
	} 

	
	/** 
	 * Checks and adds if necessary missing i18n-ed parts to meta.
	 */ 
	private Meta initI18nMetas(Meta meta) {
		if (meta == null) {
			return null;
		}
		
		List<I18nLanguage> languages = (List<I18nLanguage>)
		findByNamedQueryAndNamedParam("I18nLanguage.missingMetaLanguages", "metaId", meta.getId());
		
		if (languages != null) {
			Collection<I18nMeta> parts = meta.getI18nMetas();
			
			for (I18nLanguage language: languages) {
				I18nMeta part = new I18nMeta();
				
				part.setLanguage(language);
				part.setEnabled(false);
				part.setHeadline("");
				part.setMenuImageURL("");
				part.setMenuText("");
				
				parts.add(part);
			}
		}
		
		meta.initI18nMetaMapping();
		
		return meta;		
	}
	
	@Transactional
	public void saveMeta(Meta meta) {
		boolean setFk = meta.getId() == null;
		
		saveOrUpdate(meta); 
		
		if (setFk) {
			//??? Cascading save does not insert foreign keys in certain cases ???
			//??? temp workaround until bug/feature? is found
			Integer fk = meta.getId();
			for (I18nMeta i18nMeta: meta.getI18nMetas()) {
				i18nMeta.setMetaId(fk);
			}
		}
	}
		
	
	@Transactional
	public void saveIncludes(Integer documentId, Collection<Include> includes) {
		bulkUpdate("delete from Include i where i.metaId = ?", documentId);
		
		//flush();
		//clear();
		for (Include include: includes) {
			saveOrUpdate(include);
		}
	}

	@Transactional
	public void saveTemplateNames(Integer documentId, TemplateNames templateNames) {
		// delete first?
				
		saveOrUpdate(templateNames);
	}

	@Transactional
	public Collection<Include> getIncludes(Integer documentId) {
		return (Collection<Include>) find("select i from Include i where i.metaId = ?", documentId);
	}

	@Transactional
	public TemplateNames getTemplateNames(Integer documentId) {
		return (TemplateNames)getSession().createQuery("select n from TemplateNames n where n.metaId = ?")
			.setParameter(0, documentId)
			.uniqueResult();
	}

	@Transactional
	public Collection<FileReference> getFileReferences(Integer documentId) {
		return find("select f from FileReference f where f.metaId = ? ORDER BY f.defaultFileId DESC, f.fileId", documentId);
	}

	@Transactional
	public FileReference saveFileReference(FileReference fileRef) {
		saveOrUpdate(fileRef);
		
		return fileRef;
	}

	@Transactional
	public int deleteFileReferences(Integer documentId) {
		return bulkUpdate("delete from FileReference f where f.metaId = ?", documentId);
	}

	@Transactional
	public HtmlReference getHtmlReference(Integer documentId) {
		return (HtmlReference)getSession().createQuery("select h from HtmlReference h where h.metaId = ?")
		.setParameter(0, documentId)
		.uniqueResult();
	}	
	
	@Transactional
	public HtmlReference saveHtmlReference(HtmlReference reference) {
		saveOrUpdate(reference);
		
		return reference;
	}
	
	@Transactional
	public UrlReference getUrlReference(Integer documentId) {
		return (UrlReference)getSession().createQuery("select u from UrlReference u where u.metaId = ?")
		.setParameter(0, documentId)
		.uniqueResult();
	}

	@Transactional
	public UrlReference saveUrlReference(UrlReference reference) {
		saveOrUpdate(reference);
		
		return reference;
	}	
	
	/**
	 * Publishes working version of a document.
	 * 
	 * Changes published version to archived and working version to published.
	 * 
	 * @param documentId document id to publish.
	 * //TODO?: @param version, and select by version, not by tag ???
	 * //TODO?: @param userId - user id ??? 
	 * //TODO?: alter modification date ???
	 */
	@Transactional
	public void publishWorkingVersion(Integer documentId) {
		DocumentVersion publishedVersion = (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getPublishedVersion")
			.setParameter("documentId", documentId)
			.uniqueResult();
		
		if (publishedVersion != null) {
			publishedVersion.setTag(DocumentVersionTag.ARCHIVED);
			save(publishedVersion);
		}
		
		DocumentVersion workingVersion = (DocumentVersion)getSession()
			.getNamedQuery("DocumentVersion.getWorkingVersion")
			.setParameter("documentId", documentId)
			.uniqueResult();
				
		if (workingVersion != null) {
			workingVersion.setTag(DocumentVersionTag.PUBLISHED);
			save(workingVersion);
		}
	}
	
	
	/**
	 * Returns all versions for the document.
	 * 
	 * @param documentId document id.
	 * @return available versions for the document.
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<DocumentVersion> getDocumentVersions(Integer documentId) {
		return findByNamedQueryAndNamedParam("DocumentVersion.getByDocumentId", 
				"documentId", documentId);
	}	
	
	@Transactional(propagation=Propagation.SUPPORTS)
	public Integer getDocumentIdByAlias(String alias) {
		return (Integer)getSession().getNamedQuery("DocumentProperty.getDocumentIdByAlias")
			.setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
			.setParameter("value", alias.toLowerCase())
			.uniqueResult();
	}
	
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<String> getAllAliases() {
		return findByNamedQueryAndNamedParam(
				"DocumentProperty.getAllAliases", "name", 
				DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
	}	
	
	@Transactional(propagation=Propagation.SUPPORTS)
	public DocumentProperty getAliasProperty(String alias) {
		return (DocumentProperty)getSession().getNamedQuery("DocumentProperty.getAliasProperty")
			.setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
			.setParameter("value", alias)
			.uniqueResult();
	}	
	
	@Transactional
	public void deleteDocument(final Integer metaId) {
		String[] sqls = {
			"DELETE FROM document_categories WHERE meta_id = ?", 
			// delete form keywords ???
			"DELETE FROM childs WHERE to_meta_id = ?", 
			"DELETE FROM childs WHERE menu_id IN (SELECT menu_id FROM menus WHERE meta_id = ?)",	
			"DELETE FROM menus WHERE meta_id = ?", 
			"DELETE FROM text_docs WHERE meta_id = ?", 
			"DELETE FROM texts WHERE meta_id = ?", 
			"DELETE FROM images WHERE meta_id = ?", 
			"DELETE FROM roles_rights WHERE meta_id = ?", 
			"DELETE FROM user_rights WHERE meta_id = ?", 
			"DELETE FROM url_docs WHERE meta_id = ?", 
			"DELETE FROM fileupload_docs WHERE meta_id = ?", 
			"DELETE FROM frameset_docs WHERE meta_id = ?", 
			"DELETE FROM new_doc_permission_sets_ex WHERE meta_id = ?", 
			"DELETE FROM new_doc_permission_sets WHERE meta_id = ?", 
			"DELETE FROM doc_permission_sets_ex WHERE meta_id = ?", 
			"DELETE FROM doc_permission_sets WHERE meta_id = ?", 
			"DELETE FROM includes WHERE meta_id = ?", 
			"DELETE FROM includes WHERE included_meta_id = ?", 
			"DELETE FROM texts_history WHERE meta_id = ?", 
			"DELETE FROM images_history WHERE meta_id = ?", 
			"DELETE FROM childs_history WHERE to_meta_id = ?", 
			"DELETE FROM childs_history WHERE menu_id IN (SELECT menu_id FROM menus_history WHERE meta_id = ?)",
			"DELETE FROM menus_history WHERE meta_id = ?", 
			"DELETE FROM document_properties WHERE meta_id = ?", 	
			"DELETE FROM i18n_meta WHERE meta_id = ?", 	
			"DELETE FROM meta WHERE meta_id = ?", 	
		};
		
		Session session = getSession();
		
		for (String sql: sqls) {
			int i = session.createSQLQuery(sql).setParameter(0, metaId).executeUpdate();
		}								
	}
}