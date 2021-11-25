package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TemplateGroupDTO;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.model.TemplateGroup;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Properties;

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

    @PutMapping("/add-template")
    public void addTemplateToGroup(@RequestBody Properties data) {
        final String templateName =  data.getProperty("templateName");
        final Integer groupId = Integer.valueOf(data.getProperty("templateGroupId"));
        templateGroupService.addTemplate(templateName, groupId);
    }

    @PatchMapping("/delete-template")
    public void deleteGroup(@RequestBody Properties data){
        final String templateName = data.getProperty("templateName");
        final Integer groupId = Integer.valueOf(data.getProperty("templateGroupId"));
        templateGroupService.deleteTemplate(templateName, groupId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        templateGroupService.remove(id);
    }
}
