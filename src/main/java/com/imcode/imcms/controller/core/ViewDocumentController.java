package com.imcode.imcms.controller.core;

import com.imcode.imcms.api.DocumentLanguageDisabledException;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.LanguageMapper;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.imcode.imcms.mapping.DocumentMeta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
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
    public ModelAndView goToStartPage(HttpServletRequest request, ModelAndView mav) {

        final String docId = String.valueOf(ImcmsConstants.DEFAULT_START_DOC_ID);
        final TextDocumentDomainObject textDocument = getTextDocument(docId, getLanguageCode(), request);

        return processDocView(textDocument, request, mav);
    }

    @RequestMapping("/**")
    public ModelAndView getDocument(HttpServletRequest request, ModelAndView mav) {

        final String urlPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        final String docIdentifier = pathMatcher.extractPathWithinPattern(urlPattern, request.getPathInfo());

        final TextDocumentDomainObject textDocument = getTextDocument(docIdentifier, getLanguageCode(), request);

        return processDocView(textDocument, request, mav);
    }

    private ModelAndView processDocView(TextDocumentDomainObject textDocument, HttpServletRequest request,
                                        ModelAndView mav) {

        final String isEditModeStr = Objects.toString(request.getAttribute("isEditMode"), "false");
        final boolean isEditMode = Boolean.parseBoolean(isEditModeStr);

        final boolean isPreviewMode = isVersioningAllowed
                && Boolean.parseBoolean(request.getParameter(REQUEST_PARAM__WORKING_PREVIEW));

        final String viewName = textDocument.getTemplateName();
        final int docId = textDocument.getId();
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
        final UserDomainObject user = Imcms.getUser();

        if (!optionalCommonContent.isPresent()) {
            if (textDocument.getDisabledLanguageShowMode().equals(SHOW_IN_DEFAULT_LANGUAGE)) {
                language = LanguageMapper.convert639_2to639_1(user.getLanguageIso639_2());
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
        mav.addObject("editOptions", accessService.getEditPermission(user.getId(), docId));

        return mav;
    }

    private TextDocumentDomainObject getTextDocument(String docId, String languageCode, HttpServletRequest request) {
        return Optional.ofNullable(documentMapper.<TextDocumentDomainObject>getVersionedDocument(docId, languageCode, request))
                .orElseThrow(() -> new DocumentNotExistException(docId));
    }

    private String getLanguageCode() {
        return Imcms.getLanguage().getCode();
    }
}
