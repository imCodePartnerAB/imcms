package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CategoryDTO extends Category {

    private static final long serialVersionUID = -848500041308510098L;

    private Integer id;

    private String name;

    private String description;

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
        this.type = (type == null) ? null : new CategoryTypeDTO(type);
    }
}
