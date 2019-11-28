package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.service.PropertyService;
import com.imcode.imcms.mapping.jpa.doc.Property;
import com.imcode.imcms.mapping.jpa.doc.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DefaultPropertyService implements PropertyService {

    private final PropertyRepository propertyRepository;

    DefaultPropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Property getByDocIdAndName(int docId, String name) {
        return propertyRepository.findByDocIdAndName(docId, name);
    }

    @Override
    public Integer getDocIdByAlias(String alias) {
        return propertyRepository.findDocIdByAlias(alias);
    }

    @Override
    public List<String> findAllAliases() {
        return propertyRepository.findAllAliases();
    }

    @Override
    public List<Property> findByDocId(int docId) {
        return propertyRepository.findByDocId(docId);
    }

    @Override
    public Boolean existsByAlias(String alias) {
        return propertyRepository.existsByAlias(alias);
    }

}
