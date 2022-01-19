package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.domain.service.UserPropertyService;
import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.UserProperty;
import imcode.server.Imcms;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/user/properties")
public class UserPropertyController {

    private final UserPropertyService userPropertyService;
    private final UserRolesService userRolesService;

    public UserPropertyController(UserPropertyService userPropertyService, UserRolesService userRolesService) {
        this.userPropertyService = userPropertyService;
        this.userRolesService = userRolesService;
    }

    @GetMapping("/{id}")
    public List<UserProperty> getByUserId(@PathVariable Integer id) {
        if(!Imcms.getUser().isSuperAdmin()) checkRoles(id);
        return userPropertyService.getByUserId(id);
    }

    @PostMapping
    public void create(@RequestBody UserPropertyDTO userProperty) {
        if(!Imcms.getUser().isSuperAdmin()) checkRoles(userProperty.getUserId());
        userPropertyService.create(userProperty);
    }

    @PutMapping
    public UserProperty update(@RequestBody UserPropertyDTO userProperty) {
        if(!Imcms.getUser().isSuperAdmin()) checkRoles(userProperty.getUserId());
        return userPropertyService.update(userProperty);
    }

    @PostMapping("/update")
    public void update(@RequestBody HashMap<String, List<UserPropertyDTO>> data) {
        List<UserPropertyDTO> deletedProperties = data.get("deletedProperties");
        List<UserPropertyDTO> editedProperties = data.get("editedProperties");
        List<UserPropertyDTO> createdProperties = data.get("createdProperties");

        if(!Imcms.getUser().isSuperAdmin()) {
            Stream.concat(Stream.concat(deletedProperties.stream(), editedProperties.stream()), createdProperties.stream())
                    .map(UserPropertyDTO::getUserId).distinct().forEach(this::checkRoles);
        }

        userPropertyService.update(deletedProperties, editedProperties, createdProperties);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        if(!Imcms.getUser().isSuperAdmin()) checkRoles(userPropertyService.getById(id).getId());
        userPropertyService.deleteById(id);
    }

    private void checkRoles(int userId){
        if(userRolesService.getRoleIdsByUser(userId).stream().anyMatch(id -> Roles.SUPER_ADMIN.getId().equals(id))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot affect Superadmin properties");
        }
    }
}