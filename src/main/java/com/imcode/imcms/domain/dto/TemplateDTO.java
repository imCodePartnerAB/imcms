package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TemplateDTO extends Template {

    private static final long serialVersionUID = 441290133487733989L;

    private Integer id;

    private String name;

    private boolean hidden;

    public TemplateDTO(Template templateFrom) {
        super(templateFrom);
    }
}
