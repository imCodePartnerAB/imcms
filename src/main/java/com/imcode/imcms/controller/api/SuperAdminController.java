package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.AccessRoleType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Controller for super-admin functionality. Provides possibility to go to any
 * editor or manager window directly as independent page.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18.
 */
@Controller
@RequestMapping("/admin")
public class SuperAdminController {

    private final TextService textService;
    private final AccessService accessService;
    private final LanguageService languageService;
    private final VersionService versionService;

    SuperAdminController(TextService textService,
                         AccessService accessService,
                         LanguageService languageService,
                         VersionService versionService) {

        this.textService = textService;
        this.accessService = accessService;
        this.languageService = languageService;
	    this.versionService = versionService;
    }

    @RequestMapping("/manager")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public ModelAndView goToSuperAdminPage(HttpServletRequest request, ModelAndView mav) {
        final UserDomainObject user = Imcms.getUser();

        mav.setViewName("AdminManager");
        addMinimumModelData(request, mav);
        mav.addObject("hasFileAdminAccess", accessService.hasUserFileAdminAccess(user.getId()));
        return mav;
    }

    @RequestMapping("/text")
    @CheckAccess(docPermission = AccessContentType.TEXT)
    public ModelAndView editText(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 @RequestParam(value = "label", required = false) String label,
                                 @RequestParam(value = "lang", required = false) String langCode,
                                 @RequestParam(value = "loop-index", required = false) Integer loopIndex,
                                 @RequestParam(value = "loop-entry-index", required = false) Integer loopEntryIndex,
                                 @RequestParam(value = ImcmsConstants.REQUEST_PARAM__RETURN_URL, required = false) String returnUrl,
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        LoopEntryRef loopEntryRef = null;

        if ((loopIndex != null) && (loopEntryIndex != null)) {
            loopEntryRef = new LoopEntryRefDTO(loopIndex, loopEntryIndex);
        }

        mav.setViewName("EditText");

        final Language language = getLanguage(langCode, request.getCookies());

        addObjectModelViewData(mav, metaId);
        mav.addObject("textService", textService);
        mav.addObject("loopEntryRef", loopEntryRef);
        mav.addObject("language", language.getCode());
        addCommonModelData(metaId, index, label, returnUrl, request, mav);
        mav.addObject("currentDocument", new TextDocumentDomainObject(metaId, language));
        return mav;
    }

    private void addObjectModelViewData(ModelAndView mav, Integer metaId) {
        final UserDomainObject user = Imcms.getUser();
        final RestrictedPermission userEditPermission = accessService.getPermission(user, metaId);

        mav.addObject("editOptions", userEditPermission);
        mav.addObject("isEditMode", true);
    }

    @RequestMapping("/image")
    @CheckAccess(docPermission = AccessContentType.IMAGE)
    public ModelAndView editImage(@RequestParam("meta-id") int metaId,
                                  @RequestParam int index,
                                  @RequestParam(value = "label", required = false) String label,
                                  @RequestParam(value = "lang", required = false) String langCode,
                                  @RequestParam(value = "loop-index", required = false) Integer loopIndex,
                                  @RequestParam(value = "loop-entry-index", required = false) Integer loopEntryIndex,
                                  @RequestParam(value = ImcmsConstants.REQUEST_PARAM__RETURN_URL, required = false) String returnUrl,
                                  HttpServletRequest request,
                                  ModelAndView mav) {

        LoopEntryRef loopEntryRef = null;

        if ((loopIndex != null) && (loopEntryIndex != null)) {
            loopEntryRef = new LoopEntryRefDTO(loopIndex, loopEntryIndex);
        }

        final Language language =getLanguage(langCode, request.getCookies());

        mav.setViewName("EditImage");

        mav.addObject("loopEntryRef", loopEntryRef);
        mav.addObject("langCode", language.getCode());
        addCommonModelData(metaId, index, label, returnUrl, request, mav);
        mav.addObject("currentDocument", new TextDocumentDomainObject(metaId, language));
        return mav;
    }

    @RequestMapping("/menu")
    @CheckAccess(docPermission = AccessContentType.MENU)
    public ModelAndView editMenu(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 @RequestParam(value = "label", required = false) String label,
                                 @RequestParam(value = "lang", required = false) String langCode,
                                 @RequestParam(value = ImcmsConstants.REQUEST_PARAM__RETURN_URL, required = false) String returnUrl,
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        mav.setViewName("EditMenu");
        addObjectModelViewData(mav, metaId);
        addCommonModelData(metaId, index, label, returnUrl, request, mav);
        mav.addObject("currentDocument", new TextDocumentDomainObject(metaId, getLanguage(langCode, request.getCookies())));

        return mav;
    }

    @RequestMapping("/loop")
    @CheckAccess(docPermission = AccessContentType.LOOP)
    public ModelAndView editLoop(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 @RequestParam(value = "label", required = false) String label,
                                 @RequestParam(value = "lang", required = false) String langCode,
                                 @RequestParam(value = ImcmsConstants.REQUEST_PARAM__RETURN_URL, required = false) String returnUrl,
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        mav.setViewName("EditLoop");
	    addCommonModelData(metaId, index, label, returnUrl, request, mav);
        mav.addObject("currentDocument", new TextDocumentDomainObject(metaId, getLanguage(langCode, request.getCookies())));

        return mav;
    }

    @RequestMapping("/page-info")
    @CheckAccess(docPermission = AccessContentType.DOC_INFO)
    public ModelAndView editDocInfo(@RequestParam("meta-id") int metaId,
                                    @RequestParam(value = ImcmsConstants.REQUEST_PARAM__RETURN_URL, required = false) String returnUrl,
                                    HttpServletRequest request, ModelAndView mav) {

        mav.setViewName("EditDocInfo");
        addCommonModelData(metaId, returnUrl, request, mav);
        return mav;
    }

    @RequestMapping("/documents")
    @CheckAccess(role = AccessRoleType.DOCUMENT_EDITOR)
    public ModelAndView editDocuments(HttpServletRequest request,
                                      @RequestParam(value = ImcmsConstants.REQUEST_PARAM__RETURN_URL, required = false) String returnUrl,
                                      @RequestParam(value = "lang", required = false) String langCode,
                                      ModelAndView mav) {

        mav.setViewName("EditDocuments");
        mav.addObject("accessToDocumentEditor", accessService.getTotalRolePermissionsByUser(Imcms.getUser()).isAccessToDocumentEditor());
        addCommonModelData(returnUrl, request, mav);
        mav.addObject("currentDocument", new TextDocumentDomainObject(getLanguage(langCode, request.getCookies())));

        return mav;
    }

    @RequestMapping("/content")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public ModelAndView editContent(HttpServletRequest request,
                                    @RequestParam(value = ImcmsConstants.REQUEST_PARAM__RETURN_URL, required = false) String returnUrl,
                                    ModelAndView mav) {

        mav.setViewName("EditContent");
        addCommonModelData(returnUrl, request, mav);

        return mav;
    }

    private void addCommonModelData(Integer metaId, Integer index, String label, String returnUrl, HttpServletRequest request,
                                    ModelAndView mav) {

        mav.addObject("index", index);
        mav.addObject("label", label);
        addCommonModelData(metaId, returnUrl, request, mav);
    }

    private void addCommonModelData(Integer metaId, String returnUrl, HttpServletRequest request,
                                    ModelAndView mav) {

        mav.addObject("targetDocId", metaId);
        //duplicate same attributes because SuperAdminController and ViewDocumentController get docId in different ways
        mav.addObject("accessToPublishCurrentDoc", accessService.hasUserPublishAccess(Imcms.getUser(), metaId));
        mav.addObject("hasNewerVersion", versionService.hasNewerVersion(metaId));

        addCommonModelData(returnUrl, request, mav);
    }

    private void addCommonModelData(String returnUrl, HttpServletRequest request, ModelAndView mav) {
        mav.addObject("returnUrl", returnUrl);
        addMinimumModelData(request, mav);
    }

    private void addMinimumModelData(HttpServletRequest request, ModelAndView mav) {
        mav.addObject("disableExternal", true);
    }

    private Language getLanguage(String langCode, Cookie[] cookies){
        return (langCode == null) ? Utility.getUserLanguageFromCookie(cookies) : languageService.findByCode(langCode);
    }
}
