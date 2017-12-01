package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class TemplateGroup<T extends Template> {

    protected <T1 extends Template, TG extends TemplateGroup<T1>> TemplateGroup(TG from, Function<T1, T> templateMapper) {
        setId(from.getId());
        setName(from.getName());
        setTemplates(from.getTemplates().stream().map(templateMapper).collect(Collectors.toList()));
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract List<T> getTemplates();

    public abstract void setTemplates(List<T> templates);

}
