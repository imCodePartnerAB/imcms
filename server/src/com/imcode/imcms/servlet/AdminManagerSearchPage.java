package com.imcode.imcms.servlet;

import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminManagerSearchPage extends SearchDocumentsPage {

    public void forward(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        putInSessionAndForwardToPath("/imcms/" + user.getLanguageIso639_2() + "/jsp/admin/admin_manager_search.jsp",request, response);
    }

}
