package com.imcode.imcms.servlet;

import com.imcode.imcms.servlet.superadmin.AdminManager;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminManagerSearchPage extends SearchDocumentsPage {

    private AdminManager.AdminManagerPage adminManagerPage;

    public AdminManagerSearchPage( AdminManager.AdminManagerPage adminManagerPage ) {
        this.adminManagerPage = adminManagerPage;
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        adminManagerPage.putInRequest(request);
        putInSessionAndForwardToPath("/imcms/" + user.getLanguageIso639_2() + "/jsp/admin/admin_manager.jsp",request, response);
    }

}
