package com.imcode.imcms.servlet;

import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.servlet.superadmin.AdminManager;

public class AdminManagerSearchPage extends SearchDocumentsPage {

    private AdminManager.AdminManagerPage adminManagerPage;

    public AdminManagerSearchPage( AdminManager.AdminManagerPage adminManagerPage ) {
        this.adminManagerPage = adminManagerPage;
    }

    public String getPath(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return "/imcms/" + user.getLanguageIso639_2() + "/jsp/admin/admin_manager.jsp";
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        adminManagerPage.putInRequest(request);
        super.forward(request, response);
    }
}
