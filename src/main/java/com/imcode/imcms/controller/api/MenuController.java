package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), Imcms.getUser().getLanguage());
    }

    @PostMapping
    @CheckAccess(AccessType.MENU)
    public MenuDTO saveMenu(@RequestBody MenuDTO menu) {
        return menuService.saveFrom(menu);
    }

}

