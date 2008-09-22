package com.imcode.imcms.dao;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nLanguage;

public class LanguageDaoImpl extends HibernateTemplate implements LanguageDao {

	@Transactional
	public void setDefaultLanguage(I18nLanguage language) {
		bulkUpdate("update I18nLanguage l set l.default = false");
		bulkUpdate("update I18nLanguage l set l.default = true where l.code = ?", 
				language.getCode());
	}

	@Transactional
	public synchronized I18nLanguage getDefaultLanguage() {
		return (I18nLanguage) getSession().getNamedQuery("I18nLanguage.getDefaultLanguage").uniqueResult();
	}
	
	@Transactional
	public synchronized List<I18nLanguage> getAllLanguages() {
		return (List<I18nLanguage>) loadAll(I18nLanguage.class);
	}
	
	@Transactional
	public synchronized I18nLanguage getByCode(String code) {
		return (I18nLanguage) getSession()
			.getNamedQuery("I18nLanguage.getByCode")
			.setParameter("code", code)
			.uniqueResult();
	}
}
