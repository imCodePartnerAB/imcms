package com.imcode.imcms.servlet.superadmin;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import com.imcode.imcms.servlet.superadmin.Administrator;

public class AdminError extends Administrator {

    public AdminError(HttpServletRequest req, HttpServletResponse res, String header, String msg) throws IOException {

        Vector tags = new Vector();
        Vector data = new Vector();
        tags.add("ERROR_HEADER");
        tags.add("ERROR_MESSAGE");
        data.add(header);
        data.add(msg);

        String fileName = "AdminError.htm";

        // Lets get the path to the admin templates folder
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        File templateLib = super.getAdminTemplateFolder(imcref, user);

        HtmlGenerator htmlObj = new HtmlGenerator(templateLib, fileName);
        String html = htmlObj.createHtmlString(tags, data, req);
        res.setContentType("text/html");
        htmlObj.sendToBrowser(req, res, html);
        return;
    }

    public void log(String str) {
        System.err.println("AdminError: " + str);
    }

}
