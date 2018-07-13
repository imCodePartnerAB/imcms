package com.imcode.imcms.controller.core;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/login**", "/login/**"})
public class UserLoginController {

    @GetMapping
    public ModelAndView goToLoginPage(ModelAndView modelAndView) {
        modelAndView.setViewName("Login");
        return modelAndView;
    }

}
