package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;

import java.util.List;
import java.util.Map;

import com.imcode.imcms.api.I18nLanguage;

public interface ImageDao {
	
	//Map<I18nLanguage, Map<Integer, ImageDomainObject>> getImagesMap(int metaId);
	
	void saveAllImages(Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap);

	List<ImageDomainObject> getAllImages(int metaId, int imageId);
	
	Map<I18nLanguage, ImageDomainObject> getI18nImageMap(int metaId, int imageId);
	
	ImageDomainObject getDefaultImage(int metaId, int imageId);
}
