package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import java.util.List;
import java.util.Map;

import com.imcode.imcms.api.I18nLanguage;

public interface ImageDao {
	
	/**
	 * Returns default language image.
	 * 
	 * @returns default language image or null if image is not exists.
	 */
	ImageDomainObject getDefaultImage(int metaId, int imageId);
	
	List<ImageDomainObject> getAllImages(int metaId, int languageId);
	
	/**
	 * Returns all document images.
	 */
	Map<I18nLanguage, Map<Integer, ImageDomainObject>> getImagesMap(int doucmentId);
	
	/**
	 * Saves text document's images. 
	 */	
	void saveImagesMap(int documentId, Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap);

	/**
	 * Returns images map. 
	 */
	Map<I18nLanguage, ImageDomainObject> getImagesMap(
			int metaId, int imageId, boolean createImageIfNotExists);	
}
