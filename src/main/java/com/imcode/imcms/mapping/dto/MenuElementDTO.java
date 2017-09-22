package com.imcode.imcms.mapping.dto;

import java.io.Serializable;
import java.util.List;

public class MenuElementDTO extends AbstractIdDTO implements Serializable {

    private static final long serialVersionUID = -3232121568216145805L;

    private String title;
    private List<MenuElementDTO> children;

    public MenuElementDTO(Integer id, String title, List<MenuElementDTO> children) {
        super(id);
        this.title = title;
        this.children = children;
    }

    public String getTitle() {
        return title;
    }

    public List<MenuElementDTO> getChildren() {
        return children;
    }

}
