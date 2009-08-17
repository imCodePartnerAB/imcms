package com.imcode.imcms.web.admin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.imcode.imcms.web.admin.AdminPageController;
import com.imcode.imcms.web.admin.DefaultPermissionGroups;
import com.imcode.imcms.web.admin.PermissionGroup;
import com.imcode.imcms.web.admin.PermissionGroupsBasket;
import com.imcode.imcms.web.admin.AdminPageController.AdminModul;
import com.imcode.imcms.web.admin.command.PermissionGroupsCommand;
import com.imcode.imcms.web.admin.command.ProfileNewCommand;
import com.imcode.imcms.web.admin.command.ProfileNewOnPageCommand;

/**
 * 
 * Prototype class
 * 
 */
@Controller
@RequestMapping("/profiles/**/*")
public class ProfilesController {
	public static final Log log = LogFactory.getLog(ProfilesController.class);

	private static final Pattern URL_PATTERN__PARTIAL_PERM_FROUPS = Pattern
			.compile("^/imcms/newadmin/profiles/partialPermGroups/([^/].*)$");

	@RequestMapping("defaultPermGroups")
	public String defaultPermissionGroupsHandler(
			ModelMap model,
			Locale locale,
			HttpServletRequest request,
			@ModelAttribute(AdminControllerConstants.MODEL_ATTRIBUTE__PERMISSION_GROUP_COMMAND) PermissionGroupsCommand pgc) {
		if (request.getMethod().toUpperCase().equals("POST")) {
			// Validate
			// Edit permission groups;
			editPermissionGroups(pgc, "savePermissions");
		} else {
			// load data
			List<PermissionGroup> allDefaultPermissionGroups = new ArrayList<PermissionGroup>();
			allDefaultPermissionGroups.addAll(DefaultPermissionGroups
					.getAllDefaultPermissionGroups().values());
			pgc.setGroups(allDefaultPermissionGroups);
		}

		AdminPageController apc = new AdminPageController(model);
		apc.setAdminModul(AdminModul.DEFAULT_PERMISSION_GROUPS);
		apc.setLocale(locale);
		return apc.getAdminPage();
	}

	@RequestMapping("permGroups")
	public String permissionGroupHandler(
			ModelMap model,
			Locale locale,
			HttpServletRequest request,
			@ModelAttribute(AdminControllerConstants.MODEL_ATTRIBUTE__PERMISSION_GROUP_COMMAND) PermissionGroupsCommand pgc,
			@RequestParam(value = "strategy", required = false) String editStrategy) {
		if (request.getMethod().toUpperCase().equals("POST")) {
			// Validate
			// Edit
			editPermissionGroups(pgc, editStrategy);
		}

		AdminPageController apc = new AdminPageController(model);
		apc.setAdminModul(AdminModul.PERMISSION_GROUPS);
		apc.setLocale(locale);
		apc.addRawData();
		return apc.getAdminPage();
	}

	@RequestMapping(value = "partialPermGroups/*", method = RequestMethod.POST)
	public String partialPermGroups(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		Long id;
		try {
			Matcher m = URL_PATTERN__PARTIAL_PERM_FROUPS.matcher(request
					.getRequestURI());
			m.find();

			id = Long.parseLong(m.group(1));

		} catch (Exception ex) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		PermissionGroupsCommand pgc = new PermissionGroupsCommand();
		pgc.getGroups().add(PermissionGroupsBasket.getPermissionGroupById(id));
		model.addAttribute(AdminControllerConstants.MODEL_ATTRIBUTE__PERMISSION_GROUP_COMMAND, pgc);

		AdminPageController apc = new AdminPageController(model);
		apc.setAdminModul(AdminModul.PARTIAL_PERMISSION_GROUP);
		return apc.getPartialPage();
	}

	private void editPermissionGroups(PermissionGroupsCommand pgc,
			String editStrategy) {
		assert editStrategy != null;
		assert pgc != null;

		if (editStrategy.equals("rename")) {
			// Rename group
		} else if (editStrategy.equals("savePermissions")) {
			// Save group permissions
		}
	}

	@RequestMapping("profileNew")
	public String viewProfileNewHandler(
			ModelMap model,
			Locale locale,
			HttpServletRequest request,
			@ModelAttribute(AdminControllerConstants.MODEL_ATTRIBUTE__PROFILE_NEW_COMMAND) ProfileNewCommand pnc) {
		if (request.getMethod().equals("POST")) {
			// Validate
			// Edit
		}

		AdminPageController apc = new AdminPageController(model);
		apc.setAdminModul(AdminModul.PROFILE_NEW);
		apc.setLocale(locale);
		apc.addRawData();
		return apc.getAdminPage();
	}

	@RequestMapping("profileNewOnPage")
	public String viewProfileNewOnPageHandler(
			ModelMap model,
			Locale locale,
			HttpServletRequest request,
			@ModelAttribute(AdminControllerConstants.MODEL_ATTRIBUTE__PROVILE_NEW_ON_PAGE_COMMAND) ProfileNewOnPageCommand pnpc) {

		if (request.getMethod().equals("POST")) {
			// Validate
			// Edit
		} else {

		}

		AdminPageController apc = new AdminPageController(model);
		apc.setAdminModul(AdminModul.PROFILE_NEW_ON_PAGE);
		apc.setLocale(locale);
		apc.addRawData();
		return apc.getAdminPage();
	}
}
