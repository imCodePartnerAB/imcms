package com.imcode.imcms.mapping.dto;

import java.io.Serializable;

public class CategoryDTO extends AbstractIdDTO implements Serializable{

    private static final long serialVersionUID = -848500041308510098L;

    private String name;

    public CategoryDTO(Integer id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
