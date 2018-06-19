package com.imcode.imcms.controller.core;

import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Document;
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
    private final DelegatingByTypeDocumentService documentService;

    EditDocumentController(final VersionService versionService,
                           final DelegatingByTypeDocumentService documentService) {

        this.versionService = versionService;
        this.documentService = documentService;
    }

    @RequestMapping({"", "/"})
    public void editDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final int metaId = Integer.parseInt(request.getParameter(PARAMETER__META_ID));

        if (versionService.findWorking(metaId) == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Utility.setDefaultHtmlContentType(response);                        // not sure it's still needed
        DocumentHistory.from(request.getSession()).pushIfNotYet(metaId);    // not sure it's still needed

        final Document document = documentService.get(metaId);

        switch (document.getType()) {
            case TEXT: {
                request.setAttribute("isEditMode", "true");
                request.getRequestDispatcher("/api/viewDoc/" + metaId).forward(request, response);
                return;
            }
            case URL:
            case FILE:
                response.sendRedirect(request.getContextPath() + "/api/admin/page-info?meta-id=" + metaId);
        }
    }
}
