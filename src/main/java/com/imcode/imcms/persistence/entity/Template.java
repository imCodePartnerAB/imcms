package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Template {

    protected Template(Template templateFrom) {
        setName(templateFrom.getName());
        setHidden(templateFrom.isHidden());
    }

    public abstract String getName();

    public abstract void setName(String name);

    public abstract boolean isHidden();

    public abstract void setHidden(boolean isHidden);
}
