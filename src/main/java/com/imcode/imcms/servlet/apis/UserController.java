package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.server.document.DocumentDomainObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Realise API for working with  {@link DocumentDomainObject}
 * Created by Serhii from Ubrainians for Imcode
 * on 12.08.16.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping(method = RequestMethod.GET)
    public Object getUsers(HttpServletRequest request) {
        User[] allUsers = ContentManagementSystem.fromRequest(request).getUserService().getAllUsers();

        return Stream.of(allUsers)
                .map(user -> new HashMap<String, Object>() {{
                    put("loginName", user.getLoginName());
                    put("id", user.getId());
                }})
                .collect(Collectors.toList());
    }
}
