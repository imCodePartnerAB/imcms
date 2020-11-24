package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public MenuDTO getMenuItems(@ModelAttribute MenuDTO menu) {
        return menuService.getMenuDTO(
                menu.getDocId(),
                menu.getMenuIndex(),
                Imcms.getLanguage().getCode(),
                menu.isNested(),
                menu.getTypeSort()
        );
    }

    @PutMapping("/sorting")
    public List<MenuItemDTO> getSortedMenuItems(@RequestBody MenuDTO menuDTO) {
        return menuService.getSortedMenuItems(menuDTO, Imcms.getLanguage().getCode());
    }

    @PostMapping
    @CheckAccess(AccessType.MENU)
    public MenuDTO saveMenu(@RequestBody MenuDTO menu) {
        return menuService.saveFrom(menu);
    }

}

