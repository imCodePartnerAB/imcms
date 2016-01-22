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
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
		} else if (checkNotNull("template_get")) {
			downloadTemplate(request, imcref, response);
		} else if (checkNotNull("template_delete_cancel")) {
			htmlStr = TemplateAdmin.createDeleteTemplateDialog(templateMapper, user, lang, imcref);
		} else if (checkNotNull("template_delete")) {
			deleteTemplate(request, imcref);
			htmlStr = TemplateAdmin.createDeleteTemplateDialog(templateMapper, user, lang, imcref);
		} else if (checkNotNull("assign")) {
			htmlStr = addTemplatesToGroup(request, templateMapper, lang, user, imcref);
		} else if (checkNotNull("deassign")) {
			htmlStr = removeTemplatesFromGroup(request, templateMapper, lang, user, imcref);
		} else if (checkNotNull("show_assigned")) {
			int templateGroupId = Integer.parseInt(request.getParameter("templategroup"));
			TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(templateGroupId);
			htmlStr = TemplateAdmin.createAssignTemplatesToGroupDialog(templateMapper,
					templateGroup, lang, user, imcref);
		} else if (checkNotNull("template_rename")) {
			htmlStr = renameTemplate(request, templateMapper, lang, imcref, user);
		} else if (checkNotNull("change_availability_template")) {
			htmlStr = changeAvailabilityTemplate(request, templateMapper, lang, imcref, user);
		} else if (checkNotNull("template_delete_check")) {
			htmlStr = deleteTemplateAfterCheckingUsage(request, imcref, lang, user);
		} else if (checkNotNull("group_delete_check")) {
			htmlStr = deleteTemplateGroupAfterCheckingUsage(request, imcref, user);
		} else if (checkNotNull("group_delete")) {
			deleteTemplateGroup(request, imcref);
			htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog(templateMapper, imcref, user);
		} else if (checkNotNull("group_delete_cancel")) {
			htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog(templateMapper, imcref, user);
		} else if (checkNotNull("group_add")) {
			htmlStr = addTemplateGroup(request, imcref, user);
		} else if (checkNotNull("group_rename")) {
			htmlStr = renameTemplateGroup(request, imcref, user, lang);
		} else if (checkNotNull("list_templates_docs")) {
			htmlStr = listDocumentsUsingTemplate(request, imcref, lang, user);
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

	private String addTemplateGroup(HttpServletRequest req, ImcmsServices imcref, UserDomainObject user) {
		String htmlStr;
		String name = req.getParameter("name");
		if (StringUtils.isEmpty(name)) {
			htmlStr = createAddNameEmptyErrorDialog(imcref, user);
		} else {
			TemplateGroupDomainObject templateGroup = imcref.getTemplateMapper().getTemplateGroupByName(name);
			if (null != templateGroup) {
				htmlStr = createTemplateGroupExistsErrorDialog(imcref, user);
			} else {
				imcref.getTemplateMapper().createTemplateGroup(name);
				htmlStr = TemplateAdmin.createAddGroupDialog(imcref, user);
			}
		}
		return htmlStr;
	}

	private String addTemplatesToGroup(HttpServletRequest req, TemplateMapper templateMapper, String lang,
									   UserDomainObject user, ImcmsServices imcref) throws IOException {
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
		return TemplateAdmin.createAssignTemplatesToGroupDialog(templateMapper, templateGroup, lang, user, imcref);
	}

	private String removeTemplatesFromGroup(HttpServletRequest req, TemplateMapper templateMapper, String lang,
											UserDomainObject user, ImcmsServices imcref) throws IOException {
		String groupIdParameter = req.getParameter("group_id");
		String[] templateIds = req.getParameterValues("assigned");
		TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(Integer.parseInt(groupIdParameter));
		if (null != templateGroup && null != templateIds) {
			for (String templateName : templateIds) {
				TemplateDomainObject templateToUnassign = templateMapper.getTemplateByName(templateName);
				templateMapper.removeTemplateFromGroup(templateToUnassign, templateGroup);
			}
		}
		return TemplateAdmin.createAssignTemplatesToGroupDialog(templateMapper, templateGroup, lang, user, imcref);
	}

	private String createAddNameEmptyErrorDialog(ImcmsServices imcref, UserDomainObject user) {
		return imcref.getAdminTemplate("templategroup_add_name_blank.html", user, null);
	}

	private String createDocumentsUsingTemplateDialog(ImcmsServices imcref, UserDomainObject user,
													  TemplateDomainObject template, String lang) {
		List<String> vec2 = new ArrayList<>();
		vec2.add("#template_list#");
		vec2.add(imcref.getTemplateMapper().createHtmlOptionListOfTemplatesWithDocumentCount(user));
		if (template != null) {
			vec2.add("#templates_docs#");
			vec2.add(TemplateAdmin.createHtmlOptionListOfDocumentsUsingTemplate(imcref, template, user));
		}
		vec2.add("#language#");
		vec2.add(lang);
		return imcref.getAdminTemplate("template_list.html", user, vec2);
	}

	private String createRenameNameEmptyErrorDialog(String lang, ImcmsServices imcref, UserDomainObject user) {
		List<String> vec = new ArrayList<>();
		vec.add("#language#");
		vec.add(lang);
		return imcref.getAdminTemplate("template_rename_name_blank.html", user, vec);
	}

	private String createTemplateGroupExistsErrorDialog(ImcmsServices imcref, UserDomainObject user) {
		return imcref.getAdminTemplate("templategroup_add_exists.html", user, null);
	}

	private void deleteTemplate(HttpServletRequest req, ImcmsServices imcref) {
		TemplateMapper templateMapper = imcref.getTemplateMapper();
		String new_temp_id = req.getParameter("new_template");
		TemplateDomainObject newTemplate = templateMapper.getTemplateByName(new_temp_id);
		String template_id = req.getParameter("template");
		TemplateDomainObject template = templateMapper.getTemplateByName(template_id);

		templateMapper.replaceAllUsagesOfTemplate(template, newTemplate);
		templateMapper.deleteTemplate(template);
	}

	private String deleteTemplateAfterCheckingUsage(HttpServletRequest req, ImcmsServices imcref, String lang,
													UserDomainObject user) throws IOException {
		TemplateMapper templateMapper = imcref.getTemplateMapper();
		String template_id = req.getParameter("template");
		TemplateDomainObject template = templateMapper.getTemplateByName(template_id);
		DocumentDomainObject[] documentsUsingTemplate = templateMapper.getDocumentsUsingTemplate(template);

		if (documentsUsingTemplate.length > 0) {
			return TemplateAdmin.createDeleteTemplateInUseWarningDialog(lang, imcref, template, user, templateMapper);
		} else {
			templateMapper.deleteTemplate(template);
			return TemplateAdmin.createDeleteTemplateDialog(templateMapper, user, lang, imcref);
		}
	}

	private void deleteTemplateGroup(HttpServletRequest req, ImcmsServices imcref) {
		int grp_id = Integer.parseInt(req.getParameter("templategroup"));
		imcref.getTemplateMapper().deleteTemplateGroup(grp_id);
	}

	private String deleteTemplateGroupAfterCheckingUsage(HttpServletRequest req, ImcmsServices imcref,
														 UserDomainObject user) {
		int templateGroupId = Integer.parseInt(req.getParameter("templategroup"));
		TemplateMapper templateMapper = imcref.getTemplateMapper();
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
			htmlStr = TemplateAdmin.createDeleteNonEmptyTemplateGroupWarningDialog(templatesInGroup, templateGroupId, imcref, user);
			if (existsDocumentUsingTemplateInTemplateGroup) {
				htmlStr = TemplateAdmin.createDocumentsAssignedToTemplateInTemplateGroupWarningDialog(templatesInGroup, templateGroupId, imcref, user);
			}
		} else {
			templateMapper.deleteTemplateGroup(templateGroupId);
			htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog(templateMapper, imcref, user);
		}
		return htmlStr;
	}

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

	private String listDocumentsUsingTemplate(HttpServletRequest req, ImcmsServices imcref, String lang,
											  UserDomainObject user) {
		String templateId = req.getParameter("template");
		TemplateDomainObject template = imcref.getTemplateMapper().getTemplateByName(templateId);
		return createDocumentsUsingTemplateDialog(imcref, user, template, lang);
	}

	private String renameTemplate(HttpServletRequest req, TemplateMapper templateMapper, String lang,
								  ImcmsServices imcref, UserDomainObject user) throws IOException {
		String htmlStr;
		String template_id = req.getParameter("template");
		TemplateDomainObject template = templateMapper.getTemplateByName(template_id);
		String newNameForTemplate = req.getParameter("name");
		if (StringUtils.isEmpty(newNameForTemplate)) {
			htmlStr = createRenameNameEmptyErrorDialog(lang, imcref, user);
		} else {
			LocalizedMessage error = null;
			if (!templateMapper.renameTemplate(template.getName(), newNameForTemplate)) {
				error = ERROR__TEMPLATE_NAME_TAKEN;
			}
			htmlStr = TemplateAdmin.createRenameTemplateDialog(lang, imcref, templateMapper, user, error);
		}
		return htmlStr;
	}

	private String changeAvailabilityTemplate(HttpServletRequest req, TemplateMapper templateMapper, String lang,
											  ImcmsServices imcref, UserDomainObject user) throws IOException {
		String htmlStr;
		String template_id = (req.getParameter("template")).replace("(Hidden)", "");
		TemplateDomainObject template = templateMapper.getTemplateByName(template_id);
		boolean isHidden = req.getParameter("hidden") != null;
		LocalizedMessage error = null;
		if (!templateMapper.updateAvaliability(template.getName(), isHidden)) {
			error = ERROR_AVAILABILITY_NOT_CHANGED;
		}
		htmlStr = TemplateAdmin.createChangeAvailabilityTemplateDialog(lang, imcref, templateMapper, user, error);
		return htmlStr;
	}

	private String renameTemplateGroup(HttpServletRequest req, ImcmsServices imcref, UserDomainObject user,
									   String lang) {
		String htmlStr;
		int grp_id = Integer.parseInt(req.getParameter("templategroup"));
		String name = req.getParameter("name");
		if (StringUtils.isEmpty(name)) {
			htmlStr = createRenameNameEmptyErrorDialog(lang, imcref, user);
		} else {
			TemplateMapper templateMapper = imcref.getTemplateMapper();
			TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(grp_id);
			templateMapper.renameTemplateGroup(templateGroup, name);
			htmlStr = TemplateAdmin.createRenameTemplateGroupDialog(templateMapper, imcref, user);
		}
		return htmlStr;
	}

	private String showDocument(HttpServletRequest req, HttpServletResponse res, ImcmsServices imcref,
								String lang, UserDomainObject user) throws IOException {
		String meta_id = req.getParameter("templates_doc");
		String htmlStr = null;
		if (meta_id != null) {
			res.sendRedirect(URLEncoder.encode("AdminDoc?meta_id=" + meta_id, CharEncoding.UTF_8));
		} else {
			htmlStr = createDocumentsUsingTemplateDialog(imcref, user, null, lang);
		}
		return htmlStr;
	}

}
