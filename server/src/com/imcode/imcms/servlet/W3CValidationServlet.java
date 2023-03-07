package com.imcode.imcms.servlet;

import com.imcode.imcms.domain.services.api.W3CValidationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class W3CValidationServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!W3CValidationService.isAvailable()) {
			resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}

		final String htmlToValidate = req.getParameter("htmlToValidate");
		final boolean showResults = "true".equals(req.getParameter("showResults"));

		resp.getWriter()
				.write(W3CValidationService
						.validate(htmlToValidate, showResults)
						.toJSONString()
				);
	}
}
