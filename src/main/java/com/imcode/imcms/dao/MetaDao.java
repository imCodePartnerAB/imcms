package com.imcode.imcms.dao;

import imcode.server.document.textdocument.TemplateNames;

import java.util.Collection;

import com.imcode.imcms.api.Meta;
import com.imcode.imcms.api.orm.OrmDocument;
import com.imcode.imcms.api.orm.OrmInclude;

public interface MetaDao {

    Meta getMeta(Integer metaId);
	
	void updateMeta(Meta meta);
	
    OrmDocument getDocument(Integer metaId);
	
	void updateDocument(OrmDocument ormDocument);
	
	void saveIncludes(Integer metaId, Collection<OrmInclude> includes);
	
	void saveTemplateNames(Integer metaId, TemplateNames templateNames);
}
