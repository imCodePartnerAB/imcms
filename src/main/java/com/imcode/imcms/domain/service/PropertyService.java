package com.imcode.imcms.domain.service;

import com.imcode.imcms.mapping.jpa.doc.Property;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.12.17.
 */
public interface PropertyService {
    Property findByDocIdAndName(int docId, String name);
}
