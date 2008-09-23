package com.imcode.imcms.dao;

import com.imcode.imcms.api.Meta;

public interface MetaDao {

    Meta getMeta(Integer metaId);
	
	void updateMeta(Meta meta);
}
