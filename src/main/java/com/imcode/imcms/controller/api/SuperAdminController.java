package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
class SuperAdminController {

    private final String imagesPath;
    private final TextService textService;
    private final AccessService accessService;
    private final String documentationLink;


    SuperAdminController(@Value("${ImagePath}") String imagesPath,
                         TextService textService,
                         AccessService accessService,
                         @Value("${documentation-host}") String documentationLink) {

        this.imagesPath = imagesPath;
        this.textService = textService;
        this.accessService = accessService;
        this.documentationLink = documentationLink;
    }

    @CheckAccess
    @RequestMapping("/manager")
    public ModelAndView goToSuperAdminPage(HttpServletRequest request, ModelAndView mav) {

        mav.setViewName("AdminManager");
        addMinimumModelData(request, mav);
        mav.addObject("imagesPath", imagesPath);
        return mav;
    }

    @RequestMapping("/text")
    @CheckAccess(AccessType.TEXT)
    public ModelAndView editText(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 @RequestParam(value = "lang", required = false) String langCode,
                                 @RequestParam(value = "loop-index", required = false) Integer loopIndex,
                                 @RequestParam(value = "loop-entry-index", required = false) Integer loopEntryIndex,
                                 @RequestParam(value = "return", required = false) String returnUrl,
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        LoopEntryRef loopEntryRef = null;

        if ((loopIndex != null) && (loopEntryIndex != null)) {
            loopEntryRef = new LoopEntryRefDTO(loopIndex, loopEntryIndex);
        }

        mav.setViewName("EditText");


        final String language = (langCode == null) ? Imcms.getUser().getLanguage() : langCode;

        addObjectModelViewData(mav, metaId);
        mav.addObject("textService", textService);
        mav.addObject("loopEntryRef", loopEntryRef);
        mav.addObject("language", language);
        addCommonModelData(metaId, index, returnUrl, request, mav);
        mav.addObject("userLanguage", language);

        return mav;
    }

    private void addObjectModelViewData(ModelAndView mav, Integer metaId) {
        final UserDomainObject user = Imcms.getUser();
        final RestrictedPermission userEditPermission = accessService.getPermission(user, metaId);

        mav.addObject("isAdmin", user.isSuperAdmin());
        mav.addObject("editOptions", userEditPermission);
        mav.addObject("isEditMode", true);
        mav.addObject("currentDocument", new TextDocumentDomainObject(metaId));

    }

    @RequestMapping("/image")
    @CheckAccess(AccessType.IMAGE)
    public ModelAndView editImage(@RequestParam("meta-id") int metaId,
                                  @RequestParam int index,
                                  @RequestParam(value = "lang", required = false) String langCode,
                                  @RequestParam(value = "loop-index", required = false) Integer loopIndex,
                                  @RequestParam(value = "loop-entry-index", required = false) Integer loopEntryIndex,
                                  @RequestParam(value = "return", required = false) String returnUrl,
                                  HttpServletRequest request,
                                  ModelAndView mav) {

        LoopEntryRef loopEntryRef = null;

        if ((loopIndex != null) && (loopEntryIndex != null)) {
            loopEntryRef = new LoopEntryRefDTO(loopIndex, loopEntryIndex);
        }

        final String language = (langCode == null) ? Imcms.getUser().getLanguage() : langCode;

        mav.setViewName("EditImage");

        mav.addObject("loopEntryRef", loopEntryRef);
        mav.addObject("langCode", language);
        mav.addObject("imagesPath", imagesPath);
        addCommonModelData(metaId, index, returnUrl, request, mav);
        mav.addObject("userLanguage", language);
        return mav;
    }

    @RequestMapping("/menu")
    @CheckAccess(AccessType.MENU)
    public ModelAndView editMenu(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 @RequestParam(value = "return", required = false) String returnUrl,
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        mav.setViewName("EditMenu");
        addObjectModelViewData(mav, metaId);
        addCommonModelData(metaId, index, returnUrl, request, mav);

        return mav;
    }

    @RequestMapping("/loop")
    @CheckAccess(AccessType.LOOP)
    public ModelAndView editLoop(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 @RequestParam(value = "return", required = false) String returnUrl,
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        mav.setViewName("EditLoop");
        addCommonModelData(metaId, index, returnUrl, request, mav);

        return mav;
    }

    @RequestMapping("/page-info")
    @CheckAccess(AccessType.DOC_INFO)
    public ModelAndView editDocInfo(@RequestParam("meta-id") int metaId,
                                    @RequestParam(value = "return", required = false) String returnUrl,
                                    HttpServletRequest request, ModelAndView mav) {

        mav.setViewName("EditDocInfo");
        addCommonModelData(metaId, returnUrl, request, mav);

        return mav;
    }

    @CheckAccess(AccessType.DOCUMENT_EDITOR)
    @RequestMapping("/documents")
    public ModelAndView editDocuments(HttpServletRequest request,
                                      @RequestParam(value = "return", required = false) String returnUrl,
                                      ModelAndView mav) {

        mav.setViewName("EditDocuments");
        mav.addObject("accessToDocumentEditor", accessService.hasUserAccessToDocumentEditor(Imcms.getUser()));
        addCommonModelData(returnUrl, request, mav);

        return mav;
    }

    @CheckAccess
    @RequestMapping("/content")
    public ModelAndView editContent(HttpServletRequest request,
                                    @RequestParam(value = "return", required = false) String returnUrl,
                                    ModelAndView mav) {

        mav.setViewName("EditContent");
        mav.addObject("imagesPath", imagesPath);
        addCommonModelData(returnUrl, request, mav);

        return mav;
    }

    private void addCommonModelData(Integer metaId, Integer index, String returnUrl, HttpServletRequest request,
                                    ModelAndView mav) {

        mav.addObject("index", index);
        addCommonModelData(metaId, returnUrl, request, mav);
    }

    private void addCommonModelData(Integer metaId, String returnUrl, HttpServletRequest request,
                                    ModelAndView mav) {

        mav.addObject("targetDocId", metaId);
        addCommonModelData(returnUrl, request, mav);
    }

    private void addCommonModelData(String returnUrl, HttpServletRequest request, ModelAndView mav) {
        mav.addObject("returnUrl", returnUrl);
        addMinimumModelData(request, mav);
    }

    private void addMinimumModelData(HttpServletRequest request, ModelAndView mav) {
        mav.addObject("userLanguage", Imcms.getUser().getLanguage());
        mav.addObject("contextPath", request.getContextPath());
        mav.addObject("disableExternal", true);
        mav.addObject("documentationLink", documentationLink);
    }

}
