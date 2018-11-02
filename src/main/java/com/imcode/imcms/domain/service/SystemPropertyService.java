package com.imcode.imcms.domain.service;

import com.imcode.imcms.mapping.jpa.SystemProperty;

import java.util.List;

public interface SystemPropertyService {

    List<SystemProperty> findAll();

    SystemProperty findByName(String name);

    SystemProperty update(SystemProperty systemProperty);

    void deleteById(int id);

    SystemProperty findById(int id);

}
