package com.imcode.imcms.dao;

import java.util.List;

import com.imcode.imcms.api.Include;

public interface IncludeDao {

	List<Include> getDocumentIncludes(int metaId);

	Include saveInclude(Include include);
	
	Include getDocumentInclude(int metaId, int includeIndex);
	
	int deleteDocumentIncludes(int metaId);
	
	/*
	void deleteInclude(int metaId, int includeIndex);
	*/
}
