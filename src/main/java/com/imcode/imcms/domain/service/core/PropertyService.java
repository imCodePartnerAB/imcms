package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.mapping.jpa.doc.Property;
import com.imcode.imcms.mapping.jpa.doc.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property findByDocIdAndName(int docId, String name) {
        return propertyRepository.findByDocIdAndName(docId, name);
    }

}
