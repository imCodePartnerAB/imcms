package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;

import java.util.List;

public interface TemplateGroupService {

    List<TemplateGroup<? extends Template>> getAll();

    void save(TemplateGroup<? extends Template> templateGroup);

    TemplateGroup<? extends Template> get(String name);

}
