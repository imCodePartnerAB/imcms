package com.imcode.imcms.dao;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.imcode.imcms.api.I18nLanguage;

public class LanguageDaoImpl extends HibernateTemplate implements LanguageDao {

	public I18nLanguage getDefaultLanguage() {
		return (I18nLanguage) getSession().getNamedQuery("I18nLanguage.getDefaultLanguage").uniqueResult();
	}
	
	public List<I18nLanguage> getAllLanguages() {
		return (List<I18nLanguage>) loadAll(I18nLanguage.class);
	}
	
	public I18nLanguage getByCode(String code) {
		return (I18nLanguage) getSession()
			.getNamedQuery("I18nLanguage.getByCode")
			.setParameter("code", code)
			.uniqueResult();
	}
}
