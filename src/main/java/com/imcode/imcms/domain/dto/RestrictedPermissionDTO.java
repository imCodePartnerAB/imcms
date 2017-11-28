package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.RestrictedPermission;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RestrictedPermissionDTO extends RestrictedPermission implements Serializable {

    private static final long serialVersionUID = -3568020825278050072L;

    private boolean editText;

    private boolean editMenu;

    private boolean editImage;

    private boolean editLoop;

    private boolean editDocInfo;

    public RestrictedPermissionDTO(RestrictedPermission from) {
        super(from);
    }
}
