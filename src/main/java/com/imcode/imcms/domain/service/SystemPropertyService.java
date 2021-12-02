package com.imcode.imcms.domain.service;

import com.imcode.imcms.mapping.jpa.SystemProperty;

import java.util.List;

public interface SystemPropertyService {

    SystemProperty findById(Integer id);

    List<SystemProperty> findAll();

    SystemProperty findByName(String name);

    SystemProperty update(SystemProperty systemProperty);

}
