package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class MenuDTO implements Serializable {

    private Integer menuId;

    private Integer docId;

    private List<MenuItemDTO> menuItems;

}
