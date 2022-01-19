package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.security.AccessRoleType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/roles")
class RoleController {

    private final RoleService roleService;

    RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<Role> getRoles() {
        return roleService.getAll();
    }

    @GetMapping("/current-user")
    public Set<Integer> getRoleIds(){
        return Imcms.getUser().getRoleIds();
    }

    @PostMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public Role createNewRole(@RequestBody RoleDTO role) {
        return roleService.saveNewRole(role);
    }

    @PatchMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public Role saveRole(@RequestBody RoleDTO role) {
        return roleService.save(role);
    }

    @DeleteMapping("/{roleId}")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public void deleteRole(@PathVariable Integer roleId) {
        roleService.delete(roleId);
    }

}
