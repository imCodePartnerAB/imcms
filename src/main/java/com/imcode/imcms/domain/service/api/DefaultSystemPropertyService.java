package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.SystemPropertyService;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import com.imcode.imcms.mapping.jpa.SystemPropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DefaultSystemPropertyService implements SystemPropertyService {

    private final SystemPropertyRepository systemPropertyRepository;

    public DefaultSystemPropertyService(SystemPropertyRepository systemProperty) {
        this.systemPropertyRepository = systemProperty;
    }

    @Override
    public SystemProperty findById(int id) {
        return systemPropertyRepository.findOne(id);
    }

    @Override
    public List<SystemProperty> findAll() {
        return systemPropertyRepository.findAll();
    }

    @Override
    public SystemProperty findByName(String name) {
        return systemPropertyRepository.findByName(name);
    }

    @Override
    public SystemProperty update(SystemProperty systemProperty) {
        return systemPropertyRepository.save(systemProperty);
    }

    @Override
    public void deleteById(int id) {
        systemPropertyRepository.delete(id);
    }
}
