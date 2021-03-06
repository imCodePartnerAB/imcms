package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.RolePermissions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RolePermissionsDTO extends RolePermissions {
    private static final long serialVersionUID = 7974624040883389434L;

    public RolePermissionsDTO(RolePermissions from) {
        super(from);
    }
}
