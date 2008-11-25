package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TemplateNames;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.api.orm.OrmDocument;
import com.imcode.imcms.api.orm.OrmInclude;
import com.imcode.imcms.api.orm.OrmTextDocument;

public class MetaDaoImpl extends HibernateTemplate implements MetaDao {

	/**
	 * Returns meta for given meta id.
	 * 
	 * Checks and adds if necessary missing i18n-ed parts to meta. 
	 * 
	 * @return Meta
	 */
	@Transactional
	public synchronized Meta getMeta(Integer metaId) {
		Query query = getSession().createQuery("select o from OrmDocument o where o.metaId = :metaId")
			.setParameter("metaId", metaId);
		
		OrmDocument ormDocument = (OrmDocument)query.uniqueResult();
		Meta meta = ormDocument.getMeta();
		
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
		OrmDocument ormDocument = meta.getOrmDocument();
		saveOrUpdate(ormDocument); 
		meta.setMetaId(ormDocument.getMetaId());
	}

	@Transactional
	public OrmDocument getDocument(Integer metaId) {		
		return null;
	}

	@Transactional
	public void updateDocument(OrmDocument ormDocument) {
	}

	@Transactional
	public void saveIncludes(Integer metaId, Collection<OrmInclude> includes) {
		bulkUpdate("delete from OrmInclude i where i.metaId = ?", metaId);
		
		//flush();
		//clear();
		for (OrmInclude include: includes) {
			saveOrUpdate(include);
		}
	}

	public void saveTemplateNames(Integer metaId, TemplateNames templateNames) {
		// delete first?
		
		saveOrUpdate(templateNames);
	}			
}