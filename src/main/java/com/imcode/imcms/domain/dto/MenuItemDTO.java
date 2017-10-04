package com.imcode.imcms.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MenuItemDTO implements Serializable {

    private static final long serialVersionUID = 8297109006105427219L;

    private Integer id;

    private String title;

    private Integer documentId;

    private List<MenuItemDTO> children;

}
