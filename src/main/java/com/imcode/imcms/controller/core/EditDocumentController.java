package com.imcode.imcms.controller.core;

import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.servlet.DocumentHistory;
import imcode.util.Utility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/editDoc")
public class EditDocumentController {

    private static final String PARAMETER__META_ID = "meta_id";

    private final VersionService versionService;

    EditDocumentController(final VersionService versionService) {
        this.versionService = versionService;
    }

    @RequestMapping({"", "/"})
    public void editDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final int metaId = Integer.parseInt(request.getParameter(PARAMETER__META_ID));

        final Version documentWorkingVersion = versionService.findWorking(metaId);

        if (documentWorkingVersion == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Utility.setDefaultHtmlContentType(response);
        DocumentHistory.from(request.getSession()).pushIfNotYet(metaId);

        request.setAttribute("isEditMode", "true");

        request.getRequestDispatcher("/api/viewDoc/" + metaId).forward(request, response);
    }
}
