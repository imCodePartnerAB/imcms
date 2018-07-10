package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.dto.UserFormData;
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
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
class UserAdministrationController {

    private final UserCreationService userCreationService;

    @Autowired
    public UserAdministrationController(UserCreationService userCreationService) {
        this.userCreationService = userCreationService;
    }

    @GetMapping("/creation")
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
    public ModelAndView createUser(@ModelAttribute UserFormData userData,
                                   ModelAndView modelAndView,
                                   HttpServletRequest request) {
        try {
            userCreationService.createUser(userData);
            final String contextPath = request.getContextPath();
            modelAndView.setView(new RedirectView(contextPath.isEmpty() ? "/" : contextPath));

        } catch (UserValidationException e) {
            setModelStuff(e.validationResult, userData, modelAndView);
        }

        return modelAndView;
    }

    private void setModelStuff(UserValidationResult validationResult, UserFormData userData, ModelAndView modelAndView) {
        final UserDomainObject loggedOnUser = Imcms.getUser();

        modelAndView.setViewName("UserEdit");
        modelAndView.addObject("editedUser", userData);
        modelAndView.addObject("errorMessages", extractErrorMessageKeys(validationResult));
        modelAndView.addObject("isAdmin", loggedOnUser.isSuperAdmin());
        modelAndView.addObject("loggedOnUser", loggedOnUser);
        modelAndView.addObject("userLanguage", loggedOnUser.getLanguage());
    }

    private List<String> extractErrorMessageKeys(UserValidationResult validationResult) {
        final List<String> errorMessageKeys = new ArrayList<>();

        if (!validationResult.isPasswordsEqual()) errorMessageKeys.add("error/passwords_did_not_match");
        if (validationResult.isPasswordTooWeak()) errorMessageKeys.add("error/password_too_weak");
        if (validationResult.isEmptyUserRoles()) errorMessageKeys.add("error/user_must_have_at_least_one_role");
        if (validationResult.isEmptyEmail()) errorMessageKeys.add("error/email_is_missing");
        if (!validationResult.isEmailValid()) errorMessageKeys.add("error/email_is_invalid");
        if (validationResult.isEmailAlreadyTaken()) errorMessageKeys.add("error/email_is_taken");
        if (validationResult.isLoginAlreadyTaken())
            errorMessageKeys.add("error/servlet/AdminUserProps/username_already_exists");

        if (validationResult.isPassword1TooLong()
                || validationResult.isPassword1TooShort()
                || validationResult.isPassword2TooShort()
                || validationResult.isPassword2TooLong())
        {
            errorMessageKeys.add("error/password_length");
        }

        return errorMessageKeys;
    }

}
