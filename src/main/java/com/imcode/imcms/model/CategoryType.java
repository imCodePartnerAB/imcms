package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class CategoryType {

    protected CategoryType(CategoryType from) {
        setId(from.getId());
        setName(from.getName());
        setMultiSelect(from.isMultiSelect());
//        setCategories(from.getCategories());
    }

    public abstract boolean isMultiSelect();

    public abstract void setMultiSelect(boolean multiSelect);

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

//    public abstract List<Category> getCategories();
//
//    public abstract void setCategories(List<Category> categories);

}
