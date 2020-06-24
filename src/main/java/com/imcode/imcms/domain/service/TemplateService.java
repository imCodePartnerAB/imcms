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

    Path getTemplateAdminPath(String templateName);

    Path getPhysicalPathTemplateAdmin(String templateName);

    Path getPhysicalPath(String name);

    Path saveTemplateFile(Template template, byte[] content, OpenOption writeMode);

    void delete(Integer id);

    /**
     * replaceTemplateFile provide replace all documents which uses old template on newTemplate!
     * If template exists only one in db, it will throw exception, because we can not delete
     * existing last template!
     *
     * @param oldTemplate - file template or data template which will delete
     * @param newTemplate - file template or data template which will replace on
     */
    void replaceTemplateFile(Path oldTemplate, Path newTemplate);
}
