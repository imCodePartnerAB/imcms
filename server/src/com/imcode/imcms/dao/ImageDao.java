package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;

import java.util.List;
import java.util.Map;

import com.imcode.imcms.api.I18nLanguage;

public interface ImageDao {
	
	/**
	 * Returns document's default image or null if image does not exists.
	 * 
	 * @param metaId document id
	 * @param imageId image id
	 * 
	 * @returns document's default image or null if image does not exists.
	 */
	ImageDomainObject getDefaultImage(int metaId, int imageId);
	
	/**
	 * Returns all document's images for given language. 
	 */
	List<ImageDomainObject> getImages(int metaId, int languageId);
	
	/**
	 * Returns document's images for every language.
	 */
	List<ImageDomainObject> getImages(int metaId, int imageId, 
			boolean createImageIfNotExists);		
	
	/**
	 * Returns all document images.
	 * Currently not in use.
	 */
	Map<I18nLanguage, Map<Integer, ImageDomainObject>> getImagesMap(int doucmentId);
	
	/**
	 * Saves text document's images. 
	 */	
	void saveImagesMap(int documentId, Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap);	
}
