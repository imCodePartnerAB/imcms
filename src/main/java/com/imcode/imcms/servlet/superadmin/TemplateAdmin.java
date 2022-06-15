package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;
import imcode.util.Utility;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

public class TemplateAdmin extends HttpServlet {
    private static final String TEMPLATE_ADMIN = "template_admin.jsp";
    private static final String ADMIN_TEMPLATE_DELETE = "templategroup_delete.jsp";
    private static final String ADMIN_TEMPLATE_ADD = "templategroup_add.jsp";
    private static final String ADMIN_TEMPLATE_EDIT = "template_edit.jsp";
    private static final String ADMIN_TEMPLATE_RENAME = "template_rename.jsp";
    private static final String ADMIN_TEMPLATE_AVALIABILITY = "template_availability.jsp";
    private static final String TEMPLATE_DELETE = "template_delete.jsp";
    private static final String TEMPLATE_DEMO_UPLOAD = "templatedemo_upload.jsp";
    private static final String TEMPLATE_GROUP_RENAME = "templategroup_rename.jsp";
    private static final String TEMPLATE_ASSIGN = "template_assign.jsp";
    private static final String TEMPLATE_DELETE_WARNING = "template_delete_warning.jsp";
    private static final String TEMPLATE_GROUP_DELETE_WARNING = "templategroup_delete_warning.jsp";
    private static final String TEMPLATE_GROUP_DELETE_DOCUMENTS_ASSIGNED_WARNING = "templategroup_delete_documents_assigned_warning.jsp";

    static String createDeleteTemplateGroupDialog(TemplateMapper templateMapper, HttpServletRequest request,
                                                  HttpServletResponse response) throws ServletException, IOException {
        createTemplateGroupsList(templateMapper, request);
        return Utility.getAdminContents(ADMIN_TEMPLATE_DELETE, request, response);
    }

    private static void createTemplateGroupsList(TemplateMapper templateMapper, HttpServletRequest request) {
        final String temps = templateMapper.createHtmlOptionListOfTemplateGroups(null);
        request.setAttribute("templategroups", temps);
    }

    static String createAddGroupDialog(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return Utility.getAdminContents(ADMIN_TEMPLATE_ADD, request, response);
    }

    private static void createShortTemplateDialog(String lang, HttpServletRequest request,
                                                  TemplateMapper templateMapper) {
        List<TemplateDomainObject> templates = Imcms.getServices().getTemplateMapper().getAllTemplates();
        String temps = templateMapper.createHtmlOptionListOfTemplates(templates, null);
        request.setAttribute("templates", temps);
        langTag(lang, request);
    }

    static String createRenameTemplateDialog(String lang, TemplateMapper templateMapper,
                                             HttpServletRequest request, HttpServletResponse response,
                                             UserDomainObject user, LocalizedMessage error) throws IOException, ServletException {
        createStandardTemplateDialog(lang, templateMapper, user, error, request);
        return Utility.getAdminContents(ADMIN_TEMPLATE_RENAME, request, response);
    }

    static String createChangeAvailabilityTemplateDialog(String lang, TemplateMapper templateMapper,
                                                         HttpServletRequest request, HttpServletResponse response,
                                                         UserDomainObject user, LocalizedMessage error) throws IOException, ServletException {
        createStandardTemplateDialog(lang, templateMapper, user, error, request);
        return Utility.getAdminContents(ADMIN_TEMPLATE_AVALIABILITY, request, response);
    }

    private static void createStandardTemplateDialog(String lang, TemplateMapper templateMapper,
                                                     UserDomainObject user, LocalizedMessage error,
                                                     HttpServletRequest request) {
        langTag(lang, request);
        request.setAttribute("templates", templateMapper.createHtmlOptionListOfTemplates(templateMapper.getAllTemplates(), null));
        request.setAttribute("error", null == error ? "" : error.toLocalizedString(user));
    }

    static String createDeleteTemplateDialog(TemplateMapper templateMapper, UserDomainObject user, String lang,
                                             HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String templatesList = templateMapper.createHtmlOptionListOfTemplatesWithDocumentCount(user);
        request.setAttribute("templates", templatesList);
        langTag(lang, request);
        return Utility.getAdminContents(TEMPLATE_DELETE, request, response);
    }

    static String createRenameTemplateGroupDialog(TemplateMapper templateMapper, HttpServletRequest request,
                                                  HttpServletResponse response) throws ServletException, IOException {
        createTemplateGroupsList(templateMapper, request);
        return Utility.getAdminContents(TEMPLATE_GROUP_RENAME, request, response);
    }

    static String createAssignTemplatesToGroupDialog(TemplateMapper templateMapper, TemplateGroupDomainObject currentTemplateGroup, String language,
                                                     HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String htmlOptionListOfTemplateGroups = templateMapper.createHtmlOptionListOfTemplateGroups(currentTemplateGroup);

        request.setAttribute("templategroups", htmlOptionListOfTemplateGroups);
        if (currentTemplateGroup == null) {
            request.setAttribute("assigned", "");
            request.setAttribute("unassigned", "");
            request.setAttribute("group", "");
            request.setAttribute("group_id", "");
        } else {
            Collection<TemplateDomainObject> templatesInGroup = templateMapper.getTemplatesInGroup(currentTemplateGroup);
            List<TemplateDomainObject> templatesNotInGroup = templateMapper.getTemplatesNotInGroup(currentTemplateGroup);
            String htmlOptionListOfTemplatesInSelectedGroup = templateMapper.createHtmlOptionListOfTemplates(
                    templatesInGroup, null);
            String htmlOptionListOfTemplatesNotInSelectedGroup = templateMapper.createHtmlOptionListOfTemplates(
                    templatesNotInGroup, null);
            request.setAttribute("assigned", htmlOptionListOfTemplatesInSelectedGroup);
            request.setAttribute("unassigned", htmlOptionListOfTemplatesNotInSelectedGroup);
            request.setAttribute("group", currentTemplateGroup.getName());
            request.setAttribute("group_id", currentTemplateGroup.getId());
        }
        langTag(language, request);
        return Utility.getAdminContents(TEMPLATE_ASSIGN, request, response);
    }

    static String createHtmlOptionListOfDocumentsUsingTemplate(ImcmsServices imcref, TemplateDomainObject template) {
        DocumentDomainObject[] documents = imcref.getTemplateMapper().getDocumentsUsingTemplate(template);
        StringBuilder htmlOptionList = new StringBuilder();
        for (DocumentDomainObject document : documents) {
            final int metaId = document.getId();

            String[] pd = {"&", "&amp;", "<", "&lt;", ">", "&gt;", "\"", "&quot;"};
            String headline = document.getHeadline();
            headline = StringUtils.abbreviate(headline, 60);
            headline = Parser.parseDoc(headline, pd);
            htmlOptionList.append("<option value=\"").append(metaId).append("\">[").append(metaId).append("] ")
                    .append(headline).append("</option>");
        }
        return htmlOptionList.toString();
    }

    static String createDeleteTemplateInUseWarningDialog(String lang, ImcmsServices imcref,
                                                         TemplateDomainObject template, HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         TemplateMapper templateMapper) throws IOException, ServletException {
        langTag(lang, request);
        request.setAttribute("template", template.getName());
        request.setAttribute("docs", createHtmlOptionListOfDocumentsUsingTemplate(imcref, template));
        request.setAttribute("templates", templateMapper.createHtmlOptionListOfTemplates(
                templateMapper.getAllTemplatesExceptOne(template), null
        ));

        return Utility.getAdminContents(TEMPLATE_DELETE_WARNING, request, response);
    }

    static String createDeleteNonEmptyTemplateGroupWarningDialog(Iterable<TemplateDomainObject> templatesInGroup,
                                                                 int templateGroupId, HttpServletRequest req,
                                                                 HttpServletResponse res) throws ServletException, IOException {

        return createTemplateGroupWarningDialog(templatesInGroup, templateGroupId, TEMPLATE_GROUP_DELETE_WARNING, req, res);
    }

    static String createDocumentsAssignedToTemplateInTemplateGroupWarningDialog(Iterable<TemplateDomainObject> templatesInGroup,
                                                                                int templateGroupId,
                                                                                HttpServletRequest request,
                                                                                HttpServletResponse response) throws ServletException, IOException {

        return createTemplateGroupWarningDialog(templatesInGroup, templateGroupId, TEMPLATE_GROUP_DELETE_DOCUMENTS_ASSIGNED_WARNING, request, response);
    }

    private static String createTemplateGroupWarningDialog(Iterable<TemplateDomainObject> templatesInGroup,
                                                           int templateGroupId, String template,
                                                           HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String commaSeparatedTemplateNames = StringUtils.join(new ArrayIterator(templatesInGroup) {
            public Object next() {
                TemplateDomainObject template = (TemplateDomainObject) super.next();
                return template.getName();
            }
        }, ", ");
        request.setAttribute("templates", commaSeparatedTemplateNames);
        request.setAttribute("templategroup", String.valueOf(templateGroupId));
        return Utility.getAdminContents(template, request, response);
    }

    private static void langTag(String lang, HttpServletRequest request) {
        request.setAttribute("language", lang);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser(request);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(request, response);
            return;
        }

        Utility.setDefaultHtmlContentType(response);
        PrintWriter out = response.getWriter();
        String htmlStr = Utility.getAdminContents(TEMPLATE_ADMIN, request, response);
        out.println(htmlStr);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(req, res);
            return;
        }

        Utility.setDefaultHtmlContentType(res);
        PrintWriter out = res.getWriter();

        String lang = req.getParameter("language");
        String htmlStr = null;
        TemplateMapper templateMapper = imcref.getTemplateMapper();
        if (req.getParameter("cancel") != null) {
            res.sendRedirect("AdminManager");
            return;
        } else if (req.getParameter("add_group") != null) {
            htmlStr = createAddGroupDialog(req, res);
        } else if (req.getParameter("delete_group") != null) {
            htmlStr = createDeleteTemplateGroupDialog(templateMapper, req, res);
        } else if (req.getParameter("rename_group") != null) {
            htmlStr = createRenameTemplateGroupDialog(templateMapper, req, res);
        } else if (req.getParameter("assign_group") != null) {
            htmlStr = createAssignTemplateGroupDialog(lang, templateMapper, req, res);
        }
        out.print(htmlStr);
    }

    private String createAssignTemplateGroupDialog(String lang, TemplateMapper templateMapper,
                                                   HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        return createAssignTemplatesToGroupDialog(templateMapper, null, lang, req, res);
    }

    //Not used due to IMCMS-634 P1-RB4-security: Arbitrary file upload vulnerability in TemplateAdd
    @Deprecated
    private String createEditTemplateDialog(String lang, TemplateMapper templateMapper,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        createShortTemplateDialog(lang, request, templateMapper);
        return Utility.getAdminContents(ADMIN_TEMPLATE_EDIT, request, response);
    }

    //Not used due to IMCMS-634 P1-RB4-security: Arbitrary file upload vulnerability in TemplateAdd
    @Deprecated
    private String createDownloadTemplateDialog(String lang, TemplateMapper templateMapper, HttpServletRequest request,
                                                HttpServletResponse response) throws IOException, ServletException {
        createShortTemplateDialog(lang, request, templateMapper);
        return Utility.getAdminContents("template_get.jsp", request, response);
    }

    //Not used due to IMCMS-634 P1-RB4-security: Arbitrary file upload vulnerability in TemplateAdd
    @Deprecated
    private String createUploadDemoTemplateDialog(String lang, TemplateMapper templateMapper,
                                                  HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        langTag(lang, request);
        List<TemplateDomainObject> templates = templateMapper.getAllTemplates();
        String templatesList = templateMapper.createHtmlOptionListOfTemplates(templates, null);
        request.setAttribute("templates", templatesList);
        return Utility.getAdminContents(TEMPLATE_DEMO_UPLOAD, request, response);
    }
}
