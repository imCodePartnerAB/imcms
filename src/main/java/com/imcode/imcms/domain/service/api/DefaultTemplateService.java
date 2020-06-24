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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ucar.httpservices.HTTPAuthStore.log;

@Service
@Transactional
public class DefaultTemplateService implements TemplateService {

    private final TemplateRepository templateRepository;
    private final File templateDirectory;
    private final Set<String> templateExtensions = new HashSet<>(Arrays.asList("jsp", "jspx", "html"));
    private final TextDocumentTemplateService textDocumentTemplateService;
    private final Path templateAdminPath;

    DefaultTemplateService(TemplateRepository templateRepository,
                           @Value("WEB-INF/templates/text") File templateDirectory,
                           TextDocumentTemplateService textDocumentTemplateService, @Value("${TemplatePath}") Path templateAdminPath) {

        this.templateRepository = templateRepository;
        this.templateDirectory = templateDirectory;
        this.textDocumentTemplateService = textDocumentTemplateService;
        this.templateAdminPath = templateAdminPath.resolve("admin");
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
    public Optional<Template> get(String templateName) {
        if (isTemplateFileExist(templateName)) {
            final Template template = templateRepository.findByName(templateName);
            return Optional.ofNullable(template).map(TemplateDTO::new);
        }

        return Optional.empty();
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
    public Path saveTemplateFile(Template template, byte[] content, OpenOption writeMode) {
        final File templateFile = new File(templateDirectory, template.getName() + ".jsp");

        try {
            Files.write(templateFile.toPath(), content, writeMode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return templateFile.toPath();
    }

    @Override
    public void delete(Integer id) {
        List<TemplateJPA> allTemplates = templateRepository.findAll();
        if (allTemplates.size() > 1) {
            templateRepository.delete(id);
        } else {
            String errorMessage = "Templates less then 1 count in db!";
            log.error(errorMessage);
            throw new AloneTemplateException(errorMessage);
        }
    }

    @Override
    public void replaceTemplateFile(Path oldTemplate, Path newTemplate) {
        final String deleteTemplateName = oldTemplate.getFileName().toString();
        final String orgDeleteTemplateName = getPathWithoutExtension(deleteTemplateName);
        final String newTemplateName = newTemplate.getFileName().toString();
        final String orgNewTemplateName = getPathWithoutExtension(newTemplateName);
        final Template deletedTemplate = templateRepository.findByName(orgDeleteTemplateName);
        final Template replaceReceivedTemplate = templateRepository.findByName(orgNewTemplateName);
        if (deletedTemplate != null && replaceReceivedTemplate != null) {
            List<TextDocumentTemplateDTO> docsDeletedTemplate = textDocumentTemplateService.getByTemplateName(deleteTemplateName);

            docsDeletedTemplate.forEach(textDoc -> {
                textDoc.setTemplateName(orgNewTemplateName);
                textDoc.setChildrenTemplateName(orgNewTemplateName);
                textDocumentTemplateService.save(textDoc);
            });

        } else {
            final String errorMessage = "Template not exist " + orgDeleteTemplateName + " " + orgNewTemplateName;
            log.error(errorMessage);
            throw new EmptyResultDataAccessException(errorMessage, -1);
        }
    }

    public File getTemplateDirectory() {
        return templateDirectory;
    }

    /**
     * Will save to DB all templates if not yet for files in directory.
     */
    @PostConstruct
    private void saveTemplatesInFolder() {
        final Set<String> savedTemplateNames = templateRepository.findAll()
                .stream()
                .map(Template::getName)
                .collect(Collectors.toSet());

        final String[] templateNamesToBeSaved = templateDirectory.list((dir, name) -> {
            final String extension = FilenameUtils.getExtension(name);

            if (!templateExtensions.contains(extension)) {
                return false;
            }

            final String templateName = FilenameUtils.getBaseName(name);
            return !savedTemplateNames.contains(templateName);
        });

        if (templateNamesToBeSaved == null) {
            return; // strange situation, should not happen at all
        }

        Arrays.stream(templateNamesToBeSaved)
                .map(FilenameUtils::getBaseName)
                // TODO: 26.04.19 Check if template already assigned to group at DB
                .map(templateName -> new TemplateJPA(null, templateName, false, null))
                .forEach(templateRepository::save);
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

    private String getPathWithoutExtension(String fileName) {
        return FilenameUtils.removeExtension(fileName);
    }

}
