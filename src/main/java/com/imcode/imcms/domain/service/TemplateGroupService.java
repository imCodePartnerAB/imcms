package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.TemplateGroupDTO;

import java.util.List;

public interface TemplateGroupService {

    List<TemplateGroupDTO> getAll();

    void save(TemplateGroupDTO templateGroupDTO);

    TemplateGroupDTO get(String name);

}
