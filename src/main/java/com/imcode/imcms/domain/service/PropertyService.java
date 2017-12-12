package com.imcode.imcms.domain.service;

import com.imcode.imcms.mapping.jpa.doc.Property;

public interface PropertyService {

    Property getByDocIdAndName(int docId, String name);

    Integer getDocIdByAlias(String alias);

}
