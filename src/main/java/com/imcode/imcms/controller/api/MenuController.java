package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.api.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping(params = {"menuId", "docId"})
    public List<MenuItemDTO> getMenuItems(@RequestParam("menuId") int menuNo,
                                          @RequestParam("docId") int documentId) {
        return menuService.getMenuItemsOf(menuNo, documentId);
    }

}

