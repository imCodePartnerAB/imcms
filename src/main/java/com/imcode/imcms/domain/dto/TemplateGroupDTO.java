package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.TemplateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TemplateGroupDTO extends TemplateGroup<TemplateDTO> {

    private Integer id;

    private String name;

    private List<TemplateDTO> templates;

}
