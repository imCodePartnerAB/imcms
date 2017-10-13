package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.service.api.TemplateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<TemplateDTO> getAll() {
        return templateService.getAll();
    }

}
