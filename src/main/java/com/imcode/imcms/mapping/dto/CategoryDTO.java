package com.imcode.imcms.mapping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CategoryDTO implements Serializable{

    private static final long serialVersionUID = -848500041308510098L;

    private Integer id;

    private String name;

}
