package com.imcode.imcms.web.admin.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.imcode.imcms.web.admin.AdminPageController;
import com.imcode.imcms.web.admin.AdminPageController.AdminModul;
import com.imcode.imcms.web.admin.command.ChangeSeveralCommand;

/**
 *
 * Prototype class
 *
 */
@Controller
public class ChangeServeralController {

	@RequestMapping("/changeSeveral")
	public String changeSeveralHandler(
			ModelMap model, Locale locale, HttpServletRequest request, 
			@ModelAttribute(AdminControllerConstants.MODEL_ATTRIBUTE__CHANGE_SEVERAL_COMMAND) ChangeSeveralCommand csc
			) {
		
		if (request.getMethod().equals("POST")) {
			// Validate
			// Edit
		}
		
		AdminPageController apc = new AdminPageController(model);
		apc.setAdminModul(AdminModul.CHANGE_SEVERAL);
		apc.setLocale(locale);
		apc.addRawData();
		return apc.getAdminPage();
	}
}
