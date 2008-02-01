package com.imcode.imcms.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nKeyword;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMetaPart;
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
	public Meta getMeta(Integer metaId) {
		Meta meta = (Meta)get(Meta.class, metaId);
		
		List<I18nLanguage> languages = (List<I18nLanguage>)
				findByNamedQueryAndNamedParam("I18nLanguage.missingMetaLanguages", "metaId", metaId);
				
		if (languages != null) {
			Collection<I18nMetaPart> parts = meta.getI18nParts();
			
			for (I18nLanguage language: languages) {
				I18nMetaPart part = new I18nMetaPart();
				
				part.setLanguage(language);
				part.setEnabled(false);
				
				parts.add(part);
			}
		}
		
		return meta;
	}
	
	@Transactional
	public void updateMeta(Meta meta) {
		saveOrUpdate(meta);
	}	
}
