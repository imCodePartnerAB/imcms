package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.MenuNotExistException;
import com.imcode.imcms.domain.service.api.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuItemDTO> getMenuItems(@ModelAttribute MenuDTO menu) {
        return menuService.getMenuItemsOf(menu.getMenuId(), menu.getDocId());
    }

    @PostMapping
    public void saveMenuItems(@RequestBody MenuDTO menu) {
        menuService.saveMenuItems(menu);
    }

    @ExceptionHandler(MenuNotExistException.class)
    public List<MenuItemDTO> menuNotExistExceptionResponse(MenuNotExistException e) {
        logger.info(e.getMessage());
        return new ArrayList<>();
    }

}

