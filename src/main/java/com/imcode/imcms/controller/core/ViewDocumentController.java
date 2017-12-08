package com.imcode.imcms.controller.core;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

import static imcode.server.ImcmsConstants.REQUEST_PARAM__WORKING_PREVIEW;

/**
 * General controller for document viewing in any mode.
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 18.09.17.
 */
@Controller
@RequestMapping("/viewDoc")
public class ViewDocumentController {

    private final DocumentMapper documentMapper;
    private final DocumentService documentService;
    private final VersionService versionService;
    private final String imagesPath;
    private final String version;
    private final boolean isVersioningAllowed;

    ViewDocumentController(DocumentMapper documentMapper,
                           DocumentService documentService,
                           VersionService versionService,
                           @Value("${ImagePath}") String imagesPath,
                           @Value("${imcms.version}") String version,
                           @Value("${document.versioning:true}") boolean isVersioningAllowed) {
        this.documentMapper = documentMapper;
        this.documentService = documentService;
        this.versionService = versionService;
        this.imagesPath = imagesPath;
        this.version = version;
        this.isVersioningAllowed = isVersioningAllowed;
    }

    @RequestMapping({"", "/"})
    public ModelAndView goToStartPage(HttpServletRequest request, ModelAndView mav) {

        final String docId = String.valueOf(ImcmsConstants.DEFAULT_START_DOC_ID);
        final TextDocumentDomainObject textDocument = getTextDocument(docId, getDefaultLanguageCode(), request);

        return processDocView(textDocument, request, mav);
    }

    @RequestMapping("/{docIdentifier}")
    public ModelAndView getDocument(@PathVariable("docIdentifier") String docIdentifier,
                                    @RequestParam(value = "language-code", required = false) String languageCode,
                                    HttpServletRequest request,
                                    ModelAndView mav) {

        final String languageCodeOrDefault = getLanguageCodeOrDefault(languageCode);
        final TextDocumentDomainObject textDocument = getTextDocument(docIdentifier, languageCodeOrDefault, request);
        return processDocView(textDocument, request, mav);
    }

    @RequestMapping("/publish-document/{docIdentifier}")
    public RedirectView publishDocument(@PathVariable("docIdentifier") int docId) {
        // todo: create annotation instead of copying this each time!
        final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }

        documentService.publishDocument(docId, user.getId());

        return new RedirectView("/" + docId, true);
    }

    private ModelAndView processDocView(TextDocumentDomainObject textDocument, HttpServletRequest request,
                                        ModelAndView mav) {

        final String isEditModeStr = Objects.toString(request.getAttribute("isEditMode"), "false");
        final boolean isEditMode = Boolean.parseBoolean(isEditModeStr);

        final boolean isPreviewMode = isVersioningAllowed
                && Boolean.parseBoolean(request.getParameter(REQUEST_PARAM__WORKING_PREVIEW));

        final String viewName = textDocument.getTemplateName();

        mav.setViewName(viewName);

        mav.addObject("currentDocument", textDocument);
        mav.addObject("language", textDocument.getLanguage().getCode());
        mav.addObject("isAdmin", Imcms.getUser().isAdmin());
        mav.addObject("isEditMode", isEditMode);
        mav.addObject("contextPath", request.getContextPath());
        mav.addObject("imagesPath", imagesPath);
        mav.addObject("isVersioningAllowed", isVersioningAllowed);
        mav.addObject("isPreviewMode", isPreviewMode);
        mav.addObject("hasNewerVersion", versionService.hasNewerVersion(textDocument.getId()));
        mav.addObject("version", version);

        return mav;
    }

    private TextDocumentDomainObject getTextDocument(String docId, String languageCode, HttpServletRequest request) {
        return Optional.ofNullable(documentMapper.<TextDocumentDomainObject>getVersionedDocument(docId, languageCode, request))
                .orElseThrow(() -> new DocumentNotExistException(docId));
    }

    private String getDefaultLanguageCode() {
        return Imcms.getUser().getDocGetterCallback().getLanguage().getCode();
    }

    private String getLanguageCodeOrDefault(String languageCode) {
        return Optional.ofNullable(languageCode)
                .orElseGet(this::getDefaultLanguageCode);
    }

}
