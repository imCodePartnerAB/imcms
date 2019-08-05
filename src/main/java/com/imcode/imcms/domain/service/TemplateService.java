package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Template;

import java.io.File;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Service for work with templates in relation to template files.
 */
public interface TemplateService {

    List<Template> getAll();

    void save(Template saveMe);

    File getTemplateDirectory();

    Optional<Template> get(String name);

    Path getPhysicalPath(String name);

    Path saveTemplateFile(Template template, byte[] content, OpenOption writeMode);

    void delete(String templateName);
}
