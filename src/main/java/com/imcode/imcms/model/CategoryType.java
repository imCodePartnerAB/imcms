package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class CategoryType implements Serializable {

    private static final long serialVersionUID = 6975350692159491957L;

    protected CategoryType(CategoryType from) {
        setId(from.getId());
        setName(from.getName());
        setMultiSelect(from.isMultiSelect());
        setInherited(from.isInherited());
    }

    public abstract boolean isMultiSelect();

    public abstract void setMultiSelect(boolean multiSelect);

    public abstract boolean isInherited();

    public abstract void setInherited(boolean isInherited);

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

}
