package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.TemplateDataHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDTO extends TemplateDataHolder implements Serializable {

    private static final long serialVersionUID = 441290133487733989L;

    private Integer id;

    private String name;

    private boolean hidden;

    public TemplateDTO(TemplateDataHolder templateFrom) {
        super(templateFrom);
    }
}
