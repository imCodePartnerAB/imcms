package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CategoryTypeDTO extends CategoryType implements Serializable {

    private static final long serialVersionUID = -4636053716188761920L;

    private Integer id;

    private String name;

    private boolean multiSelect;

    private List<CategoryDTO> categories;

    public CategoryTypeDTO(CategoryType from) {
        super(from);
    }

    @Override
    public List<Category> getCategories() {
        return (categories == null) ? null : new ArrayList<>(categories);
    }

    @Override
    public void setCategories(List<Category> categories) {
        this.categories = (categories == null) ? null
                : categories.stream().map(CategoryDTO::new).collect(Collectors.toList());
    }
}
