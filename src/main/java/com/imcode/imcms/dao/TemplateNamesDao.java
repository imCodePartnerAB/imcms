package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TemplateNames;

public interface TemplateNamesDao {

	TemplateNames getTemplateNames(int metaId);
	
	TemplateNames saveTemplateNames(TemplateNames templateNames);
	
	void deleteTemplateNames(TemplateNames templateNames);
}
