package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RestrictedPermissionDTO extends RestrictedPermission implements Serializable {

    private static final long serialVersionUID = -3568020825278050072L;

    private Permission permission;

    private boolean editText;

    private boolean editMenu;

    private boolean editImage;

    private boolean editLoop;

    private boolean editDocInfo;

    public RestrictedPermissionDTO(RestrictedPermission from) {
        super(from);
    }
}
