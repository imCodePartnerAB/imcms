package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TemplateDTO extends Template {

    private static final long serialVersionUID = 441290133487733989L;

    private String name;

    private boolean hidden;

    private TemplateGroup templateGroup;

    public TemplateDTO(Template templateFrom) {
        super(templateFrom);
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    public TemplateGroup getTemplateGroup() {
        return templateGroup;
    }
}
