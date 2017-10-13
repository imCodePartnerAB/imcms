package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.api.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDTO> getRoles() {
        return roleService.getAll();
    }

}
