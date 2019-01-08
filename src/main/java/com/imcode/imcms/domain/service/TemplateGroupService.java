package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.TemplateGroup;

import java.util.List;

public interface TemplateGroupService {

    List<TemplateGroup> getAll();

    void save(TemplateGroup templateGroup);

    TemplateGroup get(String name);

    TemplateGroup get(Integer groupId);
}