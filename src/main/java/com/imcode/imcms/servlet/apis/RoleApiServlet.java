package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.util.JSONUtils;
import imcode.server.user.RoleId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shadowgun on 14.04.2015.
 */
public class RoleApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> answer = new HashMap<>();
        answer.put("Superadmin", RoleId.SUPERADMIN_ID);
        answer.put("Useradmin", RoleId.USERADMIN_ID);
        answer.put("User", RoleId.USERS_ID);
        JSONUtils.defaultJSONAnswer(resp, answer);
    }
}
