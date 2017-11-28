package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

/**
 * Super class for both JPA and DTO realisation
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.11.17.
 */
@NoArgsConstructor
public abstract class Template {

    protected Template(Template templateFrom) {
        setId(templateFrom.getId());
        setName(templateFrom.getName());
        setHidden(templateFrom.isHidden());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract boolean isHidden();

    public abstract void setHidden(boolean isHidden);
}
