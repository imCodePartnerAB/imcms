package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;

import java.util.List;
import java.util.Map;

import com.imcode.imcms.api.I18nLanguage;

public interface ImageDao {
	
	/**
	 * Returns all document's images for given language. 
	 */
	List<ImageDomainObject> getImages(int metaId, int languageId);
	
	/**
	 * Returns document's images for every language.
	 */
	List<ImageDomainObject> getImages(List<I18nLanguage> languages, 
			int metaId, int imageId, boolean createImageIfNotExists);		
		
	/**
	 * Saves text document's images. 
	 */	
	void saveImagesMap(int documentId, Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap);	
}
