package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TextDomainObject;

import java.util.List;

public interface TextDao {

	/**
	 * Returns all document's texts for given language. 
	 */
	List<TextDomainObject> getTexts(int metaId, int languageId);
	
	/**
	 * Returns text or null if text does not exists.
	 */
	TextDomainObject getText(int metaId, int index, int languageId);

	/**
	 * Inserts or updates existing text.
	 *   
	 * @return reference to saved text.
	 */
	TextDomainObject insertOrUpdateText(TextDomainObject text);
	
	/**
	 * Saves new text.
	 *   
	 * @return reference to saved text.
	 */
	TextDomainObject insertText(TextDomainObject text);
	
	
	//void deleteText () 
	
	//List<TextDomainObject> getAllTextsWithIndex(int metaId, int index);
	//List<TextDomainObject> getAllTextsWithLanguage(int metaId, int languageId);
}
