package com.imcode.imcms.dao;


import java.util.Collection;

import com.imcode.imcms.api.Meta;
import com.imcode.imcms.mapping.orm.FileReference;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.Include;
import com.imcode.imcms.mapping.orm.TemplateNames;
import com.imcode.imcms.mapping.orm.UrlReference;

public interface MetaDao {

    Meta getMeta(Integer metaId);
	
	void updateMeta(Meta meta);
	
	// Move 
	
	void saveIncludes(Integer metaId, Collection<Include> includes);
	
	void saveTemplateNames(Integer metaId, TemplateNames templateNames);
	
	Collection<Include> getIncludes(Integer metaId);
	
	TemplateNames getTemplateNames(Integer metaId);	
	
	Collection<FileReference> getFileReferences(int metaId);
	
	FileReference saveFileReference(FileReference fileRef);
	
	int deleteFileReferences(int metaId);
	
	HtmlReference getHtmlReference(int metaId);
	
	HtmlReference saveHtmlReference(HtmlReference reference);
	
	UrlReference getUrlReference(int metaId);
	
	UrlReference saveUrlReference(UrlReference reference);	
}
