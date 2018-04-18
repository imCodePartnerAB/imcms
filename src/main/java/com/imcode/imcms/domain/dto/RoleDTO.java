package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RoleDTO extends Role {

    private static final long serialVersionUID = -6429901776462985054L;

    private Integer id;

    private String name;

    public RoleDTO(Role from) {
        super(from);
    }
}
