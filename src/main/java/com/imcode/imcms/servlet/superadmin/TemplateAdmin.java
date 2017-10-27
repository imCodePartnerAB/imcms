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
import java.util.ArrayList;
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
    private static final String TEMPLATE_UPLOAD = "template_upload.jsp";
    private static final String TEMPLATE_GROUP_RENAME = "templategroup_rename.jsp";
    private static final String TEMPLATE_ASSIGN = "template_assign.jsp";
    private static final String TEMPLATE_DELETE_WARNING = "template_delete_warning.jsp";
    private static final String TEMPLATE_GROUP_DELETE_WARNING = "templategroup_delete_warning.jsp";
    private static final String TEMPLATE_GROUP_DELETE_DOCUMENTS_ASSIGNED_WARNING = "templategroup_delete_documents_assigned_warning.jsp";

    static String createDeleteTemplateGroupDialog(TemplateMapper templateMapper, ImcmsServices imcref,
                                                  UserDomainObject user) {
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_DELETE, user, createTemplateGroupsList(templateMapper));
    }

    private static List<String> createTemplateGroupsList(TemplateMapper templateMapper) {
        String temps = templateMapper.createHtmlOptionListOfTemplateGroups(null);
        List<String> vec = new ArrayList<>();
        vec.add("#templategroups#");
        vec.add(temps);
        return vec;
    }

    static String createAddGroupDialog(ImcmsServices imcref, UserDomainObject user) {
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_ADD, user, null);
    }

    private static List<String> createShortTemplateDialog(String lang, ImcmsServices imcref,
                                                          TemplateMapper templateMapper) throws IOException {
        List<String> vec = new ArrayList<>();
        List<TemplateDomainObject> templates = imcref.getTemplateMapper().getAllTemplates();
        String temps = templateMapper.createHtmlOptionListOfTemplates(templates, null);
        vec.add("#templates#");
        vec.add(temps);
        vec.addAll(langTag(lang));
        return vec;
    }

    static String createRenameTemplateDialog(String lang, ImcmsServices imcref, TemplateMapper templateMapper,
                                             UserDomainObject user, LocalizedMessage error) throws IOException {
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_RENAME, user,
                createStandardTemplateDialog(lang, imcref, templateMapper, user, error));
    }

    static String createChangeAvailabilityTemplateDialog(String lang, ImcmsServices imcref, TemplateMapper templateMapper,
                                                         UserDomainObject user, LocalizedMessage error) throws IOException {
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_AVALIABILITY, user,
                createStandardTemplateDialog(lang, imcref, templateMapper, user, error));
    }

    static List<String> createStandardTemplateDialog(String lang, ImcmsServices imcref, TemplateMapper templateMapper,
                                                     UserDomainObject user, LocalizedMessage error) throws IOException {
        List<String> vec = langTag(lang);
        vec.add("#templates#");
        vec.add(templateMapper.createHtmlOptionListOfTemplates(imcref.getTemplateMapper().getAllTemplates(), null));
        vec.add("#error#");
        vec.add(null == error ? "" : error.toLocalizedString(user));
        return vec;
    }

    static String createDeleteTemplateDialog(TemplateMapper templateMapper, UserDomainObject user, String lang,
                                             ImcmsServices imcref) {
        List<String> vec = new ArrayList<>();
        String templatesList = templateMapper.createHtmlOptionListOfTemplatesWithDocumentCount(user);
        vec.add("#templates#");
        vec.add(templatesList);
        vec.addAll(langTag(lang));
        return imcref.getAdminTemplate(TEMPLATE_DELETE, user, vec);
    }

    static String createRenameTemplateGroupDialog(TemplateMapper templateMapper, ImcmsServices imcref,
                                                  UserDomainObject user) {
        return imcref.getAdminTemplate(TEMPLATE_GROUP_RENAME, user, createTemplateGroupsList(templateMapper));
    }

    static String createAssignTemplatesToGroupDialog(TemplateMapper templateMapper, TemplateGroupDomainObject currentTemplateGroup, String language,
                                                     UserDomainObject user, ImcmsServices imcref) throws IOException {
        List<String> vec = new ArrayList<>();

        String htmlOptionListOfTemplateGroups = templateMapper.createHtmlOptionListOfTemplateGroups(
                currentTemplateGroup);

        vec.add("#templategroups#");
        vec.add(htmlOptionListOfTemplateGroups);
        if (currentTemplateGroup == null) {
            vec.add("#assigned#");
            vec.add("");
            vec.add("#unassigned#");
            vec.add("");
            vec.add("#group#");
            vec.add("");
            vec.add("#group_id#");
            vec.add("");
        } else {
            Collection<TemplateDomainObject> templatesInGroup = templateMapper.getTemplatesInGroup(currentTemplateGroup);
            List<TemplateDomainObject> templatesNotInGroup = templateMapper.getTemplatesNotInGroup(currentTemplateGroup);
            String htmlOptionListOfTemplatesInSelectedGroup = templateMapper.createHtmlOptionListOfTemplates(
                    templatesInGroup, null);
            String htmlOptionListOfTemplatesNotInSelectedGroup = templateMapper.createHtmlOptionListOfTemplates(
                    templatesNotInGroup, null);
            vec.add("#assigned#");
            vec.add(htmlOptionListOfTemplatesInSelectedGroup);
            vec.add("#unassigned#");
            vec.add(htmlOptionListOfTemplatesNotInSelectedGroup);
            vec.add("#group#");
            vec.add(currentTemplateGroup.getName());
            vec.add("#group_id#");
            vec.add("" + currentTemplateGroup.getId());
        }
        vec.addAll(langTag(language));
        return imcref.getAdminTemplate(TEMPLATE_ASSIGN, user, vec);
    }

    static String createHtmlOptionListOfDocumentsUsingTemplate(ImcmsServices imcref, TemplateDomainObject template) {
        DocumentDomainObject[] documents = imcref.getTemplateMapper().getDocumentsUsingTemplate(template);
        StringBuffer htmlOptionList = new StringBuffer();
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
                                                         TemplateDomainObject template, UserDomainObject user,
                                                         TemplateMapper templateMapper) throws IOException {
        List<String> vec = langTag(lang);
        vec.add("#template#");
        vec.add(template.getName());
        vec.add("#docs#");
        vec.add(createHtmlOptionListOfDocumentsUsingTemplate(imcref, template));
        vec.add("#templates#");
        vec.add(templateMapper.createHtmlOptionListOfTemplates(templateMapper.getAllTemplatesExceptOne(template), null));

        return imcref.getAdminTemplate(TEMPLATE_DELETE_WARNING, user, vec);
    }

    static String createDeleteNonEmptyTemplateGroupWarningDialog(Iterable<TemplateDomainObject> templatesInGroup,
                                                                 int templateGroupId, ImcmsServices imcref,
                                                                 UserDomainObject user) {
        return createTemplateGroupWarningDialog(templatesInGroup, templateGroupId, imcref, TEMPLATE_GROUP_DELETE_WARNING, user);
    }

    static String createDocumentsAssignedToTemplateInTemplateGroupWarningDialog(Iterable<TemplateDomainObject> templatesInGroup,
                                                                                int templateGroupId, ImcmsServices imcref,
                                                                                UserDomainObject user) {

        return createTemplateGroupWarningDialog(templatesInGroup, templateGroupId, imcref, TEMPLATE_GROUP_DELETE_DOCUMENTS_ASSIGNED_WARNING, user);
    }

    private static String createTemplateGroupWarningDialog(Iterable<TemplateDomainObject> templatesInGroup, int templateGroupId,
                                                           ImcmsServices imcref, String template,
                                                           UserDomainObject user) {
        String commaSeparatedTemplateNames = StringUtils.join(new ArrayIterator(templatesInGroup) {
            public Object next() {
                TemplateDomainObject template = (TemplateDomainObject) super.next();
                return template.getName();
            }
        }, ", ");
        List<String> vec = new ArrayList<>();
        vec.add("#templates#");
        vec.add(commaSeparatedTemplateNames);
        vec.add("#templategroup#");
        vec.add(String.valueOf(templateGroupId));
        return imcref.getAdminTemplate(template, user, vec);
    }

    private static List<String> langTag(String lang) {
        final List<String> languageTag = new ArrayList<>();
        languageTag.add("#language#");
        languageTag.add(lang);

        return languageTag;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(req, res);
            return;
        }

        Utility.setDefaultHtmlContentType(res);
        PrintWriter out = res.getWriter();
        String htmlStr = imcref.getAdminTemplate(TEMPLATE_ADMIN, user, null);
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
        } else if (req.getParameter("add_template") != null) {
            htmlStr = createUploadTemplateDialog(templateMapper, lang, imcref, user);
        } else if (req.getParameter("add_demotemplate") != null) {
            htmlStr = createUploadDemoTemplateDialog(lang, templateMapper, imcref, user);
        } else if (req.getParameter("delete_template") != null) {
            htmlStr = createDeleteTemplateDialog(templateMapper, user, lang, imcref);
        } else if (req.getParameter("rename_template") != null) {
            htmlStr = createRenameTemplateDialog(lang, imcref, templateMapper, user, null);
        } else if (req.getParameter("change_availability_template") != null) {
            htmlStr = createChangeAvailabilityTemplateDialog(lang, imcref, templateMapper, user, null);
        } else if (req.getParameter("get_template") != null) {
            htmlStr = createDownloadTemplateDialog(lang, imcref, templateMapper, user);
        } else if (req.getParameter("edit_template") != null) {
            htmlStr = createEditTemplateDialog(lang, imcref, templateMapper, user);
        } else if (req.getParameter("add_group") != null) {
            htmlStr = createAddGroupDialog(imcref, user);
        } else if (req.getParameter("delete_group") != null) {
            htmlStr = createDeleteTemplateGroupDialog(templateMapper, imcref, user);
        } else if (req.getParameter("rename_group") != null) {
            htmlStr = createRenameTemplateGroupDialog(templateMapper, imcref, user);
        } else if (req.getParameter("assign_group") != null) {
            htmlStr = createAssignTemplateGroupDialog(lang, templateMapper, user, imcref);
        } else if (req.getParameter("show_templates") != null) {
            htmlStr = createListTemplatesDialog(templateMapper, user, lang, imcref);
        }
        out.print(htmlStr);
    }

    private String createListTemplatesDialog(TemplateMapper templateMapper, UserDomainObject user, String lang,
                                             ImcmsServices imcref) {
        String templateList = templateMapper.createHtmlOptionListOfTemplatesWithDocumentCount(user);
        List<String> vec = langTag(lang);
        vec.add("#template_list#");
        vec.add(templateList);
        return imcref.getAdminTemplate("template_list.jsp", user, vec);
    }

    private String createAssignTemplateGroupDialog(String lang, TemplateMapper templateMapper,
                                                   UserDomainObject user, ImcmsServices imcref) throws IOException {
        return createAssignTemplatesToGroupDialog(templateMapper, null, lang, user, imcref);
    }

    private String createEditTemplateDialog(String lang, ImcmsServices imcref, TemplateMapper templateMapper,
                                            UserDomainObject user) throws IOException {
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_EDIT, user, createShortTemplateDialog(lang, imcref, templateMapper));
    }

    private String createDownloadTemplateDialog(String lang, ImcmsServices imcref,
                                                TemplateMapper templateMapper, UserDomainObject user) throws IOException {
        return imcref.getAdminTemplate("template_get.jsp", user, createShortTemplateDialog(lang, imcref, templateMapper));
    }

    private String createUploadDemoTemplateDialog(String lang, TemplateMapper templateMapper,
                                                  ImcmsServices imcref, UserDomainObject user) throws IOException {
        List<String> vec = langTag(lang);
        List<TemplateDomainObject> templates = templateMapper.getAllTemplates();
        String templatesList = templateMapper.createHtmlOptionListOfTemplates(templates, null);
        vec.add("#templates#");
        vec.add(templatesList);
        return imcref.getAdminTemplate(TEMPLATE_DEMO_UPLOAD, user, vec);
    }

    private String createUploadTemplateDialog(TemplateMapper templateMapper, String lang, ImcmsServices imcref,
                                              UserDomainObject user) {
        List<String> vec = langTag(lang);
        String temps = templateMapper.createHtmlOptionListOfTemplateGroups(null);
        vec.add("#templategroups#");
        vec.add(temps);
        return imcref.getAdminTemplate(TEMPLATE_UPLOAD, user, vec);
    }
}
