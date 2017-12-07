package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public abstract class TemplateGroup {

    protected TemplateGroup(TemplateGroup from) {
        setId(from.getId());
        setName(from.getName());
        setTemplates(from.getTemplates());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract List<Template> getTemplates();

    public abstract void setTemplates(List<Template> templates);

}
