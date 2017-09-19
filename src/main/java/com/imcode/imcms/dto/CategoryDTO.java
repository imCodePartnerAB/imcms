package com.imcode.imcms.dto;

import imcode.server.document.CategoryDomainObject;

import java.io.Serializable;

public class CategoryDTO implements Serializable{

    private final Integer id;
    private final String name;

    private CategoryDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryDTO of(CategoryDomainObject categoryDO) {
        return new CategoryDTO(categoryDO.getId(), categoryDO.getName());
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
