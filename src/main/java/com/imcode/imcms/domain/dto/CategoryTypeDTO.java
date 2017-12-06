package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class CategoryTypeDTO extends CategoryType<CategoryDTO> implements Serializable {

    private static final long serialVersionUID = -4636053716188761920L;

    private Integer id;

    private String name;

    private boolean multiSelect;

    private List<CategoryDTO> categories;

    public <C2 extends Category, CT extends CategoryType<C2>> CategoryTypeDTO(CT from) {
        super(from, CategoryDTO::new);
    }
}
