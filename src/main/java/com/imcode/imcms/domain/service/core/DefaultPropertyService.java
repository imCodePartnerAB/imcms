package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.service.PropertyService;
import com.imcode.imcms.mapping.jpa.doc.Property;
import com.imcode.imcms.mapping.jpa.doc.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class DefaultPropertyService implements PropertyService {

    private final PropertyRepository propertyRepository;

    DefaultPropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Property findByDocIdAndName(int docId, String name) {
        return propertyRepository.findByDocIdAndName(docId, name);
    }

}
