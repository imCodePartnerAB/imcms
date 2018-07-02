package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.UserData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserCreationService;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
class UserAdministrationController {

    private final UserCreationService userCreationService;

    @Autowired
    public UserAdministrationController(UserCreationService userCreationService) {
        this.userCreationService = userCreationService;
    }

    @GetMapping("/create")
    public ModelAndView goToCreateUser() {

        final UserDomainObject loggedOnUser = Imcms.getUser();
        final ModelAndView modelAndView = new ModelAndView("UserEdit");

        modelAndView.addObject("isAdmin", loggedOnUser.isSuperAdmin());
        modelAndView.addObject("loggedOnUser", loggedOnUser);
        modelAndView.addObject("userLanguage", loggedOnUser.getLanguage());
        return modelAndView;
    }

    @CheckAccess
    @PostMapping("/create")
    public ModelAndView createUser(@ModelAttribute UserData userData, ModelAndView modelAndView) {

        try {
            userCreationService.createUser(userData);
        } catch (UserValidationException e) {
            e.printStackTrace();
        }

        final UserDomainObject loggedOnUser = Imcms.getUser();

        modelAndView.setViewName("UserEdit");
        modelAndView.addObject("isAdmin", loggedOnUser.isSuperAdmin());
        modelAndView.addObject("loggedOnUser", loggedOnUser);
        modelAndView.addObject("userLanguage", loggedOnUser.getLanguage());

        return modelAndView;
    }

}
