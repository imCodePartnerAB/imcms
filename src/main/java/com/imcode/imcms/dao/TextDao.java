package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

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
            .setParameter("loopContentIndex", text.getContentNo()).executeUpdate();
	}

    /**
     * @param docId
     * @param docVersionNo
     * @return all texts in all languages.
     */
	@Transactional
	public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo) {
		return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNo",
				new String[] {"docId", "docVersionNo"}, 
				new Object[] {docId, docVersionNo}
		);
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