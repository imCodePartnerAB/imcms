package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class Category implements Serializable {

    private static final long serialVersionUID = 2473773444124523520L;

    protected Category(Category from) {
        setId(from.getId());
        setName(from.getName());
        setDescription(from.getDescription());
        setType(from.getType());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract CategoryType getType();

    public abstract void setType(CategoryType type);

}
