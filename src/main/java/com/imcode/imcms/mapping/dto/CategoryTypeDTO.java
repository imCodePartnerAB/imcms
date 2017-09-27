package com.imcode.imcms.mapping.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CategoryTypeDTO implements Serializable {

    private static final long serialVersionUID = -4636053716188761920L;

    private Integer id;

    private String name;

    private Boolean multiSelect;

    private List<CategoryDTO> categories;

}
