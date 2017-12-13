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

    Optional<Template> getTemplate(String templateName);

    File getTemplateDirectory();

}
