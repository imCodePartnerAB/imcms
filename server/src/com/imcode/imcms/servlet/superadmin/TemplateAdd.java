package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateAdd extends HttpServlet {
    private static final String REQUEST_PARAMETER__ACTION = "action";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(req, res);
            return;
        }

        ServletOutputStream out = res.getOutputStream();

        // Redirected here with bogus parameter, no-cache workaround
        if (req.getParameter(REQUEST_PARAMETER__ACTION) != null) {
            if (req.getParameter(REQUEST_PARAMETER__ACTION).equals("return")) {
                Utility.setDefaultHtmlContentType(res);

                List vec = new ArrayList();
                vec.add("#buttonName#");
                vec.add("return");
                vec.add("#formAction#");
                vec.add("TemplateAdmin");
                vec.add("#formTarget#");
                vec.add("_top");
                out.write(imcref.getAdminTemplate("back_button.html", user, vec).getBytes(Imcms.DEFAULT_ENCODING));
            }
        }

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }
}
