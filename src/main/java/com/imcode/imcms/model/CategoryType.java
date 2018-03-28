package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class CategoryType {

    protected CategoryType(CategoryType from) {
        setId(from.getId());
        setName(from.getName());
        setMultiSelect(from.isMultiSelect());
    }

    public abstract boolean isMultiSelect();

    public abstract void setMultiSelect(boolean multiSelect);

    public abstract boolean isInherited();

    public abstract void setInherited(boolean isInherited);

    public abstract boolean isImageArchive();

    public abstract void setImageArchive(boolean imageArchive);

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

}
