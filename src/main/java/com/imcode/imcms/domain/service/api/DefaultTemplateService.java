package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.persistence.entity.Template;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for work with templates in relation to template files.
 */
@Service
@Transactional
public class DefaultTemplateService implements TemplateService {

    private final TemplateRepository templateRepository;
    private final File templateDirectory;
    private final Function<TemplateDTO, TemplateJPA> templateSaver;
    private final Set<String> templateExtensions = new HashSet<>(Arrays.asList("jsp", "jspx", "html"));

    DefaultTemplateService(TemplateRepository templateRepository,
                           @Value("WEB-INF/templates/text") File templateDirectory) {

        this.templateRepository = templateRepository;
        this.templateDirectory = templateDirectory;
        this.templateSaver = ((Function<TemplateDTO, TemplateJPA>) TemplateJPA::new)
                .andThen(templateRepository::save);
    }

    @Override
    public List<TemplateDTO> getAll() {
        return templateRepository.findAll().stream()
                .map(TemplateDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void save(TemplateDTO saveMe) {
        final String templateName = saveMe.getName();

        if (isTemplateFileExist(templateName)) {
            templateSaver.apply(saveMe);
        }
    }

    @Override
    public Optional<TemplateDTO> getTemplate(String templateName) {
        if (isTemplateFileExist(templateName)) {
            final TemplateJPA template = templateRepository.findOne(templateName);
            return Optional.ofNullable(template).map(TemplateDTO::new);
        }

        return Optional.empty();
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
                .map(templateName -> new TemplateJPA(templateName, false))
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
