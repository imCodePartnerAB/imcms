package com.imcode.imcms.controller.core;

import com.imcode.imcms.api.MultiFactorAuthenticationService;
import imcode.server.ImcmsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/mfa")
public class MultiFactorAuthenticationController {

	private final MultiFactorAuthenticationService multiFactorAuthenticationService;

	MultiFactorAuthenticationController(MultiFactorAuthenticationService multiFactorAuthenticationService) {
		this.multiFactorAuthenticationService = multiFactorAuthenticationService;
	}

	@RequestMapping
	public ModelAndView goTo2FaPage() {
		return new ModelAndView("MFA");
	}

	@PostMapping(value = "/second-factor")
	public void checkSecondFactor(@RequestParam String oneTimePassword,
	                                            HttpServletRequest request,
	                                            HttpServletResponse response) throws IOException, ServletException {

		if (multiFactorAuthenticationService.checkSecondFactor(request, response, oneTimePassword)) {
			response.sendRedirect("/servlet/VerifyUser");
			return;
		}

		request.getRequestDispatcher(ImcmsConstants.API_PREFIX.concat("/mfa")).forward(request, response);
	}
}
