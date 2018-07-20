package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admins")
    public List<UserDTO> getAllAdmins() {
        return userService.getAdminUsers();
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAllActiveUsers();
    }

    @PatchMapping
    public void updateUser(@RequestBody UserDTO updateMe) {
        userService.updateUser(updateMe);
    }

}
