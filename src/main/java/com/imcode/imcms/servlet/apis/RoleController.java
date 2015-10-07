package com.imcode.imcms.servlet.apis;

import imcode.server.Imcms;
import imcode.server.user.RoleDomainObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 14.04.2015.
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @RequestMapping
    protected Object getRolesList() {
        return Stream.of(Imcms.getServices()
                .getImcmsAuthenticatorAndUserAndRoleMapper()
                .getAllRoles())
                .collect(Collectors.toMap(RoleDomainObject::getName, b -> b.getId().getRoleId()));
    }
}
