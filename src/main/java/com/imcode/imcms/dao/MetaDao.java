package com.imcode.imcms.dao;

import com.imcode.imcms.api.Meta;
import com.imcode.imcms.api.orm.OrmDocument;

public interface MetaDao {

    Meta getMeta(Integer metaId);
	
	void updateMeta(Meta meta);
	
    OrmDocument getDocument(Integer metaId);
	
	void updateDocument(OrmDocument ormDocument);	
}
