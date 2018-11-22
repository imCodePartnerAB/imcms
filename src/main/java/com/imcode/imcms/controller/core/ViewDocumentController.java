package com.imcode.imcms.controller.core;

import com.imcode.imcms.api.DocumentLanguageDisabledException;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.imcode.imcms.mapping.DocumentMeta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static imcode.server.ImcmsConstants.*;
import static javax.servlet.RequestDispatcher.FORWARD_REQUEST_URI;

/**
 * General controller for document viewing in any mode.
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 18.09.17.
 */
@Controller
@RequestMapping(VIEW_DOC_PATH)
public class ViewDocumentController {

    private final DocumentMapper documentMapper;
    private final VersionService versionService;
    private final CommonContentService commonContentService;
    private final AccessService accessService;
    private final PathMatcher pathMatcher;
    private final String imagesPath;
    private final String version;
    private final boolean isVersioningAllowed;

    ViewDocumentController(DocumentMapper documentMapper,
                           VersionService versionService,
                           CommonContentService commonContentService,
                           AccessService accessService,
                           PathMatcher pathMatcher,
                           @Value("${ImagePath}") String imagesPath,
                           @Value("${imcms.version}") String version,
                           @Value("${document.versioning:true}") boolean isVersioningAllowed) {

        this.documentMapper = documentMapper;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.accessService = accessService;
        this.pathMatcher = pathMatcher;
        this.imagesPath = imagesPath;
        this.version = version;
        this.isVersioningAllowed = isVersioningAllowed;
    }

    @RequestMapping({"", "/"})
    public ModelAndView goToStartPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mav)
            throws ServletException, IOException {

        final String docId = String.valueOf(Imcms.getServices().getSystemData().getStartDocument());
        final TextDocumentDomainObject textDocument = getTextDocument(docId, getLanguageCode(), request);

        return processDocView(textDocument, request, response, mav);
    }

    @RequestMapping("/**")
    public ModelAndView getDocument(HttpServletRequest request, HttpServletResponse response, ModelAndView mav)
            throws ServletException, IOException {

        final String urlPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        final String docIdentifier = pathMatcher.extractPathWithinPattern(urlPattern, request.getPathInfo());

        final TextDocumentDomainObject textDocument = getTextDocument(docIdentifier, getLanguageCode(), request);

        return processDocView(textDocument, request, response, mav);
    }

    private ModelAndView processDocView(TextDocumentDomainObject textDocument,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        ModelAndView mav) throws ServletException, IOException {

        final UserDomainObject user = Imcms.getUser();

        if (user.isDefaultUser() && !textDocument.isPublished()
                || !user.isAdmin() && textDocument.hasDisapprovedStatus()) {

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final int docId = textDocument.getId();

        final RestrictedPermission userEditPermission = accessService.getEditPermission(user, docId);

        final String isEditModeStr = Objects.toString(request.getAttribute("isEditMode"), "false");
        final boolean isEditMode = Boolean.parseBoolean(isEditModeStr);

        final boolean isPreviewMode = isVersioningAllowed
                && Boolean.parseBoolean(request.getParameter(REQUEST_PARAM__WORKING_PREVIEW));

        if ((isEditMode || isPreviewMode) && !hasUserContentEditAccess(userEditPermission)) {

            final Object loginTarget = Optional
                    .ofNullable(request.getAttribute(FORWARD_REQUEST_URI))
                    .orElse(request.getRequestURL());

            Utility.forwardToLogin(
                    request, response, HttpServletResponse.SC_FORBIDDEN, new StringBuffer(loginTarget.toString())
            );
        }

        final String viewName = textDocument.getTemplateName();
        final String docLangCode = textDocument.getLanguage().getCode();
        final Version latestDocVersion = versionService.getLatestVersion(docId);

        final List<CommonContent> enabledCommonContents =
                commonContentService.getOrCreateCommonContents(docId, latestDocVersion.getNo())
                        .stream()
                        .filter(CommonContent::isEnabled)
                        .collect(Collectors.toList());

        if (enabledCommonContents.size() == 0) {
            throw new DocumentLanguageDisabledException(textDocument, textDocument.getLanguage());
        }

        final Optional<CommonContent> optionalCommonContent = enabledCommonContents.stream()
                .filter(commonContent -> commonContent.getLanguage().getCode().equals(docLangCode))
                .findFirst();

        final String language;

        if (!optionalCommonContent.isPresent()) {
            if (textDocument.getDisabledLanguageShowMode().equals(SHOW_IN_DEFAULT_LANGUAGE)) {
                language = user.getLanguage();
            } else {
                throw new DocumentLanguageDisabledException(textDocument, textDocument.getLanguage());
            }
        } else {
            language = docLangCode;
        }

        mav.setViewName(viewName);

        mav.addObject("userLanguage", user.getLanguage());
        mav.addObject("currentDocument", textDocument);
        mav.addObject("language", language);
        mav.addObject("isAdmin", user.isSuperAdmin());
        mav.addObject("isEditMode", isEditMode);
        mav.addObject("contextPath", request.getContextPath());
        mav.addObject("imagesPath", imagesPath);
        mav.addObject("isVersioningAllowed", isVersioningAllowed);
        mav.addObject("isPreviewMode", isPreviewMode);
        mav.addObject("hasNewerVersion", versionService.hasNewerVersion(docId));
        mav.addObject("version", version);
        mav.addObject("editOptions", userEditPermission);
        mav.addObject("isDocNew", textDocument.hasNewStatus());

        return mav;
    }

    private TextDocumentDomainObject getTextDocument(String docId, String languageCode, HttpServletRequest request) {
        return Optional.ofNullable(documentMapper.<TextDocumentDomainObject>getVersionedDocument(docId, languageCode, request))
                .orElseThrow(() -> new DocumentNotExistException(docId));
    }

    private String getLanguageCode() {
        return Imcms.getLanguage().getCode();
    }

    private boolean hasUserContentEditAccess(final RestrictedPermission permission) {
        return permission.isEditImage() || permission.isEditLoop()
                || permission.isEditMenu() || permission.isEditText();
    }
}
