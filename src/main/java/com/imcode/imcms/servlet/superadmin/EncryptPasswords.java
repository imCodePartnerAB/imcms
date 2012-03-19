package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.api.ContentManagementSystem;
import imcode.server.Imcms;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EncryptPasswords extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        if (!cms.getCurrentUser().isSuperAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().encryptUnencryptedUsersLoginPasswords();
    }
}
