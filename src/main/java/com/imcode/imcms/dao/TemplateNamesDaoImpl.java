package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TemplateNames;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

public class TemplateNamesDaoImpl extends HibernateTemplate implements TemplateNamesDao {

	@Transactional
	public void deleteTemplateNames(TemplateNames templateNames) {
		delete(templateNames);
	}

	@Transactional
	public TemplateNames getTemplateNames(int metaId) {
		return (TemplateNames)get(TemplateNames.class, metaId);
	}

	@Transactional
	public TemplateNames saveTemplateNames(TemplateNames templateNames) {
		saveOrUpdate(templateNames);
		
		return templateNames;
	}
}