package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminError extends Administrator {

    public AdminError(HttpServletRequest req, HttpServletResponse res, String header, String msg) throws IOException {

        List tagsAndData = new ArrayList();
        tagsAndData.add("#ERROR_HEADER#");
        tagsAndData.add(header);
        tagsAndData.add("#ERROR_MESSAGE#");
        tagsAndData.add(msg);

        String fileName = "AdminError.htm";

        // Lets get the path to the admin templates folder
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );

        String html = imcref.getAdminTemplate( fileName, user, tagsAndData );
        Utility.setDefaultHtmlContentType( res );
        res.getWriter().println(html);
    }

}
