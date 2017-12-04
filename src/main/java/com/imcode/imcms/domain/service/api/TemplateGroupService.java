package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateGroupDTO;
import com.imcode.imcms.persistence.entity.TemplateGroupJPA;
import com.imcode.imcms.persistence.repository.TemplateGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class TemplateGroupService {

    private final TemplateGroupRepository templateGroupRepository;

    public TemplateGroupService(TemplateGroupRepository templateGroupRepository) {
        this.templateGroupRepository = templateGroupRepository;
    }

    public List<TemplateGroupDTO> getAll() {
        return templateGroupRepository.findAll().stream()
                .map(TemplateGroupDTO::new)
                .collect(Collectors.toList());
    }

    public void save(TemplateGroupDTO templateGroupDTO) {
        final TemplateGroupJPA templateGroupJpa = new TemplateGroupJPA(templateGroupDTO);
        templateGroupRepository.saveAndFlush(templateGroupJpa);
    }

    public TemplateGroupDTO get(String name) {
        final TemplateGroupJPA templateGroupJPA = templateGroupRepository.findByName(name);
        return new TemplateGroupDTO(templateGroupJPA);
    }

}
