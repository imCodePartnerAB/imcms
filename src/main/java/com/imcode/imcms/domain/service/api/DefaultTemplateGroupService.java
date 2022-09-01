package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateGroupDTO;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import com.imcode.imcms.persistence.entity.TemplateGroupJPA;
import com.imcode.imcms.persistence.repository.TemplateGroupRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultTemplateGroupService implements TemplateGroupService {

    private final TemplateGroupRepository templateGroupRepository;

    private final TemplateService templateService;

    DefaultTemplateGroupService(TemplateGroupRepository templateGroupRepository, TemplateService templateService) {
        this.templateGroupRepository = templateGroupRepository;
        this.templateService = templateService;
    }

    @Override
    public List<TemplateGroup> getAll() {
        return templateGroupRepository.findAll()
                .stream()
                .map(TemplateGroupDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public TemplateGroup save(TemplateGroup templateGroup) {
        final TemplateGroupJPA templateGroupJpa = new TemplateGroupJPA(templateGroup);
        return new TemplateGroupDTO(templateGroupRepository.saveAndFlush(templateGroupJpa));
    }

    @Override
    public TemplateGroup edit(TemplateGroup templateGroup) {
        final TemplateGroupJPA receivedTemplateGroup = templateGroupRepository.getOne(templateGroup.getId());
        receivedTemplateGroup.setName(templateGroup.getName());
        return new TemplateGroupDTO(templateGroupRepository.save(receivedTemplateGroup));
    }

    @Override
    public void addTemplate(String templateFilename, Integer groupId) {
	    final Template addedTemplate = templateService.get(FilenameUtils.removeExtension(templateFilename));
	    final TemplateGroupJPA group = templateGroupRepository.findById(groupId).orElse(null);
        if(addedTemplate != null && group != null){
            final Set<Template> groupTemplates = group.getTemplates();
            if(groupTemplates.stream().noneMatch(template -> addedTemplate.getId().equals(template.getId()))){
                groupTemplates.add(addedTemplate);
                group.setTemplates(groupTemplates);
                templateGroupRepository.saveAndFlush(group);
            }
        }
    }

    @Override
    public void deleteTemplate(String templateName, Integer groupId) {
	    final TemplateGroupJPA templateGroup = templateGroupRepository.findById(groupId).orElse(null);
        if(templateGroup != null){
            final Set<Template> templates = templateGroup.getTemplates().stream()
                    .filter(template -> !template.getName().equals(templateName)).collect(Collectors.toSet());

            templateGroup.setTemplates(templates);
            templateGroupRepository.save(templateGroup);
        }
    }

    @Override
    public TemplateGroup get(String name) {
        final TemplateGroupJPA templateGroupJPA = templateGroupRepository.findByName(name);
        return new TemplateGroupDTO(templateGroupJPA);
    }

    @Override
    public TemplateGroup get(Integer groupId) {
	    return new TemplateGroupDTO(templateGroupRepository.getOne(groupId));
    }

    @Override
    public void remove(Integer id) {
	    templateGroupRepository.deleteTemplateGroupByGroupId(id);
	    templateGroupRepository.deleteById(id);
    }
}
