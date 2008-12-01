package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public class TextDao extends HibernateTemplate {

	@Transactional
	public synchronized List<TextDomainObject> getTexts(int metaId, int languageId) {
		List<TextDomainObject> texts = findByNamedQueryAndNamedParam(
				"Text.getByMetaIdAndLanguageId", 
					new String[] {"metaId", "languageId"}, 
					new Object[] {metaId, languageId});
			
		return texts;
	}
	
	@Transactional
	public synchronized TextDomainObject getText(int metaId, int index, int languageId) {
		Session session = getSession();
		
		TextDomainObject text = (TextDomainObject)session
			.getNamedQuery("Text.getByMetaIdAndIndexAndLanguageId")
			.setParameter("metaId", metaId)
			.setParameter("index", index)
			.setParameter("languageId", languageId)
			.uniqueResult();
		
		return text;
	}
	

	@Transactional
	public TextDomainObject saveText(TextDomainObject text) {
		saveOrUpdate(text);

		return text;
		
	}

	@Transactional
	public Collection<TextDomainObject> getTexts(int metaId) {
		return find("select t from I18nText t where t.metaId = ?", metaId);
	}
	
	@Transactional
	public void saveTextHistory(int metaId, TextDomainObject text, UserDomainObject user) {
		String sql = "INSERT INTO texts_history (meta_id, name, text, type, modified_datetime, user_id, language_id) VALUES " +
		"(:metaId,:index,:text,:type,:modifiedDt,:userId,:languageId)";
		
		getSession().createSQLQuery(sql)
			.setParameter("metaId", metaId)
			.setParameter("index", text.getIndex())
			.setParameter("type", text.getType())
			.setParameter("text", text.getText())
			.setParameter("modifiedDt", new Date())
			.setParameter("userId", user.getId())
			.setParameter("languageId", text.getLanguage().getId()).executeUpdate();
	}}
