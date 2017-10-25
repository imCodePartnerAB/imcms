package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = -6429901776462985054L;

    private Integer id;

    private String name;

    private Permission permission;

    public RoleDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public enum Permission {
        VIEW,
        EDIT,
        RESTRICTED_1,
        RESTRICTED_2
    }

}
