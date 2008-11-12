package com.imcode.imcms.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.Meta;

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
}