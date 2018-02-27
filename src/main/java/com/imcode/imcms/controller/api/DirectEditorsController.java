package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides possibility to go to any editor or manager window directly.
 * Created by Serhii from Ubrainians for Imcode
 * on 26.08.16.
 */
@Controller
@RequestMapping("/edit")
public class DirectEditorsController {

    private final String imagesPath;
    private final TextService textService;

    public DirectEditorsController(@Value("${ImagePath}") String imagesPath,
                                   TextService textService) {

        this.imagesPath = imagesPath;
        this.textService = textService;
    }

    @RequestMapping("/text")
    @CheckAccess(AccessType.TEXT)
    public ModelAndView editText(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 @RequestParam("language-code") String langCode,
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

        mav.addObject("textService", textService);
        mav.addObject("loopEntryRef", loopEntryRef);
        mav.addObject("langCode", langCode);
        addCommonModelData(metaId, index, returnUrl, request, mav);

        return mav;
    }

    @RequestMapping("/image")
    @CheckAccess(AccessType.IMAGE)
    public ModelAndView editImage(@RequestParam("meta-id") int metaId,
                                  @RequestParam int index,
                                  @RequestParam("language-code") String langCode,
                                  @RequestParam(value = "loop-index", required = false) Integer loopIndex,
                                  @RequestParam(value = "loop-entry-index", required = false) Integer loopEntryIndex,
                                  @RequestParam(value = "return", required = false) String returnUrl,
                                  HttpServletRequest request,
                                  ModelAndView mav) {

        LoopEntryRef loopEntryRef = null;

        if ((loopIndex != null) && (loopEntryIndex != null)) {
            loopEntryRef = new LoopEntryRefDTO(loopIndex, loopEntryIndex);
        }

        mav.setViewName("EditImage");

        mav.addObject("loopEntryRef", loopEntryRef);
        mav.addObject("langCode", langCode);
        mav.addObject("imagesPath", imagesPath);
        addCommonModelData(metaId, index, returnUrl, request, mav);

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

    @CheckAccess
    @RequestMapping("/documents")
    public ModelAndView editDocuments(HttpServletRequest request,
                                      @RequestParam(value = "return", required = false) String returnUrl,
                                      ModelAndView mav) {

        mav.setViewName("EditDocuments");
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
        mav.addObject("userLanguage", Imcms.getUser().getLanguage());
        mav.addObject("contextPath", request.getContextPath());
        mav.addObject("returnUrl", returnUrl);
    }

}
