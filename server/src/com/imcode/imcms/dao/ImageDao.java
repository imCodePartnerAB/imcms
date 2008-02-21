package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;

import java.util.List;

public interface ImageDao {

	List<ImageDomainObject> getAllImages(int metaId, int imageId);
	
	ImageDomainObject getDefaultImage(int metaId, int imageId);
}
