package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TextDomainObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nLanguage;

public class TextDaoImpl extends HibernateTemplate implements TextDao {

	@Transactional
	public List<TextDomainObject> getTexts(int metaId, int languageId) {
		List<TextDomainObject> texts = findByNamedQueryAndNamedParam(
				"Text.getByMetaIdAndLanguageId", 
					new String[] {"metaId", "languageId"}, 
					new Object[] {metaId, languageId});
			
		return texts;
	}
	
	@Transactional
	public TextDomainObject getText(int metaId, int index, int languageId) {
		Session session = getSession();
		
		TextDomainObject text = (TextDomainObject)session
			.getNamedQuery("Text.getByMetaIdAndIndexAndLanguageId")
			.setParameter("metaId", metaId)
			.setParameter("index", index)
			.setParameter("languageId", languageId)
			.uniqueResult();
		
		return text;
	}
}
