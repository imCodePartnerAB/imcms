package com.imcode.imcms.servlet.apis;

import imcode.server.document.DocumentPermissionSetTypeDomainObject;
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
                .of(DocumentPermissionSetTypeDomainObject.values())
                .collect(
                        Collectors.toMap(
                                DocumentPermissionSetTypeDomainObject::getId,
                                DocumentPermissionSetTypeDomainObject::getName
                        )
                );
    }

}
