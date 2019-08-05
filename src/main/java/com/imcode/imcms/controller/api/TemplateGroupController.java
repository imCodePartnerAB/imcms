package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TemplateGroupDTO;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.model.TemplateGroup;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/template-group")
public class TemplateGroupController {

    private final TemplateGroupService templateGroupService;


    TemplateGroupController(TemplateGroupService templateGroupService) {
        this.templateGroupService = templateGroupService;
    }


    @GetMapping
    public List<TemplateGroup> getAll() {
        return templateGroupService.getAll();
    }

    @GetMapping("/{name}")
    public TemplateGroup getByName(@PathVariable String name) {
        return templateGroupService.get(name);
    }

    @PostMapping
    public TemplateGroup create(@RequestBody TemplateGroupDTO templateGroup) {
        return templateGroupService.save(templateGroup);
    }

    @PutMapping
    public TemplateGroup edit(@RequestBody TemplateGroupDTO templateGroup) {
        return templateGroupService.edit(templateGroup);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        templateGroupService.remove(id);
    }
}
