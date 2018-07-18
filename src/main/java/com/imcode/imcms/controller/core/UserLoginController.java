package com.imcode.imcms.controller.core;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static com.imcode.imcms.servlet.VerifyUser.*;

@Controller
@RequestMapping({"/login**", "/login/**"})
class UserLoginController {

    @RequestMapping
    public ModelAndView goToLoginPage(@RequestParam(value = REQUEST_PARAMETER__NEXT_URL, required = false) String nextUrl,
                                      @RequestParam(value = REQUEST_PARAMETER__NEXT_META, required = false) String nextMeta,
                                      HttpServletRequest request) {

        final ModelAndView modelAndView = new ModelAndView("Login");

        String nextUrlParam = null;

        if (nextUrl != null) {
            nextUrlParam = nextUrl;

        } else if (nextMeta != null) {
            nextUrlParam = request.getContextPath() + "/" + nextMeta;
        }

        if (nextUrlParam != null) {
            modelAndView.addObject(REQUEST_PARAMETER__NEXT_URL, nextUrlParam);
        }

        return modelAndView;
    }

}
