package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Category {

    protected Category(Category from) {
        setId(from.getId());
        setName(from.getName());
        setDescription(from.getDescription());
        setImageUrl(from.getImageUrl());
        setType(from.getType());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract String getImageUrl();

    public abstract void setImageUrl(String imageUrl);

    public abstract CategoryType getType();

    public abstract void setType(CategoryType type);

}
