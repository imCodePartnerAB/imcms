package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.TemplateGroup;

import java.util.List;

public interface TemplateGroupService {
    List<TemplateGroup> getAll();

    TemplateGroup save(TemplateGroup templateGroup);

    TemplateGroup edit(TemplateGroup templateGroup);

    void addTemplate(String templateFilename, Integer groupId);

    void deleteTemplate(String templateName, Integer groupId);

    TemplateGroup get(String name);

    TemplateGroup get(Integer groupId);

    void remove(Integer id);
}
