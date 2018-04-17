package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class TemplateGroupDTO extends TemplateGroup {

    private static final long serialVersionUID = -5039489943024804410L;

    private Integer id;

    private String name;

    private List<TemplateDTO> templates;

    public TemplateGroupDTO(TemplateGroup from) {
        super(from);
    }

    @Override
    public List<Template> getTemplates() {
        return (templates == null) ? null : new ArrayList<>(templates);
    }

    @Override
    public void setTemplates(List<Template> templates) {
        this.templates = (templates == null) ? null
                : templates.stream().map(TemplateDTO::new).collect(Collectors.toList());
    }
}
