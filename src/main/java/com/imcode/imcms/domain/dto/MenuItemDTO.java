package com.imcode.imcms.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MenuItemDTO implements Serializable {

    private static final long serialVersionUID = 8297109006105427219L;

    private String title;

    private Integer documentId;

    private String target;

    private String link;

    private List<MenuItemDTO> children = new ArrayList<>();

}
