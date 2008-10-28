package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;

import java.util.List;
import java.util.Map;

import com.imcode.imcms.api.I18nLanguage;

public interface ImageDao {
	
	/**
	 * Returns all document's images for given language. 
	 */
	List<ImageDomainObject> getDocumentImagesByLanguage(int metaId, int languageId);
	
	/**
	 * Returns document's images for every language.
	 */
	List<ImageDomainObject> getDocumentImagesByIndex(int metaId, int imageId, boolean createImageIfNotExists);
	
	/**
	 * Returns document's images for every language.
	 */
	List<ImageDomainObject> saveDocumentImages(int metaId, List<ImageDomainObject> images);		
	
		
	/**
	 * Saves text document's images. 
	 */	
	void saveImagesMap(int metaId, Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap);	
}
