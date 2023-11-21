package com.imcode.imcms.controller.core;

import com.imcode.imcms.api.MultiFactorAuthenticationService;
import com.imcode.imcms.servlet.VerifyUser;
import imcode.server.ImcmsConstants;
import imcode.util.Utility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/mfa")
public class MultiFactorAuthenticationController {

	private final MultiFactorAuthenticationService multiFactorAuthenticationService;

	MultiFactorAuthenticationController(MultiFactorAuthenticationService multiFactorAuthenticationService) {
		this.multiFactorAuthenticationService = multiFactorAuthenticationService;
	}

	@RequestMapping
	public ModelAndView goTo2FaPage(HttpServletRequest request) {
		final ModelAndView modelAndView = new ModelAndView("MFA");
		modelAndView.addObject("userLanguage", Utility.getUserLanguageFromCookie(request.getCookies()).getCode());
		return modelAndView;
	}

	@PostMapping(value = "/second-factor")
	public void checkSecondFactor(@RequestParam String oneTimePassword,
	                                            HttpServletRequest request,
	                                            HttpServletResponse response) throws IOException, ServletException {

		if (multiFactorAuthenticationService.checkSecondFactor(request, response, oneTimePassword)) {
			final HttpSession session=request.getSession();
			session.setAttribute(VerifyUser.REQUEST_PARAMETER__EDIT_USER, request.getAttribute(VerifyUser.REQUEST_PARAMETER__EDIT_USER));

			response.sendRedirect(request.getContextPath() + "/servlet/VerifyUser");
			return;
		}

		request.getRequestDispatcher(ImcmsConstants.API_PREFIX.concat("/mfa")).forward(request, response);
	}
}
