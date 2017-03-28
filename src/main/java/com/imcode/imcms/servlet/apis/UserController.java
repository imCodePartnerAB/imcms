package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.server.document.DocumentDomainObject;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
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
    public Object getUsers(@RequestParam(value = "current", required = false) Boolean current,
                           HttpServletRequest request) {

        current = BooleanUtils.toBoolean(current);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);

        User[] allUsers = (current)
                ? new User[]{cms.getCurrentUser()}
                : cms.getUserService().getAllUsers();

        return Stream.of(allUsers)
                .map(user -> {
                    final Map<String, Object> userData = new HashMap<>();
                    userData.put("loginName", user.getLoginName());
                    userData.put("id", user.getId());

                    return userData;
                })
                .collect(Collectors.toList());
    }
}
