package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.persistence.entity.Meta.Permission;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 14.04.2015.
 */

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @RequestMapping
    protected Object getPermissionsList() {
        return Stream
                .of(Permission.values())
                .collect(
                        Collectors.toMap(
                                Permission::getId,
                                Permission::getName
                        )
                );
    }

}
