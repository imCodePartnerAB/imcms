package com.imcode.imcms.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuItemDTO implements Serializable {

    private static final long serialVersionUID = 8297109006105427219L;

    private Integer id;

    private String title;

    private Integer documentId;

    private List<MenuItemDTO> children;

}
