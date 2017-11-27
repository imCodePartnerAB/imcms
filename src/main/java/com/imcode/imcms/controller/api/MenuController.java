package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.api.MenuService;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
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
        return menuService.getMenuItemsOf(menu.getMenuIndex(), menu.getDocId());
    }

    @PostMapping
    public MenuDTO saveMenu(@RequestBody MenuDTO menu) {
        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }

        return menuService.saveFrom(menu);
    }

}

