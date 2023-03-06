package com.imcode.imcms.servlet.superadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@Log4j2
public class UpdateDocumentExportStatusServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final UserDomainObject user = Utility.getLoggedOnUser(req);

		if (!user.isSuperAdmin()) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		final ObjectMapper mapper = new ObjectMapper();
		final Properties properties = mapper.readValue(req.getInputStream(), Properties.class);

		Imcms.getServices().getDocumentMapper()
				.updateExportStatus(
						Integer.parseInt(String.valueOf(properties.get("docId"))),
						Boolean.parseBoolean(String.valueOf(properties.get("allowedToExport"))),
						Utility.getLoggedOnUser(req));

		resp.sendError(HttpServletResponse.SC_OK);
	}
}
