package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CategoryTypeDTO extends CategoryType {

    private static final long serialVersionUID = -4636053716188761920L;

    private Integer id;

    private String name;

    private boolean multiSelect;

    private boolean inherited;

    private boolean visible;

    public CategoryTypeDTO(CategoryType from) {
        super(from);
    }
}
