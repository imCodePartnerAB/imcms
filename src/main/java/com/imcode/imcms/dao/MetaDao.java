package com.imcode.imcms.dao;


import java.util.Collection;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

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
	 * TODO: Implement
	 * @return Meta
	 */
	@Transactional
	public synchronized Meta getMeta(int documentId, int documentVersion) 
	throws OperationNotSupportedException {
		return null;
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
		return getMeta(documentId, Meta.DocumentVersionStatus.PUBLISHED); 
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
		return getMeta(documentId, Meta.DocumentVersionStatus.WORKING); 
	} 

	
	
	/**
	 * Returns meta for given document id and and version status. 
	 * 
	 * Checks and adds if necessary missing i18n-ed parts to meta. 
	 * 
	 * @return Meta
	 */
	@Transactional
	public Meta getMeta(Integer documentId, 
			Meta.DocumentVersionStatus documentVersionStatus) {
		Query query = getSession().createQuery("select m from Meta m where m.documentId = :documentId and m.documentVersionStatus = :documentVersionStatus")
			.setParameter("documentId", documentId)
			.setParameter("documentVersionStatus", documentVersionStatus);
		
		Meta meta = (Meta)query.uniqueResult();
		
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
	
	
	@Transactional
	public synchronized void insertFirstVersionMeta(Meta meta) {
		Query query = getSession().createQuery("select max(m.documentId) from Meta m");
		Integer documentId = (Integer)query.uniqueResult();
		
		if (documentId == null) {
			documentId = 1001;
		}
		
		meta.setDocumentId(documentId);
		meta.setDocumentVersion(1);
		
		save(meta);
	}
	
	
	@Transactional
	public synchronized void insertNextVersionMeta(Long metaId, Meta meta) {
		Query query = getSession().createQuery("SELECT max(m.documentVersion) + 1 FROM Meta m WHERE m.id = ?")
			.setLong(0, metaId);
		
		Integer version = (Integer)query.uniqueResult();
		
		meta.setDocumentVersion(version);
		
		save(meta);
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
}
