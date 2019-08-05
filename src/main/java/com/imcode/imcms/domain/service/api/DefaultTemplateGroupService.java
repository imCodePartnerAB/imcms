package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateGroupDTO;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.model.TemplateGroup;
import com.imcode.imcms.persistence.entity.TemplateGroupJPA;
import com.imcode.imcms.persistence.repository.TemplateGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
class DefaultTemplateGroupService implements TemplateGroupService {

    private final TemplateGroupRepository templateGroupRepository;

    DefaultTemplateGroupService(TemplateGroupRepository templateGroupRepository) {
        this.templateGroupRepository = templateGroupRepository;
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
    public TemplateGroup edit(TemplateGroup templateGroup) { //todo it necessary or delete it ?
        final TemplateGroupJPA receivedTemplateGroup = templateGroupRepository.findOne(templateGroup.getId());
        receivedTemplateGroup.setName(templateGroup.getName());
        return new TemplateGroupDTO(templateGroupRepository.save(receivedTemplateGroup));
    }

    @Override
    public TemplateGroup get(String name) {
        final TemplateGroupJPA templateGroupJPA = templateGroupRepository.findByName(name);
        return new TemplateGroupDTO(templateGroupJPA);
    }

    @Override
    public TemplateGroup get(Integer groupId) { //Todo need remove that ? work only directory?
        return new TemplateGroupDTO(templateGroupRepository.findOne(groupId));
    }

    @Override
    public void remove(String name) {
        templateGroupRepository.deleteByName(name);
    }
}
