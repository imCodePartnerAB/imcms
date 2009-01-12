package com.imcode.imcms.dao;


import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
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

public class MetaDao extends HibernateTemplate {

	/**
	 * @return Meta with given primary key (meta id).
	 */
	@Transactional
	public synchronized Meta getMeta(Long metaId) {
		Meta meta = (Meta)get(Meta.class, metaId);
		
		return addMissingI18nMetas(meta);
	}
	
	
	/**
	 * Returns meta for given document id and and version. 
	 * 
	 * @return Meta
	 */
	@Transactional
	public Meta getMeta(Integer documentId, Integer version) {
		Query query = getSession().createQuery("select m from Meta m where m.documentId = :documentId and m.documentVersion = :documentVersion")
			.setParameter("documentId", documentId)
			.setParameter("documentVersion", version);
		
		Meta meta = (Meta)query.uniqueResult();
		
		return addMissingI18nMetas(meta);
	}	

	
	/**
	 * Returns published document Meta for given document id. 
	 * 
	 * @param documentId document id.
	 * 
	 * @return published document meta.
	 */
	@Transactional
	public synchronized Meta getPublishedMeta(Integer documentId) {
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
	public synchronized Meta getWorkingMeta(Integer documentId) {
		return getMeta(documentId, DocumentVersionTag.WORKING); 
	} 

	
	
	/**
	 * Returns meta for given document id and and version tag. 
	 * 
	 * Checks and adds if necessary missing i18n-ed parts to meta. 
	 * 
	 * @return Meta
	 */
	@Transactional
	public Meta getMeta(Integer documentId, DocumentVersionTag documentVersionTag) {
		Query query = getSession().createQuery("select m from Meta m where m.documentId = :documentId and m.documentVersionTag = :documentVersionTag")
			.setParameter("documentId", documentId)
			.setParameter("documentVersionTag", documentVersionTag);
		
		Meta meta = (Meta)query.uniqueResult();
		
		return addMissingI18nMetas(meta);
	}
	
	/** 
	 * Checks and adds if necessary missing i18n-ed parts to meta.
	 */ 
	private Meta addMissingI18nMetas(Meta meta) {
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
		
		return meta;		
	}
	
	@Transactional
	public synchronized void updateMeta(Meta meta) {
		update(meta); 
	}
	
	
	/**
	 * Returns next document id.
	 * 
	 * Counting begins from 1001.
	 * 
	 * @return next document id.
	 */
	private Integer getNextDocumentId() {
		Integer maxId = (Integer)getSession().getNamedQuery("Meta.getMaxDocumentId")
			.uniqueResult();
		
		return maxId == null ? 1001 : maxId + 1;
	}
	
	/**
	 * Returns next document version for a document.
	 * 
	 * @return next document version for a document.
	 */
	private Integer getNextDocumentVersion(Long existingMetaId) {
		return (Integer)getSession().getNamedQuery("Meta.getNextDocumentVersion")
			.setLong(0, existingMetaId)
			.uniqueResult();
	} 	
	
	
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
	
	
	@Transactional
	public void saveIncludes(Long metaId, Collection<Include> includes) {
		bulkUpdate("delete from Include i where i.metaId = ?", metaId);
		
		//flush();
		//clear();
		for (Include include: includes) {
			saveOrUpdate(include);
		}
	}

	@Transactional
	public void saveTemplateNames(Long metaId, TemplateNames templateNames) {
		// delete first?
				
		saveOrUpdate(templateNames);
	}

	@Transactional
	public Collection<Include> getIncludes(Long metaId) {
		return (Collection<Include>) find("select i from Include i where i.metaId = ?", metaId);
	}

	@Transactional
	public TemplateNames getTemplateNames(Long metaId) {
		return (TemplateNames)getSession().createQuery("select n from TemplateNames n where n.metaId = ?")
			.setParameter(0, metaId)
			.uniqueResult();
	}

	@Transactional
	public Collection<FileReference> getFileReferences(Long metaId) {
		return find("select f from FileReference f where f.metaId = ? ORDER BY f.defaultFileId DESC, f.fileId", metaId);
	}

	@Transactional
	public FileReference saveFileReference(FileReference fileRef) {
		saveOrUpdate(fileRef);
		
		return fileRef;
	}

	@Transactional
	public int deleteFileReferences(Long metaId) {
		return bulkUpdate("delete from FileReference f where f.metaId = ?", metaId);
	}

	@Transactional
	public HtmlReference getHtmlReference(Long metaId) {
		return (HtmlReference)getSession().createQuery("select h from HtmlReference h where h.metaId = ?")
		.setParameter(0, metaId)
		.uniqueResult();
	}	
	
	@Transactional
	public HtmlReference saveHtmlReference(HtmlReference reference) {
		saveOrUpdate(reference);
		
		return reference;
	}
	
	@Transactional
	public UrlReference getUrlReference(Long metaId) {
		return (UrlReference)getSession().createQuery("select u from UrlReference u where u.metaId = ?")
		.setParameter(0, metaId)
		.uniqueResult();
	}

	@Transactional
	public UrlReference saveUrlReference(UrlReference reference) {
		saveOrUpdate(reference);
		
		return reference;
	}	
	
	@Transactional
	public synchronized void publishDocument(Integer documentId) {
		Query hql = getSession().createQuery("UPDATE Meta m SET m.documentVersionTag = :newVersionTag WHERE m.documentId = :documentId AND m.documentVersionTag = :oldVersionTag");
		
		hql.setParameter("documentId", documentId);
		hql.setParameter("newVersionTag", DocumentVersionTag.ARCHIVED);
		hql.setParameter("oldVersionTag", DocumentVersionTag.PUBLISHED);		
		hql.executeUpdate();
		
		hql.setParameter("newVersionTag", DocumentVersionTag.PUBLISHED);
		hql.setParameter("oldVersionTag", DocumentVersionTag.WORKING);
		hql.executeUpdate();
		
		// TODO: Update menu items
		// TODO: Update includes
	}
	
	
	@Transactional
	//@SuppressWarnings()
	public List<DocumentVersion> getDocumentVersions(Integer documentId) {
		return findByNamedQueryAndNamedParam("Meta.getDocumentVersions", 
				"documentId", documentId);
	}	
}