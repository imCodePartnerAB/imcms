package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.I18nLanguage;

public class TextDao extends HibernateTemplate {

	/**
	 * Inserts or updates text. 
	 */
	@Transactional
	public TextDomainObject saveText(TextDomainObject text) {
		saveOrUpdate(text);

		return text;
		
	}
	
	/**
	 * Updates texts. 
	 */
	@Transactional
	public void updateTexts(Collection<TextDomainObject> texts) {
		for (TextDomainObject text: texts) {
			update(text);
		}
	}
	

	
	
	/**
	 * Saves text history.
	 * TODO: Refactor out SQL call. 
	 */
	@Transactional
	public void saveTextHistory(Integer documentId, TextDomainObject text, UserDomainObject user) {
		String sql = "INSERT INTO texts_history (meta_id, meta_version, name, text, type, modified_datetime, user_id, language_id) VALUES " +
		"(:metaId,:metaVersion,:index,:text,:type,:modifiedDt,:userId,:languageId)";
		
		getSession().createSQLQuery(sql)
			.setParameter("metaId", documentId)
			.setParameter("metaVersion", text.getMetaVersion())
			.setParameter("index", text.getIndex())
			.setParameter("type", text.getType())
			.setParameter("text", text.getText())
			.setParameter("modifiedDt", new Date())
			.setParameter("userId", user.getId())
			.setParameter("languageId", text.getLanguage().getId()).executeUpdate();
	}

	/**
	 * Returns all texts for given document id, text index, text language and versions.
	 * TODO: Reafactor out HQL call  
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<TextDomainObject> getTexts(Integer documentId, Integer index, I18nLanguage language, Collection<DocumentVersion> versions) {
		String hql = String.format(
			"SELECT t FROM Text t WHERE t.metaId = ? AND t.index = ? AND t.language.id = ? AND t.metaVersion IN (%s)",
			createVersionString(versions));
		
		return find(hql, new Object[] {documentId, index, language.getId()});
	}
	
	@Transactional
	public Collection<TextDomainObject> getTexts(Integer documentId, Integer documentVersion) {
		return findByNamedQueryAndNamedParam("Text.getByDocumentIdAndDocumentVersion", 
				new String[] {"documentId", "documentVersion"}, 
				new Object[] {documentId, documentVersion}	
		);
	}

	
	/**
	 * Returns text fields for the same document for all versions.
	 */
	public List<TextDomainObject> getTextsForAllVersions(Integer documentId, I18nLanguage language) {
		String query = "SELECT t FROM Text t WHERE t.metaId = ? AND t.language = ?";
		
		return find(query, new Object [] {documentId, language});
	}
	
	/**
	 * eturns text fields for the same document in version range.
	 */
	public List<TextDomainObject> getTextsForVersionsInRange(Integer documentId, 
			I18nLanguage language, Integer versionFrom, Integer versionTo) {
		
		String query = "SELECT t FROM Text t WHERE t.metaId=? AND t.language=?" +
				" AND t.metaVersion BETWEEN ? AND ?";
		
		return find(query, new Object[] {documentId, language, versionFrom, versionTo});
	}
	

	/**
	 * Returns text fields for the same document in specific versions.
	 */
	public List<TextDomainObject> getTextsForVersions(Integer documentId, 
			I18nLanguage language, Collection<Integer> versions) {
		
		String versionsString = StringUtils.join(versions, ",");
		
		String query = String.format(
			"SELECT t FROM Text t WHERE t.metaId = ? AND t.language=? AND t.metaVersion IN (%s)",
			versionsString);
		
		return find(query, new Object[] {documentId, language} );
	}	
	
	/**
	 * Helper method.
	 * TODO: replace
	 */
	private String createVersionString(Collection<DocumentVersion> versions) {
		StringBuilder versionString = new StringBuilder();
		int i = 0;
		for (DocumentVersion version: versions) {
			if (i++ != 0) versionString.append(",");
			
			versionString.append(version.getNumber().toString());
		}
				
		return versionString.toString();
	}
}