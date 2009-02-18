package com.imcode.imcms.dao;


import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionTag;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.mapping.orm.FileReference;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.Include;
import com.imcode.imcms.mapping.orm.TemplateNames;
import com.imcode.imcms.mapping.orm.UrlReference;

//TODO: convert in-line queries to named queries
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
	 * Creates next working version of a document.
	 * 
	 * If document has a working version tag it as cancelled.  
	 * 
	 * @return next document version.
	 */
	@Transactional
	public synchronized DocumentVersion createNextWorkingVersion(Integer documentId) {
		DocumentVersion nextVersion;
		DocumentVersion latestVersion = (DocumentVersion)getSession().getNamedQuery("DocumentVersion.getLastVersion")
			.setParameter("documentId", documentId)
			.uniqueResult();
		
		if (latestVersion == null) {
			nextVersion = new DocumentVersion(documentId, 1, DocumentVersionTag.WORKING);			
		} else {
			nextVersion = new DocumentVersion(documentId, 
					latestVersion.getVersion() + 1, DocumentVersionTag.WORKING);
			
			if (latestVersion.getVersionTag() == DocumentVersionTag.WORKING) {
				latestVersion.setVersionTag(DocumentVersionTag.CANCELLED);
				update(latestVersion);
			} 
		}
		
		save(nextVersion);
		
		return nextVersion;
	}	
	
	
	/**
	 * Returns meta with version data. 
	 * 
	 * @return Meta
	 */
	@Transactional
	public Meta getMeta(Integer id, Integer version) {
		Meta meta = getMeta(id, version);
		
		if (meta != null) {
			Query query = getSession().createQuery("SELECT v FROM DocumentVersion v WHERE v.documentId = :documentId AND v.version = :version")
				.setParameter("documentId", id)
				.setParameter("version", version);
			
			DocumentVersion documentVersion = (DocumentVersion)query.uniqueResult();
			
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
	public Meta getMeta(Integer id, DocumentVersionTag versionTag) {
		Query query = getSession().createQuery("SELECT v FROM DocumentVersion v WHERE v.documentId = :documentId AND v.versionTag = :versionTag")
			.setParameter("documentId", id)
			.setParameter("versionTag", versionTag);
	
		DocumentVersion version = (DocumentVersion)query.uniqueResult();
		
		if (version == null) {
			return null;
		} else {
			Meta meta = getMeta(id);
			meta.setVersion(version);			
			return initI18nMetas(meta);
		}
	}	
	
	/**
	 * Returns published document Meta for given document id. 
	 * 
	 * @param documentId document id.
	 * 
	 * @return published document meta.
	 */
	@Transactional
	public Meta getPublishedMeta(Integer documentId) {
		return getMeta(documentId, DocumentVersionTag.PUBLISHED); 
	}
	
	
	/**
	 * Returns working document Meta for given document id. 
	 * 
	 * @param documentId document id.
	 * 
	 * @return published document meta.
	 */
	@Transactional
	public Meta getWorkingMeta(Integer documentId) {
		return getMeta(documentId, DocumentVersionTag.WORKING); 
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
		saveOrUpdate(meta); 
	}
		
	
	/*
	@Transactional
	public synchronized void insertFirstVersionMeta(Meta meta) {
		Integer documentId = getNextDocumentId();
		
		meta.setDocumentId(documentId);
		meta.setDocumentVersion(1);
		
		save(meta);
	}
	
	
	@Transactional
	public synchronized void insertNextVersionMeta(Long existingMetaId, Meta newMeta) {
		Integer version = getNextDocumentVersion(existingMetaId);
		
		newMeta.setDocumentVersion(version);
		
		save(newMeta);
	}
	*/
	
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
	public synchronized void publishWorkingDocument(Integer documentId) {
		Query query = getSession().getNamedQuery("DocumentVersion.getByDocumentIdAndVersionTag")
			.setParameter("documentId", documentId)
			.setParameter("versionTag", DocumentVersionTag.PUBLISHED);
		
		DocumentVersion publishedVersion = (DocumentVersion)query.uniqueResult();
		
		if (publishedVersion != null) {
			publishedVersion.setVersionTag(DocumentVersionTag.ARCHIVED);
			save(publishedVersion);
		}
		
		query.setParameter("versionTag", DocumentVersionTag.WORKING);
		DocumentVersion workingVersion = (DocumentVersion)query.uniqueResult();
		
		if (workingVersion != null) {
			workingVersion.setVersionTag(DocumentVersionTag.PUBLISHED);
			save(workingVersion);
		}
	}
	
	
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<DocumentVersion> getDocumentVersions(Integer documentId) {
		return findByNamedQueryAndNamedParam("DocumentVersion.getByDocumentId", 
				"documentId", documentId);
	}	
}