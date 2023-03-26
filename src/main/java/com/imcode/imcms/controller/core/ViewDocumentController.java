package com.imcode.imcms.controller.core;

import com.imcode.imcms.api.exception.DocumentLanguageDisabledException;
import com.imcode.imcms.domain.component.PublicDocumentsCache;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentWasteBasketService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.model.RolePermissions;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.imcode.imcms.mapping.DocumentMeta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static com.imcode.imcms.persistence.entity.Meta.Permission.NONE;
import static imcode.server.ImcmsConstants.REQUEST_PARAM__WORKING_PREVIEW;
import static imcode.server.ImcmsConstants.VIEW_DOC_PATH;

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
    private final DocumentWasteBasketService documentWasteBasketService;
    private final PathMatcher pathMatcher;
    private final String imagesPath;
    private final String version;
    private final boolean isVersioningAllowed;
    private final PublicDocumentsCache publicDocumentsCache;

    ViewDocumentController(DocumentMapper documentMapper,
                           VersionService versionService,
                           CommonContentService commonContentService,
                           AccessService accessService,
                           DocumentWasteBasketService documentWasteBasketService,
                           PathMatcher pathMatcher,
                           @Qualifier("storageImagePath") String imagesPath,
                           @Value("${imcms.version}") String version,
                           @Value("${document.versioning:true}") boolean isVersioningAllowed,
                           PublicDocumentsCache publicDocumentsCache) {

        this.documentMapper = documentMapper;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.accessService = accessService;
        this.documentWasteBasketService = documentWasteBasketService;
        this.pathMatcher = pathMatcher;
        this.imagesPath = imagesPath;
        this.version = version;
        this.isVersioningAllowed = isVersioningAllowed;
        this.publicDocumentsCache = publicDocumentsCache;
    }

    @RequestMapping({"", "/"})
    public ModelAndView goToStartPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mav)
            throws IOException {

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
                                        ModelAndView mav) throws IOException {

        final UserDomainObject user = Imcms.getUser();

        if (user.isDefaultUser() && !textDocument.isPublished()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final int docId = textDocument.getId();

        final RestrictedPermission userContentPermission = accessService.getPermission(user, docId);
        final RolePermissions rolePermissions = accessService.getTotalRolePermissionsByUser(user);

        final String isEditModeStr = Objects.toString(request.getAttribute("isEditMode"), "false");
        final boolean isEditMode = Boolean.parseBoolean(isEditModeStr);

        final boolean isPreviewMode = isVersioningAllowed
                && Boolean.parseBoolean(request.getParameter(REQUEST_PARAM__WORKING_PREVIEW));

        if (!isVersioningAllowed) {
	        publicDocumentsCache.invalidateDoc(docId, Collections.singletonList(textDocument.getAlias()));
        }

        if (((isEditMode || isPreviewMode) && !hasUserContentEditAccess(userContentPermission))
                || (!hasUserViewAccess(userContentPermission) && !textDocument.isVisible())
                || (!user.isSuperAdmin() && documentWasteBasketService.isDocumentInWasteBasket(docId))) {
            response.sendError(404, String.valueOf(HttpServletResponse.SC_NOT_FOUND));
            return null;
        }

        final String viewName = textDocument.getTemplateName();
        final String docLangCode = textDocument.getLanguage().getCode();
        final Version latestDocVersion = versionService.getLatestVersion(docId);

        final List<CommonContent> enabledCommonContents =
                commonContentService.getOrCreateCommonContents(docId, latestDocVersion.getNo())
                        .stream()
		                .filter(commonContent -> isEditMode || commonContent.isEnabled())
                        .collect(Collectors.toList());

        if (enabledCommonContents.size() == 0) {
            throw new DocumentLanguageDisabledException(textDocument, textDocument.getLanguage());
        }

        final Optional<CommonContent> optionalCommonContent = enabledCommonContents.stream()
                .filter(commonContent -> commonContent.getLanguage().getCode().equals(docLangCode))
                .findFirst();

        final String language;
	    if (optionalCommonContent.isEmpty()) {
		    if (user.isSuperAdmin() || textDocument.getDisabledLanguageShowMode().equals(SHOW_IN_DEFAULT_LANGUAGE)) {
			    language = Imcms.getServices().getLanguageService().getDefaultLanguage().getCode();
		    } else {
			    response.sendError(404, String.valueOf(HttpServletResponse.SC_NOT_FOUND));
			    return null;
		    }
	    } else {
		    language = docLangCode;
	    }

        mav.setViewName(viewName);

        mav.addObject("userLanguage", user.getLanguage());
        mav.addObject("currentDocument", textDocument);
        mav.addObject("language", language);
        mav.addObject("isSuperAdmin", user.isSuperAdmin());
        mav.addObject("isEditMode", isEditMode);
        mav.addObject("contextPath", request.getContextPath());
        mav.addObject("imagesPath", imagesPath);
        mav.addObject("isVersioningAllowed", isVersioningAllowed);
        mav.addObject("isPreviewMode", isPreviewMode);
        mav.addObject("hasNewerVersion", versionService.hasNewerVersion(docId));
        mav.addObject("version", version);
        mav.addObject("editOptions", userContentPermission);
        mav.addObject("isDocNew", textDocument.hasNewStatus());
        mav.addObject("accessToAdminPages", rolePermissions.isAccessToAdminPages());
        mav.addObject("accessToDocumentEditor", rolePermissions.isAccessToDocumentEditor());
        mav.addObject("accessToPublishCurrentDoc", accessService.hasUserPublishAccess(user, docId));

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

    private boolean hasUserViewAccess(final RestrictedPermission permission) {
        return !permission.getPermission().getName().equals(NONE.getName());
    }
}
