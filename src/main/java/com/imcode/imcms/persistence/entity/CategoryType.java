package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class CategoryType<C extends Category> {

    protected <C2 extends Category, CT extends CategoryType<C2>> CategoryType(CT from, Function<C2, C> categoryMapper) {
        setId(from.getId());
        setName(from.getName());
        setMultiSelect(from.isMultiSelect());
        setCategories(from.getCategories().stream().map(categoryMapper).collect(Collectors.toList()));
    }

    public abstract boolean isMultiSelect();

    public abstract void setMultiSelect(boolean multiSelect);

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract List<C> getCategories();

    public abstract void setCategories(List<C> categories);

}
