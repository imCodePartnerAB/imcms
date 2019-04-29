package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class Template implements Serializable {

    private static final long serialVersionUID = -9018871628940459460L;

    protected Template(Template templateFrom) {
        setName(templateFrom.getName());
        setHidden(templateFrom.isHidden());
        setTemplateGroup(templateFrom.getTemplateGroup());
    }

    public abstract String getName();

    public abstract void setName(String name);

    public abstract boolean isHidden();

    public abstract void setHidden(boolean isHidden);

    public abstract TemplateGroup getTemplateGroup();

    public abstract void setTemplateGroup(TemplateGroup template);

}
