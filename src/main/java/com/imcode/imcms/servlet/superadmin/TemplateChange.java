package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class TemplateChange extends HttpServlet {

    private final static LocalizedMessage ERROR__TEMPLATE_NAME_TAKEN = new LocalizedMessage("error/template_with_name_exists");
    private final static LocalizedMessage ERROR_AVAILABILITY_NOT_CHANGED = new LocalizedMessage("error/template_availability_not_changed");

    private HttpServletRequest request;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(request);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(request, response);
            return;
        }

        Utility.setDefaultHtmlContentType(response);

        TemplateMapper templateMapper = imcref.getTemplateMapper();
        String htmlStr = null;
        String lang = request.getParameter("language");
        setRequestForChecking(request);
        if (checkNotNull("cancel")) {
            response.sendRedirect("TemplateAdmin");
        } else if (checkNotNull("assign")) {
            htmlStr = addTemplatesToGroup(request, templateMapper, lang, response);
        } else if (checkNotNull("deassign")) {
            htmlStr = removeTemplatesFromGroup(request, templateMapper, lang, response);
        } else if (checkNotNull("show_assigned")) {
            int templateGroupId = Integer.parseInt(request.getParameter("templategroup"));
            TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(templateGroupId);
            htmlStr = TemplateAdmin.createAssignTemplatesToGroupDialog(templateMapper,
                    templateGroup, lang, request, response);
        } else if (checkNotNull("group_delete_check")) {
            htmlStr = deleteTemplateGroupAfterCheckingUsage(request, response);
        } else if (checkNotNull("group_delete")) {
            deleteTemplateGroup(request, imcref);
            htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog(templateMapper, request, response);
        } else if (checkNotNull("group_delete_cancel")) {
            htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog(templateMapper, request, response);
        } else if (checkNotNull("group_add")) {
            htmlStr = addTemplateGroup(request, response, imcref);
        } else if (checkNotNull("group_rename")) {
            htmlStr = renameTemplateGroup(request, imcref, lang, response);
        } else if (checkNotNull("show_doc")) {
            htmlStr = showDocument(request, response, imcref, lang, user);
        }

        if (null != htmlStr) {
            response.getOutputStream().print(htmlStr);
        }
    }

    private void setRequestForChecking(HttpServletRequest request) {
        this.request = request;
    }

    private boolean checkNotNull(String parameter) {
        if (null == this.request) {
            throw new NullPointerException("Set request by setRequestByChecking() before use this");
        }
        return this.request.getParameter(parameter) != null;
    }

    private String addTemplateGroup(HttpServletRequest request, HttpServletResponse response, ImcmsServices imcref) throws ServletException, IOException {
        String htmlStr;
        String name = request.getParameter("name");
        if (StringUtils.isEmpty(name)) {
            htmlStr = createAddNameEmptyErrorDialog(request, response);
        } else {
            TemplateGroupDomainObject templateGroup = imcref.getTemplateMapper().getTemplateGroupByName(name);
            if (null != templateGroup) {
                htmlStr = createTemplateGroupExistsErrorDialog(request, response);
            } else {
                imcref.getTemplateMapper().createTemplateGroup(name);
                htmlStr = TemplateAdmin.createAddGroupDialog(request, response);
            }
        }
        return htmlStr;
    }

    private String addTemplatesToGroup(HttpServletRequest req, TemplateMapper templateMapper, String lang,
                                       HttpServletResponse res) throws IOException, ServletException {
        String groupIdParameter = req.getParameter("group_id");
        String[] templateNames = req.getParameterValues("unassigned");
        TemplateGroupDomainObject templateGroup = null;
        if (null != groupIdParameter && null != templateNames) {
            int grp_id = Integer.parseInt(groupIdParameter);
            templateGroup = templateMapper.getTemplateGroupById(grp_id);
            for (String templateName : templateNames) {
                TemplateDomainObject templateToAssign = templateMapper.getTemplateByName(templateName);
                templateMapper.addTemplateToGroup(templateToAssign, templateGroup);
            }
        }
        return TemplateAdmin.createAssignTemplatesToGroupDialog(templateMapper, templateGroup, lang, req, res);
    }

    private String removeTemplatesFromGroup(HttpServletRequest req, TemplateMapper templateMapper, String lang,
                                            HttpServletResponse res) throws IOException, ServletException {
        String groupIdParameter = req.getParameter("group_id");
        String[] templateIds = req.getParameterValues("assigned");
        TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(Integer.parseInt(groupIdParameter));
        if (null != templateGroup && null != templateIds) {
            for (String templateName : templateIds) {
                TemplateDomainObject templateToUnassign = templateMapper.getTemplateByName(templateName);
                templateMapper.removeTemplateFromGroup(templateToUnassign, templateGroup);
            }
        }
        return TemplateAdmin.createAssignTemplatesToGroupDialog(templateMapper, templateGroup, lang, req, res);
    }

    private String createAddNameEmptyErrorDialog(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return Utility.getAdminContents("templategroup_add_name_blank.jsp", request, response);
    }

    private String createRenameNameEmptyErrorDialog(String lang, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("language", lang);
        return Utility.getAdminContents("template_rename_name_blank.jsp", request, response);
    }

    private String createTemplateGroupExistsErrorDialog(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return Utility.getAdminContents("templategroup_add_exists.jsp", request, response);
    }

    //Not used due to IMCMS-634 P1-RB4-security: Arbitrary file upload vulnerability in TemplateAdd
    @Deprecated
    private void deleteTemplate(HttpServletRequest req, ImcmsServices imcref) {
        TemplateMapper templateMapper = imcref.getTemplateMapper();
        String new_temp_id = req.getParameter("new_template");
        TemplateDomainObject newTemplate = templateMapper.getTemplateByName(new_temp_id);
        String template_id = req.getParameter("template");
        TemplateDomainObject template = templateMapper.getTemplateByName(template_id);

        templateMapper.replaceAllUsagesOfTemplate(template, newTemplate);
        templateMapper.deleteTemplate(template);
    }

    //Not used due to IMCMS-634 P1-RB4-security: Arbitrary file upload vulnerability in TemplateAdd
    @Deprecated
    private String deleteTemplateAfterCheckingUsage(HttpServletRequest req, ImcmsServices imcref, String lang,
                                                    UserDomainObject user, HttpServletResponse res) throws IOException, ServletException {
        TemplateMapper templateMapper = imcref.getTemplateMapper();
        String template_id = req.getParameter("template");
        TemplateDomainObject template = templateMapper.getTemplateByName(template_id);
        DocumentDomainObject[] documentsUsingTemplate = templateMapper.getDocumentsUsingTemplate(template);

        if (documentsUsingTemplate.length > 0) {
            return TemplateAdmin.createDeleteTemplateInUseWarningDialog(lang, imcref, template, req, res, templateMapper);
        } else {
            templateMapper.deleteTemplate(template);
            return TemplateAdmin.createDeleteTemplateDialog(templateMapper, user, lang, req, res);
        }
    }

    private void deleteTemplateGroup(HttpServletRequest req, ImcmsServices imcref) {
        int grp_id = Integer.parseInt(req.getParameter("templategroup"));
        imcref.getTemplateMapper().deleteTemplateGroup(grp_id);
    }

    private String deleteTemplateGroupAfterCheckingUsage(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int templateGroupId = Integer.parseInt(req.getParameter("templategroup"));
        TemplateMapper templateMapper = Imcms.getServices().getTemplateMapper();
        TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(templateGroupId);
        Collection<TemplateDomainObject> templatesInGroup = templateMapper.getTemplatesInGroup(templateGroup);
        boolean existsDocumentUsingTemplateInTemplateGroup = false;
        for (TemplateDomainObject aTemplatesInGroup : templatesInGroup) {
            if (templateMapper.getDocumentsUsingTemplate(aTemplatesInGroup).length > 0) {
                existsDocumentUsingTemplateInTemplateGroup = true;
            }
        }
        String htmlStr;

        if (!templatesInGroup.isEmpty()) {
            htmlStr = TemplateAdmin.createDeleteNonEmptyTemplateGroupWarningDialog(templatesInGroup, templateGroupId, req, res);
            if (existsDocumentUsingTemplateInTemplateGroup) {
                htmlStr = TemplateAdmin.createDocumentsAssignedToTemplateInTemplateGroupWarningDialog(templatesInGroup, templateGroupId, req, res);
            }
        } else {
            templateMapper.deleteTemplateGroup(templateGroupId);
            htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog(templateMapper, req, res);
        }
        return htmlStr;
    }

    //Not used due to IMCMS-634 P1-RB4-security: Arbitrary file upload vulnerability in TemplateAdd
    @Deprecated
    private void downloadTemplate(HttpServletRequest req, ImcmsServices imcref, HttpServletResponse res
    ) throws IOException {
        String template_id = req.getParameter("template");
        TemplateMapper templateMapper = imcref.getTemplateMapper();
        String filename = Optional.ofNullable(templateMapper.getTemplateByName(template_id).getFileName()).orElse("");
        byte[] file = templateMapper.getTemplateData(template_id).getBytes();
        res.setContentType("application/octet-stream; name=\"" + filename + "\"");
        res.setContentLength(file.length);
        res.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\";");
        ServletOutputStream out = res.getOutputStream();
        out.write(file);
        out.flush();
    }

    //Not used due to IMCMS-634 P1-RB4-security: Arbitrary file upload vulnerability in TemplateAdd
    @Deprecated
    private String renameTemplate(HttpServletRequest req, TemplateMapper templateMapper, String lang,
                                  UserDomainObject user, HttpServletResponse res) throws IOException, ServletException {
        String htmlStr;
        String template_id = req.getParameter("template");
        TemplateDomainObject template = templateMapper.getTemplateByName(template_id);
        String newNameForTemplate = req.getParameter("name");
        if (StringUtils.isEmpty(newNameForTemplate)) {
            htmlStr = createRenameNameEmptyErrorDialog(lang, req, res);
        } else {
            LocalizedMessage error = null;
            if (!templateMapper.renameTemplate(template.getName(), newNameForTemplate)) {
                error = ERROR__TEMPLATE_NAME_TAKEN;
            }
            htmlStr = TemplateAdmin.createRenameTemplateDialog(lang, templateMapper, req, res, user, error);
        }
        return htmlStr;
    }

    //Not used due to IMCMS-634 P1-RB4-security: Arbitrary file upload vulnerability in TemplateAdd
    @Deprecated
    private String changeAvailabilityTemplate(HttpServletRequest req, TemplateMapper templateMapper, String lang,
                                              UserDomainObject user, HttpServletResponse res) throws IOException, ServletException {
        String htmlStr;
        String template_id = (req.getParameter("template")).replace("(Hidden)", "");
        TemplateDomainObject template = templateMapper.getTemplateByName(template_id);
        boolean isHidden = req.getParameter("hidden") != null;
        LocalizedMessage error = null;
        if (!templateMapper.updateAvailability(template.getName(), isHidden)) {
            error = ERROR_AVAILABILITY_NOT_CHANGED;
        }
        htmlStr = TemplateAdmin.createChangeAvailabilityTemplateDialog(lang, templateMapper, req, res, user, error);
        return htmlStr;
    }

    private String renameTemplateGroup(HttpServletRequest req, ImcmsServices imcref, String lang,
                                       HttpServletResponse res) throws ServletException, IOException {
        String htmlStr;
        int grp_id = Integer.parseInt(req.getParameter("templategroup"));
        String name = req.getParameter("name");
        if (StringUtils.isEmpty(name)) {
            htmlStr = createRenameNameEmptyErrorDialog(lang, req, res);
        } else {
            TemplateMapper templateMapper = imcref.getTemplateMapper();
            TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(grp_id);
            templateMapper.renameTemplateGroup(templateGroup, name);
            htmlStr = TemplateAdmin.createRenameTemplateGroupDialog(templateMapper, req, res);
        }
        return htmlStr;
    }

    private String showDocument(HttpServletRequest req, HttpServletResponse res, ImcmsServices imcref,
                                String lang, UserDomainObject user) throws IOException {
        String meta_id = req.getParameter("templates_doc");
        String htmlStr = "AdminDoc?meta_id=" + meta_id;
        if (meta_id != null) {
            res.sendRedirect("AdminDoc?meta_id=" + meta_id);
        }

        return htmlStr;
    }

}
