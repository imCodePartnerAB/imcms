package com.imcode.imcms.dao;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nLanguage;

public class LanguageDao extends HibernateTemplate {

	@Transactional
	public synchronized List<I18nLanguage> getAllLanguages() {
		return (List<I18nLanguage>) loadAll(I18nLanguage.class);
	}
	    
	@Transactional
	public synchronized I18nLanguage getById(Integer id) {
		return (I18nLanguage) getSession()
			.getNamedQuery("I18nLanguage.getById")
			.setParameter("id", id)
			.uniqueResult();
	}

	@Transactional
	public synchronized I18nLanguage getByCode(String code) {
		return (I18nLanguage) getSession()
			.getNamedQuery("I18nLanguage.getByCode")
			.setParameter("code", code)
			.uniqueResult();
	}
}
