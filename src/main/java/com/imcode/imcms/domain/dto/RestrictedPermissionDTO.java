package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RestrictedPermissionDTO implements Serializable {

    private static final long serialVersionUID = -3568020825278050072L;

    private int id;

    private boolean editText;

    private boolean editMenu;

    private boolean editImage;

    private boolean editLoop;

    private boolean editDocumentInfo;

}
