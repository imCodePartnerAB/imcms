package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.ContentManagementSystem;
import imcode.server.Imcms;
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

    /**
     * Method goes to elements edition jsp.
     *
     * @param metaId  - document meta_id
     * @param textNo  - [optional] - desired textNo to edit
     * @param imageNo - [optional] - desired imageNo to edit
     * @param menuNo  - [optional] - desired menuNo to edit
     * @return ModelAndView of element editing jsp with all parameters.
     */
    @RequestMapping
    public ModelAndView editText(@RequestParam Integer metaId,
                                 @RequestParam(required = false) Integer textNo,
                                 @RequestParam(required = false) Integer imageNo,
                                 @RequestParam(required = false) Integer menuNo,
                                 HttpServletRequest request) {

        ContentManagementSystem cms = Imcms.fromRequest(request);

        ModelAndView mav = new ModelAndView("editElement");
        mav.addObject("metaId", metaId);
        if (textNo != null) {
            mav.addObject("textNo", textNo);

        } else if (imageNo != null) {
            mav.addObject("imageNo", imageNo);

        } else if (menuNo != null) {
            mav.addObject("menuNo", menuNo);
        }

        mav.addObject("user", cms.getCurrentUser());
        return mav;
    }
}
