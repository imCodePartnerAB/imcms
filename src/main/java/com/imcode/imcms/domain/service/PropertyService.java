package com.imcode.imcms.domain.service;

import com.imcode.imcms.mapping.jpa.doc.Property;

import java.util.List;

public interface PropertyService { //todo need cover tests and repo too?

    Property getByDocIdAndName(int docId, String name);

    /**
     * @return doc's id or null if doc does not exists or alias is not set
     */
    Integer getDocIdByAlias(String alias);

    List<String> findAllAliases();

    List<Property> findByDocId(int docId);

    Boolean existsByAlias(String alias);

}
