package com.imcode.imcms.dao;


import java.util.Collection;
import java.util.List;

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
	 * Returns meta for given meta id.
	 * 
	 * Checks and adds if necessary missing i18n-ed parts to meta. 
	 * 
	 * @return Meta
	 */
	@Transactional
	public synchronized Meta getMeta(Integer metaId) {
		Query query = getSession().createQuery("select m from Meta m where m.metaId = :metaId")
			.setParameter("metaId", metaId);
		
		Meta meta = (Meta)query.uniqueResult();
		
		List<I18nLanguage> languages = (List<I18nLanguage>)
				findByNamedQueryAndNamedParam("I18nLanguage.missingMetaLanguages", "metaId", metaId);
				
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
		saveOrUpdate(meta); 
	}


	@Transactional
	public void saveIncludes(Integer metaId, Collection<Include> includes) {
		bulkUpdate("delete from Include i where i.metaId = ?", metaId);
		
		//flush();
		//clear();
		for (Include include: includes) {
			saveOrUpdate(include);
		}
	}

	@Transactional
	public void saveTemplateNames(Integer metaId, TemplateNames templateNames) {
		// delete first?
				
		saveOrUpdate(templateNames);
	}

	@Transactional
	public Collection<Include> getIncludes(Integer metaId) {
		return (Collection<Include>) find("select i from Include i where i.metaId = ?", metaId);
	}

	@Transactional
	public TemplateNames getTemplateNames(Integer metaId) {
		return (TemplateNames)getSession().createQuery("select n from TemplateNames n where n.metaId = ?")
			.setParameter(0, metaId)
			.uniqueResult();
	}

	@Transactional
	public Collection<FileReference> getFileReferences(int metaId) {
		return find("select f from FileReference f where f.metaId = ? ORDER BY f.defaultFileId DESC, f.fileId", metaId);
	}

	@Transactional
	public FileReference saveFileReference(FileReference fileRef) {
		saveOrUpdate(fileRef);
		
		return fileRef;
	}

	@Transactional
	public int deleteFileReferences(int metaId) {
		return bulkUpdate("delete from FileReference f where f.metaId = ?", metaId);
	}

	@Transactional
	public HtmlReference getHtmlReference(int metaId) {
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
	public UrlReference getUrlReference(int metaId) {
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
