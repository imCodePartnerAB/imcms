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
 * Controller provides possibility to go to page with elements editing without going to it's docs.
 * Created by Serhii from Ubrainians for Imcode
 * on 26.08.16.
 */
@Controller
@RequestMapping("/edit")
public class EditElementController {

    private final String imagesPath;
    private final TextService textService;

    public EditElementController(@Value("${ImagePath}") String imagesPath,
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
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        LoopEntryRef loopEntryRef = null;

        if ((loopIndex != null) && (loopEntryIndex != null)) {
            loopEntryRef = new LoopEntryRefDTO(loopIndex, loopEntryIndex);
        }

        mav.setViewName("EditText");

        mav.addObject("textService", textService);
        mav.addObject("targetDocId", metaId);
        mav.addObject("index", index);
        mav.addObject("loopEntryRef", loopEntryRef);
        mav.addObject("userLanguage", Imcms.getUser().getLanguage());
        mav.addObject("langCode", langCode);
        mav.addObject("contextPath", request.getContextPath());

        return mav;
    }

    @RequestMapping("/image")
    @CheckAccess(AccessType.IMAGE)
    public ModelAndView editImage(@RequestParam("meta-id") int metaId,
                                  @RequestParam int index,
                                  @RequestParam("language-code") String langCode,
                                  @RequestParam(value = "loop-index", required = false) Integer loopIndex,
                                  @RequestParam(value = "loop-entry-index", required = false) Integer loopEntryIndex,
                                  HttpServletRequest request,
                                  ModelAndView mav) {

        LoopEntryRef loopEntryRef = null;

        if ((loopIndex != null) && (loopEntryIndex != null)) {
            loopEntryRef = new LoopEntryRefDTO(loopIndex, loopEntryIndex);
        }

        mav.setViewName("EditImage");

        mav.addObject("targetDocId", metaId);
        mav.addObject("index", index);
        mav.addObject("loopEntryRef", loopEntryRef);
        mav.addObject("userLanguage", Imcms.getUser().getLanguage());
        mav.addObject("langCode", langCode);
        mav.addObject("contextPath", request.getContextPath());
        mav.addObject("imagesPath", imagesPath);

        return mav;
    }

    @RequestMapping("/menu")
    @CheckAccess(AccessType.MENU)
    public ModelAndView editMenu(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        mav.setViewName("EditMenu");

        mav.addObject("targetDocId", metaId);
        mav.addObject("index", index);
        mav.addObject("userLanguage", Imcms.getUser().getLanguage());
        mav.addObject("contextPath", request.getContextPath());

        return mav;
    }

    @RequestMapping("/loop")
    @CheckAccess(AccessType.LOOP)
    public ModelAndView editLoop(@RequestParam("meta-id") int metaId,
                                 @RequestParam int index,
                                 HttpServletRequest request,
                                 ModelAndView mav) {

        mav.setViewName("EditLoop");

        mav.addObject("targetDocId", metaId);
        mav.addObject("index", index);
        mav.addObject("userLanguage", Imcms.getUser().getLanguage());
        mav.addObject("contextPath", request.getContextPath());

        return mav;
    }

}
