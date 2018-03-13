package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Template;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Service for work with templates in relation to template files.
 */
public interface TemplateService {

    List<Template> getAll();

    void save(Template saveMe);

    Optional<Template> getTemplateOptional(String templateName);

    File getTemplateDirectory();

    @Deprecated
    Template getTemplate(String templateName);
}
