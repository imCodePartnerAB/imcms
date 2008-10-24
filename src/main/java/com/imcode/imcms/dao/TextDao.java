package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TextDomainObject;

import java.util.List;

public interface TextDao {

	/**
	 * Returns all document's texts for given language. 
	 */
	List<TextDomainObject> getTexts(int metaId, int languageId);
	
	/**
	 * Returns text;
	 */
	TextDomainObject getText(int metaId, int index, int languageId);
	
	// new methods to be implemented:
	
	void saveText(TextDomainObject text);
	//void saveText(TextDomainObject text);
	//void deleteText () ???
	
	//List<TextDomainObject> getAllTextsWithIndex(int metaId, int index);
	//List<TextDomainObject> getAllTextsWithLanguage(int metaId, int languageId);
}
