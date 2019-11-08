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
    public List<MenuItemDTO> getMenuItems(@ModelAttribute MenuDTO menu) {
        return menuService.getMenuItems(
                menu.getDocId(),
                menu.getMenuIndex(),
                Imcms.getUser().getLanguage(),
                menu.isNested(),
                menu.getTypeSort()
        );
    }

    @PutMapping("/sorting")
    public List<MenuItemDTO> getSortedMenuItems(@RequestBody MenuDTO menuDTO) {
        return menuService.getSortedMenuItems(menuDTO);
    }

    @PostMapping
    @CheckAccess(AccessType.MENU)
    public MenuDTO saveMenu(@RequestBody MenuDTO menu) {
        return menuService.saveFrom(menu);
    }

}

