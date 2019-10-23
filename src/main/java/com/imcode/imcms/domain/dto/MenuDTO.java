package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class MenuDTO implements Documentable, Serializable {

    private static final long serialVersionUID = 2486639868480793796L;

    private Integer menuIndex;

    private Integer docId;

    private List<MenuItemDTO> menuItems;

    private boolean nested;

    private String typeSort;
}
