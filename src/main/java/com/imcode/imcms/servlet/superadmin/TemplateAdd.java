package com.imcode.imcms.servlet.superadmin;

import com.imcode.util.MultipartHttpServletRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TemplateAdd extends HttpServlet {

    private static final String REQUEST_PARAMETER__FILE = "file";
    private static final String REQUEST_PARAMETER__OVERWRITE = "overwrite";
    private static final String REQUEST_PARAMETER__HIDDEN = "hidden";
    private static final String REQUEST_PARAMETER__NAME = "name";
    private static final String REQUEST_PARAMETER__ACTION = "action";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(req, res);
            return;
        }

        ServletOutputStream out = res.getOutputStream();
        String actionParam = req.getParameter(REQUEST_PARAMETER__ACTION);
        // Redirected here with bogus parameter, no-cache workaround
        if (actionParam != null && actionParam.equals("return")) {
            Utility.setDefaultHtmlContentType(res);

            List<String> vec = new ArrayList<>();
            vec.add("#buttonName#");
            vec.add("return");
            vec.add("#formAction#");
            vec.add("TemplateAdmin");
            vec.add("#formTarget#");
            vec.add("_top");
            out.write(imcref.getAdminTemplate("back_button.html", user, vec).getBytes("8859_1"));
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(req, res);
            return;
        }

        TemplateMapper templateMapper = Imcms.getServices().getTemplateMapper();
        PrintWriter out = res.getWriter();
        MultipartHttpServletRequest request = new MultipartHttpServletRequest(req);

        if (request.getParameter("cancel") != null) {
            res.sendRedirect("TemplateAdmin");
            return;
        }
        Utility.setDefaultHtmlContentType(res);

        String simpleName = request.getParameter(REQUEST_PARAMETER__NAME);

        if (simpleName == null || simpleName.equals("")) {
            String htmlStr = Utility.getAdminContents("template_upload_name_blank.jsp", request, res);
            out.print(htmlStr);
            return;
        }

        FileItem file = request.getParameterFileItem(REQUEST_PARAMETER__FILE);
        if (file == null || file.getSize() == 0) {
            String htmlStr = Utility.getAdminContents("template_upload_file_blank.jsp", request, res);
            out.print(htmlStr);
            return;
        }

        String filename = request.getParameterFileItem(REQUEST_PARAMETER__FILE).getName();
        File fn = new File(filename);
        boolean overwrite = request.getParameter(REQUEST_PARAMETER__OVERWRITE) != null;
        boolean isHidden = request.getParameter(REQUEST_PARAMETER__HIDDEN) != null;
        String htmlStr;

        int result = templateMapper.saveTemplate(simpleName, fn.getName(), file.getInputStream(), overwrite, isHidden);
        if (result == -2) {
            htmlStr = Utility.getAdminContents("template_upload_error.jsp", request, res);
        } else if (result == -1) {
            htmlStr = Utility.getAdminContents("template_upload_file_exists.jsp", request, res);
        } else {
            TemplateDomainObject template = templateMapper.getTemplateByName(simpleName);

            String[] templateGroupIdStrings = request.getParameterValues("templategroup");
            if (templateGroupIdStrings != null) {
                for (String templateGroupIdString : templateGroupIdStrings) {
                    int templateGroupId = Integer.parseInt(templateGroupIdString);
                    TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(templateGroupId);
                    templateMapper.removeTemplateFromGroup(template, templateGroup);
                    templateMapper.addTemplateToGroup(template, templateGroup);
                }
            }

            htmlStr = Utility.getAdminContents("template_upload_done.jsp", request, res);
        }
        out.print(htmlStr);
    }
}
