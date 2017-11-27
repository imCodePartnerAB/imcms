package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CategoryDTO extends Category implements Serializable {

    private static final long serialVersionUID = -848500041308510098L;

    private Integer id;

    private String name;

    public CategoryDTO(Category from) {
        super(from);
    }
}
