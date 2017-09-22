package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.mapping.dto.MenuElementDTO;
import com.imcode.imcms.service.MenuElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuElementService menuElementService;

    @Autowired
    public MenuController(MenuElementService menuElementService) {
        this.menuElementService = menuElementService;
    }

    @GetMapping(params = {"docId", "menuId"})
    public List<MenuElementDTO> getMenuItems(@RequestParam("menuId") int menuNo,
                                             @RequestParam("docId") int documentId) {
        return menuElementService.getMenuElements(menuNo, documentId);
    }

}

