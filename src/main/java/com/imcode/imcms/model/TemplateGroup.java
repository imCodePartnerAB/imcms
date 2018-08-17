package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
public abstract class TemplateGroup implements Serializable {

    private static final long serialVersionUID = 1728725295437520411L;

    protected TemplateGroup(TemplateGroup from) {
        setId(from.getId());
        setName(from.getName());
        setTemplates(from.getTemplates());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Set<Template> getTemplates();

    public abstract void setTemplates(Set<Template> templates);

}
