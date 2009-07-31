package com.imcode.imcms.web.admin;

import imcode.server.Imcms;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.ModelMap;

import com.imcode.imcms.web.admin.controller.AdminControllerConstants;

/**
 * 
 * Prototype class
 * 
 */
public final class AdminPageController {
	public enum AdminModul {
				DEFAULT_PERMISSION_GROUPS("profile/default_perm_groups", "admin/progile/def_perm_groups/title"), 
				PERMISSION_GROUPS("profile/perm_groups", "admin/profile/perm_groups/title"), 
				PARTIAL_PERMISSION_GROUP("profile/partial_perm_group"), 
				PROFILE_NEW("profile/profile_new", "admin/profile/profile_new/title"), 
				PROFILE_NEW_ON_PAGE("profile/profile_new_on_page", "admin/profile/profile_new_on_page/title"),
				
				CHANGE_SEVERAL("change_several/change_several", "admin/change_several/title"),
				
				SEARCH_FORM("search/search_form", "admin/search/title"),
				SEARCH_RESULTS("search/search_results");

		private String name;
		private String defaultTitle;

		AdminModul(String name) {
			this.name = name;
		}

		private AdminModul(String name, String defaultTitle) {
			this.name = name;
			this.defaultTitle = defaultTitle;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return getName();
		}

		public String getDefaultTitle() {
			return defaultTitle;
		}
	}

	private final static Log log = LogFactory.getLog(AdminPageController.class);
	
	private final static String ADMIN_PAGE_TEMPLATE = "admin/%s";
	private final static String ADMIN_MODUL = "adminModul";
	private final static String INCLUDE_MODUL = "includeModul";
	private final static String PAGE_TYPE = "pageType";
	private final static String PAGE_TITLE = "pageTitle";
	private final static String LOCALE = "locale";

	private ModelMap model;

	private AdminModul adminModul;
	private String pageTitle;
	private Locale locale;

	public AdminPageController(ModelMap model) {
		this.model = model;
	}

	public AdminPageController(ModelMap model, AdminModul adminModul) {
		this.model = model;

		setAdminModul(adminModul);
	}

	public AdminPageController(ModelMap model, AdminModul adminModul,
			String pageTitle) {
		this.model = model;

		setAdminModul(adminModul);
		setPageTitle(pageTitle);
	}
	
	public String getPartialPage() {
		model.addAttribute(PAGE_TYPE, "partial");
		
		model.addAttribute(LOCALE, locale);
		
		return String.format(ADMIN_PAGE_TEMPLATE, adminModul.getName());
	}

	public String getAdminPage() {
		if (validate()) {
			model.addAttribute(ADMIN_MODUL, adminModul);
			model.addAttribute(PAGE_TYPE, "page");
			
			model.addAttribute(PAGE_TITLE, pageTitle);
			model.addAttribute(INCLUDE_MODUL, adminModul.getName());
			model.addAttribute(LOCALE, locale);

			return String.format(ADMIN_PAGE_TEMPLATE, "admin");
		} else {
			throw new RuntimeException("No validated");
		}
	}

	private boolean validate() {
		if (adminModul != null && pageTitle != null) {
			return true;
		}

		throw new RuntimeException("Admin Modul or Page Title are not setted");
	}

	public void setAdminModul(AdminModul adminModul) {
		assert adminModul != null;

		this.adminModul = adminModul;
		if (StringUtils.trimToNull(this.pageTitle) == null) {
			setPageTitle(adminModul.getDefaultTitle());
		}
	}

	public void setPageTitle(String pageTitle) {
		if (pageTitle != null) {
			this.pageTitle = pageTitle;
		}
	}

	public void addRawData() {
		WebContentStock stock = WebContentStock
				.getInstance(Imcms.getServices());
		model.addAttribute(AdminControllerConstants.MODEL_ATTRIBUTE__RAW_DATA,
				stock.getAllStock());
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
