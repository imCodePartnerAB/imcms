package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Template;
import com.imcode.imcms.persistence.entity.TemplateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateGroupDTO extends TemplateGroup<TemplateDTO> {

    private Integer id;

    private String name;

    private List<TemplateDTO> templates;

    public <T1 extends Template, TG extends TemplateGroup<T1>> TemplateGroupDTO(TG from) {
        super(from, TemplateDTO::new);
    }

}
