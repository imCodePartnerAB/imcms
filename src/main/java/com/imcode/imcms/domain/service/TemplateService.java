package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Template;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Service for work with templates in relation to template files.
 */
public interface TemplateService {

    Set<String> checkTemplates();

    List<Template> getAll();

    File getTemplateDirectory();

    Template get(String name);

    Template getById(int id);

    Path getTemplateAdminPath(String templateName);

    Path getPhysicalPathTemplateAdmin(String templateName);

    Path getPhysicalPath(String name);

    boolean isValidName(String name);

    void save(Template saveMe);

    /**
     * replaceTemplateFile provide replace all documents which uses old template on new template!
     * If new template exists only one in db, it will throw exception!
     */
    void replaceTemplateFile(String oldTemplateName, String newTemplateName);

    void renameTemplate(String oldTemplateName, String newTemplateName);

    void delete(Integer id);
}
