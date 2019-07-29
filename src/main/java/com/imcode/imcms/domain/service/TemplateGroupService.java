package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.TemplateGroup;

import java.util.List;

public interface TemplateGroupService {
    //TODO Drop get by id. After switch to work on folder based model
    List<TemplateGroup> getAll();

    TemplateGroup save(TemplateGroup templateGroup);

    TemplateGroup edit(TemplateGroup templateGroup);

    TemplateGroup get(String name);

    TemplateGroup get(Integer groupId);

    void remove(String name);
}
