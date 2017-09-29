package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class CategoryTypeDTO implements Serializable {

    private static final long serialVersionUID = -4636053716188761920L;

    private Integer id;

    private String name;

    private Boolean multiSelect;

    private List<CategoryDTO> categories;

}
