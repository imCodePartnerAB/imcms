package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateService {
    List<Template> getAll();

    void save(Template saveMe);

    Optional<Template> getTemplate(String templateName);
}
