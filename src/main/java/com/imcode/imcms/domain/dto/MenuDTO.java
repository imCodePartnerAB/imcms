package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MenuDTO {

    private Integer menuId;

    private Integer docId;

    private List<MenuItemDTO> menuItems;

}
