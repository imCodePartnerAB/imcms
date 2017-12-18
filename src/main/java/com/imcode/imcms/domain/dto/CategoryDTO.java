package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CategoryDTO extends Category implements Serializable {

    private static final long serialVersionUID = -848500041308510098L;

    private Integer id;

    private String name;

    private String description;

    private String imageUrl;

    private CategoryTypeDTO type;

    public CategoryDTO(Category from) {
        super(from);
    }

    @Override
    public CategoryType getType() {
        return type;
    }

    @Override
    public void setType(CategoryType type) {
        this.type = new CategoryTypeDTO(type);
    }
}
