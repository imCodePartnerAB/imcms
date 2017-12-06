package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.TemplateDTO;

import java.util.List;
import java.util.Optional;

public interface TemplateService {
    List<TemplateDTO> getAll();

    void save(TemplateDTO saveMe);

    Optional<TemplateDTO> getTemplate(String templateName);
}
