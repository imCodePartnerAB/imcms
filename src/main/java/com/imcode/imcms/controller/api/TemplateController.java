package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<Template> getAll() {
        return templateService.getAll();
    }

    @PutMapping("/replace")
    public void replaceTemplate(@RequestBody Properties data) {
        final String oldTemplateName = data.getProperty("oldTemplate");
        final String newTemplateName = data.getProperty("newTemplate");
        templateService.replaceTemplateFile(oldTemplateName, newTemplateName);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        templateService.delete(id);
    }
}
