package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.AloneTemplateException;
import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static ucar.httpservices.HTTPAuthStore.log;

@Service
@Transactional
public class DefaultTemplateService implements TemplateService {

    private final TemplateRepository templateRepository;

    private final TextDocumentTemplateService textDocumentTemplateService;

    private final File templateDirectory;
    private final Set<String> templateExtensions = new HashSet<>(Arrays.asList("jsp", "jspx", "html"));
    private final Path templateAdminPath;

    DefaultTemplateService(TemplateRepository templateRepository, TextDocumentTemplateService textDocumentTemplateService,
                           @Value("WEB-INF/templates/text") File templateDirectory, @Value("${TemplatePath}") Path templateAdminPath) {

        this.templateRepository = templateRepository;
        this.templateDirectory = templateDirectory;
        this.textDocumentTemplateService = textDocumentTemplateService;
        this.templateAdminPath = templateAdminPath.resolve("admin");
    }

    /**
     * Will save/delete to DB all templates if not yet for files in directory.
     */
    @PostConstruct
    private void saveTemplatesInFolder() {
        final Set<String> savedTemplateNames = templateRepository.findAll()
                .stream()
                .map(Template::getName)
                .collect(Collectors.toSet());

        final String[] templateNames = templateDirectory.list(
                (dir, name) -> templateExtensions.contains(FilenameUtils.getExtension(name)));

        if(templateNames == null) return;

        final List<String> templateNamesToBeSaved = Arrays.stream(templateNames)
                .filter(templateName -> !savedTemplateNames.contains(FilenameUtils.getBaseName(templateName))).collect(Collectors.toList());

        templateNamesToBeSaved.stream()
                .map(FilenameUtils::getBaseName)
                .map(templateName -> new TemplateJPA(null, templateName, false))
                .forEach(templateRepository::save);

        //delete templates from db that were manually deleted
        final List<TemplateJPA> allTemplates = templateRepository.findAll();
        if (allTemplates.size() > templateNames.length) {
            final List<String> templateNamesList = Arrays.stream(templateNames)
                    .map(FilenameUtils::getBaseName).collect(Collectors.toList());

            allTemplates.stream()
                    .filter(template -> !templateNamesList.contains(template.getName()))
                    .forEach(template -> delete(template.getId()));
        }
    }

    @Override
    public List<Template> getAll() {
        return templateRepository.findAll().stream()
                .map(TemplateDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Template saveMe) {
        final String templateName = saveMe.getName();

        if (isTemplateFileExist(templateName)) {
            templateRepository.save(new TemplateJPA(saveMe));
        }
    }

    @Override
    public Template get(String templateName) {
        return templateRepository.findByName(templateName);
    }

    @Override
    public Path getTemplateAdminPath(String template) {
        return templateAdminPath.resolve(template);
    }

    @Override
    public Path getPhysicalPathTemplateAdmin(String templateName) {
        return getPhysicalPathTemplateFromDirectory(templateAdminPath.toFile(), templateName);
    }

    @Override
    public Path getPhysicalPath(String name) {
        return getPhysicalPathTemplateFromDirectory(templateDirectory, name);
    }

    @Override
    public void delete(Integer id) {
        List<TemplateJPA> allTemplates = templateRepository.findAll();
        if (allTemplates.size() > 1) {
            templateRepository.deleteTemplateGroupByTemplateId(id);
            templateRepository.delete(id);
        } else {
            String errorMessage = "Templates less then 1 count in db!";
            log.error(errorMessage);
            throw new AloneTemplateException(errorMessage);
        }
    }

    @Override
    public void replaceTemplateFile(String oldTemplateName, String newTemplateName) {
        if (templateRepository.findByName(newTemplateName) != null) {
            List<TextDocumentTemplateDTO> docsDeletedTemplate = textDocumentTemplateService.getByTemplateName(oldTemplateName);

            docsDeletedTemplate.forEach(textDoc -> {
                textDoc.setTemplateName(newTemplateName);
                textDoc.setChildrenTemplateName(newTemplateName);
                textDocumentTemplateService.save(textDoc);
            });

        } else {
            final String errorMessage = "Template not exist " + oldTemplateName + " " + newTemplateName;
            log.error(errorMessage);
            throw new EmptyResultDataAccessException(errorMessage, -1);
        }
    }

    @Override
    public void renameTemplate(String oldTemplateName, String newTemplateName) {
        templateRepository.updateTemplateName(oldTemplateName, newTemplateName);

        textDocumentTemplateService.getByTemplateName(oldTemplateName).forEach(textDoc -> {
            textDoc.setTemplateName(newTemplateName);
            textDoc.setChildrenTemplateName(newTemplateName);
            textDocumentTemplateService.save(textDoc);
        });
    }

    @Override
    public boolean isValidName(String name) {
        final String extension = FilenameUtils.getExtension(name);
        return templateExtensions.contains(extension);
    }

    @Override
    public File getTemplateDirectory() {
        return templateDirectory;
    }

    private boolean isTemplateFileExist(String templateName) {
        for (String extension : templateExtensions) {
            final String templateFileName = templateName + "." + extension;
            final File templateFile = new File(templateDirectory, templateFileName);

            if (templateFile.exists()) {
                return true;
            }
        }

        return false;
    }

    private Path getPhysicalPathTemplateFromDirectory(File templateDirectory, String templateName) {
        for (String extension : templateExtensions) {
            final String templateFileName = templateName + "." + extension;
            final File templateFile = new File(templateDirectory, templateFileName);

            if (templateFile.exists()) {
                return templateFile.toPath();
            }
        }

        return null;
    }
}
