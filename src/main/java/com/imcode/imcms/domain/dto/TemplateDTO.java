package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Template;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class TemplateDTO extends Template implements Serializable {

    private static final long serialVersionUID = 441290133487733989L;

    private String name;

    private boolean hidden;

    public TemplateDTO(Template templateFrom) {
        super(templateFrom);
    }
}
