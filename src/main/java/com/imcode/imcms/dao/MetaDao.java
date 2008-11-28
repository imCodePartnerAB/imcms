package com.imcode.imcms.dao;

import imcode.server.document.textdocument.Include;
import imcode.server.document.textdocument.TemplateNames;

import java.util.Collection;

import com.imcode.imcms.api.Meta;
import com.imcode.imcms.api.orm.OrmDocument;

public interface MetaDao {

    Meta getMeta(Integer metaId);
	
	void updateMeta(Meta meta);
	
	void saveIncludes(Integer metaId, Collection<Include> includes);
	
	void saveTemplateNames(Integer metaId, TemplateNames templateNames);
	
	Collection<Include> getIncludes(Integer metaId);
	
	TemplateNames getTemplateNames(Integer metaId);	
}
