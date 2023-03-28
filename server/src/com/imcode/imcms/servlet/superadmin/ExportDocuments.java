package com.imcode.imcms.servlet.superadmin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.domain.dto.export.DocumentExportService;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ExportDocuments extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final UserDomainObject user = Utility.getLoggedOnUser(req);
		if (!user.isSuperAdmin()) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		final ObjectMapper mapper = new ObjectMapper();
		final TypeReference<HashMap<String, Integer>> typeReference = new TypeReference<HashMap<String, Integer>>() {
		};
		final Map<String, Integer> params = mapper.readValue(req.getInputStream(), typeReference);

		if (!params.containsKey("start") && !params.containsKey("end")) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect params");
			return;
		}

		final boolean skipExported = Boolean.parseBoolean(req.getParameter("skipExported"));
		final DocumentExportService documentExportService = Imcms.getServices().getDocumentExportService();

		documentExportService.exportDocuments(params.get("start"), params.get("end"), skipExported, user);
		resp.sendError(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final UserDomainObject user = Utility.getLoggedOnUser(req);
		if (!user.isSuperAdmin()) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		final DocumentExportService documentExportService = Imcms.getServices().getDocumentExportService();
		final Path exportZip = documentExportService.getExportZipPath();

		if (Boolean.parseBoolean(req.getParameter("download"))) {
			try (final OutputStream outputStream = resp.getOutputStream();
			     final InputStream inputStream = Files.newInputStream(exportZip)) {
				final byte[] bytes = new byte[1024];

				resp.setContentType("application/zip");
				resp.setContentLength(Math.toIntExact(inputStream.available()));
				resp.setHeader("Content-Disposition", "attachment; filename=" + exportZip.getFileName());

				int length;
				while ((length = inputStream.read(bytes)) >= 0) {
					outputStream.write(bytes, 0, length);
				}

				return;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		req.setAttribute("history", documentExportService.getDocumentExportHistory());
		req.getRequestDispatcher("/imcms/" + user.getLanguageIso639_2() + "/jsp/export/export_summary.jsp").forward(req, resp);
	}
}
