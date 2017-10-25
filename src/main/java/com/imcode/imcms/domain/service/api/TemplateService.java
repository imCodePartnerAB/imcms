package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.persistence.entity.Template;
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
    private final Function<Template, TemplateDTO> templateToTemplateDTO;

    public TemplateService(TemplateRepository templateRepository,
                           Function<Template, TemplateDTO> templateToTemplateDTO) {
        this.templateRepository = templateRepository;
        this.templateToTemplateDTO = templateToTemplateDTO;
    }

    public List<TemplateDTO> getAll() {
        return templateRepository.findAll().stream()
                .map(templateToTemplateDTO)
                .collect(Collectors.toList());
    }

    public Optional<TemplateDTO> getTemplate(String templateName) {
        String[] extensions = new String[]{"jsp", "jspx", "html"};
        for (String extension : extensions) {
            String templateFileName = templateName + "." + extension;
            File templateFile = new File(TemplateMapper.getTemplateDirectory(), templateFileName);
            if (templateFile.exists()) {
                final Template template = templateRepository.findByName(templateName);
                return Optional.ofNullable(template).map(templateToTemplateDTO);
            }
        }
        return Optional.empty();
    }

}
