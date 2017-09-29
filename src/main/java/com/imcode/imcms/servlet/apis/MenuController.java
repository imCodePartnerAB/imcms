package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.domain.dto.MenuElementDTO;
import com.imcode.imcms.domain.service.MenuElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

