package com.imcode.imcms.mapping.dto;

import java.io.Serializable;
import java.util.List;

public class CategoryTypeDTO extends AbstractIdDTO implements Serializable {

    private static final long serialVersionUID = -4636053716188761920L;

    private String name;
    private Boolean multiSelect;
    private List<CategoryDTO> categories;

    public CategoryTypeDTO(Integer id, String name, Boolean multiSelect, List<CategoryDTO> categories) {
        super(id);
        this.name = name;
        this.multiSelect = multiSelect;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public Boolean getMultiSelect() {
        return multiSelect;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }
}
