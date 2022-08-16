package com.imcode.imcms.domain.service;

import com.imcode.imcms.mapping.jpa.doc.Property;

import java.util.List;

public interface PropertyService { //todo need cover tests and repo too?

    Property getByDocIdAndName(int docId, String name);

    List<Property> findByDocId(int docId);
}
