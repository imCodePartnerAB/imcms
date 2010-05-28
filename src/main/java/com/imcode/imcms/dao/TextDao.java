package com.imcode.imcms.dao;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.TextHistory;
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
	public int deleteTexts(Integer docId, Integer docVersionNo, Integer languageId) {
		return getSession().getNamedQuery("Text.deleteTexts")
			.setParameter("docId", docId)
			.setParameter("docVersionNo", docVersionNo)
            .setParameter("languageId", languageId)
			.executeUpdate();
	}

			
	/**
	 * Saves text history.
	 */
	@Transactional
	public void saveTextHistory(TextHistory textHistory) {
        save(textHistory);
	}
    

    /**
     * @param docId
     * @param docVersionNo
     * 
     * @return all texts in a doc.
     */
	@Transactional
	public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo) {
		return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNo",
				new String[] {"docId", "docVersionNo"}, 
				new Object[] {docId, docVersionNo}
		);
	}	
	

	/**
	 * Returns text fields for the same doc, version and language.
	 */
	@Transactional
	public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo, Integer languageId) {
		return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNoAndLanguageId",
				new String[] {"docId", "docVersionNo", "languageId"},
				new Object[] {docId, docVersionNo, languageId}
		);
	}
}