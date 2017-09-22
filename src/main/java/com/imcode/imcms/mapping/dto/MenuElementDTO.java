package com.imcode.imcms.mapping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class MenuElementDTO implements Serializable {

    private static final long serialVersionUID = -3232121568216145805L;

    private Integer id;

    private String title;

    private List<MenuElementDTO> children;

}
