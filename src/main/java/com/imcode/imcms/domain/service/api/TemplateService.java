package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import imcode.server.document.TemplateMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final Function<TemplateJPA, TemplateDTO> templateToTemplateDTO;
    private final Function<TemplateDTO, TemplateDTO> templateSaver;

    public TemplateService(TemplateRepository templateRepository,
                           Function<TemplateJPA, TemplateDTO> templateToTemplateDTO,
                           Function<TemplateDTO, TemplateJPA> templateDtoToTemplate) {

        this.templateRepository = templateRepository;
        this.templateToTemplateDTO = templateToTemplateDTO;
        this.templateSaver = templateDtoToTemplate.andThen(templateRepository::save).andThen(templateToTemplateDTO);
    }

    public List<TemplateDTO> getAll() {
        return templateRepository.findAll().stream()
                .map(templateToTemplateDTO)
                .collect(Collectors.toList());
    }

    public TemplateDTO save(TemplateDTO saveMe) {
        final String templateName = saveMe.getName();

        if (isTemplateFileExist(templateName)) {
            return templateSaver.apply(saveMe);
        }

        return null;
    }

    public Optional<TemplateDTO> getTemplate(String templateName) {
        if (isTemplateFileExist(templateName)) {
            final TemplateJPA template = templateRepository.findByName(templateName);
            return Optional.ofNullable(template).map(templateToTemplateDTO);
        }

        return Optional.empty();
    }

    private boolean isTemplateFileExist(String templateName) {
        for (String extension : new String[]{"jsp", "jspx", "html"}) {
            final String templateFileName = templateName + "." + extension;
            final File templateFile = new File(TemplateMapper.getTemplateDirectory(), templateFileName);

            if (templateFile.exists()) {
                return true;
            }
        }

        return false;
    }

}
