package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultTemplateService implements TemplateService {

    private final TemplateRepository templateRepository;
    private final File templateDirectory;
    private final Set<String> templateExtensions = new HashSet<>(Arrays.asList("jsp", "jspx", "html"));

    DefaultTemplateService(TemplateRepository templateRepository,
                           @Value("WEB-INF/templates/text") File templateDirectory) {

        this.templateRepository = templateRepository;
        this.templateDirectory = templateDirectory;
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
            final Template template = templateRepository.findOne(templateName);
            return Optional.ofNullable(template).map(TemplateDTO::new);
        }

        return Optional.empty();
    }

    @Override
    public Path getPhysicalPath(String name) {
        for (String extension : templateExtensions) {
            final String templateFileName = name + "." + extension;
            final File templateFile = new File(templateDirectory, templateFileName);

            if (templateFile.exists()) {
                return templateFile.toPath();
            }
        }

        return null;
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
                .map(templateName -> new TemplateJPA(templateName, false, null))
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

}
