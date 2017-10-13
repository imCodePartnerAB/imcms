package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.persistence.entity.Template;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

}
