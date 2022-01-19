package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.security.AccessRoleType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
class UserController {

    private final UserService userService;
    private final UserRolesService userRolesService;

    UserController(UserService userService, UserRolesService userRolesService) {
        this.userService = userService;
        this.userRolesService = userRolesService;
    }

    @GetMapping("/admins")
    public List<UserDTO> getAllAdmins() {
        return userService.getAdminUsers();
    }

    @GetMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public List<UserDTO> getAll() {
        List<UserDTO> userDTOS = userService.getAllActiveUsers();

        if(!Imcms.getUser().isSuperAdmin()) userDTOS = filterSuperAdmins(userDTOS);

        return userDTOS;
    }

    @PatchMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public void updateUser(@RequestBody UserDTO updateMe) {
        if(!Imcms.getUser().isSuperAdmin() && userRolesService.getRoleIdsByUser(updateMe.getId()).stream()
                .anyMatch(id -> (Roles.SUPER_ADMIN.getId().equals(id)))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to update Superadmin");
        }

        userService.updateUser(updateMe);
    }

    @GetMapping("/search")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public List<UserDTO> searchUsers(@RequestParam String term,
                                     @RequestParam boolean includeInactive,
                                     @RequestParam(value = "roleIds[]", required = false) Set<Integer> roleIds) {
        List<UserDTO> userDTOS = userService.searchUsers(term, roleIds, includeInactive);

        if(!Imcms.getUser().isSuperAdmin()) userDTOS = filterSuperAdmins(userDTOS);

        return userDTOS;
    }

    private List<UserDTO> filterSuperAdmins(List<UserDTO> userDTOS){
        final Set<Integer> usersByRole = userRolesService.getUserIdsByRole(Roles.SUPER_ADMIN.getId());
        return userDTOS.stream().filter(user -> !usersByRole.contains(user.getId())).collect(Collectors.toList());
    }

}
