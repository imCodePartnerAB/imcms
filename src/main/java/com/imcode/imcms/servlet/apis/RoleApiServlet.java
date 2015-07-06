package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.util.JSONUtils;
import imcode.server.Imcms;
import imcode.server.user.RoleDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 14.04.2015.
 */
public class RoleApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> answer = new HashMap<>();
        answer.putAll(
                Stream.of(Imcms.getServices()
                        .getImcmsAuthenticatorAndUserAndRoleMapper()
                        .getAllRoles())
                        .collect(Collectors.toMap(RoleDomainObject::getName, b -> b.getId().getRoleId()))
        );

        JSONUtils.defaultJSONAnswer(resp, answer);
    }
}
