package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;
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
	
	@Transactional
	public int deleteTexts(Integer docId, Integer docVersionNo) {
		return getSession().getNamedQuery("Text.deleteTexts")
			.setParameter("docId", docId)
			.setParameter("docVersionNo", docVersionNo)
			.executeUpdate();
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
		String sql = "INSERT INTO imcms_text_doc_texts_history (doc_id, doc_version_no, no, text, type, modified_datetime, user_id, language_id,loop_no, loop_content_index) VALUES " +
		"(:docId,:docVersionNo,:no,:text,:type,:modifiedDt,:userId,:languageId,:loopNo,:loopContentIndex)";
		
		getSession().createSQLQuery(sql)
			.setParameter("docId", documentId)
			.setParameter("docVersionNo", text.getDocVersionNo())
			.setParameter("no", text.getNo())
			.setParameter("type", text.getType())
			.setParameter("text", text.getText())
			.setParameter("modifiedDt", new Date())
			.setParameter("userId", user.getId())
			.setParameter("languageId", text.getLanguage().getId())
            .setParameter("loopNo", text.getLoopNo())
            .setParameter("loopContentIndex", text.getContentIndex()).executeUpdate();
	}

	/**
	 * Returns all texts for given document id, text no, text language and versions.
	 * TODO: Reafactor out HQL call  
	 */
	@Transactional
	public List<TextDomainObject> getTexts(Integer docId, Integer no, I18nLanguage language, Collection<DocumentVersion> versions) {
		String hql = String.format(
			"SELECT t FROM Text t WHERE t.docId = ? AND t.no = ? AND t.language.id = ? AND t.docVersionNo IN (%s)",
			createVersionString(versions));
		
		return find(hql, new Object[] {docId, no, language.getId()});
	}
	
	@Transactional
	public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo) {
		return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNo",
				new String[] {"docId", "docVersionNo"}, 
				new Object[] {docId, docVersionNo}
		);
	}

	
	/**
	 * Returns text fields for the same document for all versions.
	 */
	@Transactional
	public List<TextDomainObject> getTextsForAllVersions(Integer documentId, I18nLanguage language) {
		String query = "SELECT t FROM Text t WHERE t.docId = ? AND t.language = ?";
		
		return find(query, new Object [] {documentId, language});
	}
	
	/**
	 * Returns text fields for the same document in version range.
	 */
	@Transactional
	public List<TextDomainObject> getTextsForVersionsInRange(Integer docId,
			I18nLanguage language, Integer versionFrom, Integer versionTo) {
		
		String query = "SELECT t FROM Text t WHERE t.docId=? AND t.language=?" +
				" AND t.docVersionNo BETWEEN ? AND ?";
		
		return find(query, new Object[] {docId, language, versionFrom, versionTo});
	}
	

	/**
	 * Returns text fields for the same document in specific versions.
	 */
	@Transactional
	public List<TextDomainObject> getTextsForVersions(Integer docId,
			I18nLanguage language, Collection<Integer> versions) {
		
		String versionsString = StringUtils.join(versions, ",");
		
		String query = String.format(
			"SELECT t FROM Text t WHERE t.docId = ? AND t.language=? AND t.docVersionNo IN (%s)",
			versionsString);
		
		return find(query, new Object[] {docId, language} );
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
			
			versionString.append(version.getNo().toString());
		}
				
		return versionString.toString();
	}


	/**
	 * Returns text fields for the same document in version range.
	 */
	@Transactional
	public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo, Integer languageId) {
		return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNoAndLanguageId",
				new String[] {"docId", "docVersionNo", "languageId"},
				new Object[] {docId, docVersionNo, languageId}
		);
	}
}