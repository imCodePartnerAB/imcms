package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserCreationService;
import com.imcode.imcms.domain.service.UserEditorService;
import com.imcode.imcms.domain.component.UserLockValidator;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
class UserAdministrationController {

    private final UserCreationService userCreationService;
    private final UserEditorService userEditorService;
    private final UserService userService;
    private final UserLockValidator userLockValidator;

    @Autowired
    public UserAdministrationController(UserCreationService userCreationService,
                                        UserEditorService userEditorService,
                                        UserService userService,
                                        UserLockValidator userLockValidator) {

        this.userCreationService = userCreationService;
        this.userEditorService = userEditorService;
        this.userService = userService;
        this.userLockValidator = userLockValidator;
    }

    @GetMapping("/edition/{userId}")
    public ModelAndView goToEditUser(@PathVariable("userId") Integer userId, HttpServletResponse response) throws IOException {
        final UserDomainObject loggedOnUser = Imcms.getUser();
        if (!loggedOnUser.isSuperAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        final ModelAndView modelAndView = new ModelAndView("UserEdit");

        final UserFormData user = userService.getUserData(userId);
        modelAndView.addObject("editedUser", user);
        modelAndView.addObject("isAdmin", loggedOnUser.isSuperAdmin());
        modelAndView.addObject("loggedOnUser", loggedOnUser);
        modelAndView.addObject("userLanguage", loggedOnUser.getLanguage());
        modelAndView.addObject("isBlockedNow", userLockValidator.isUserBlocked(user));
        return modelAndView;
    }

    @CheckAccess
    @PostMapping("/edit")
    public ModelAndView editUser(@ModelAttribute UserFormData userData,
                                 ModelAndView modelAndView,
                                 HttpServletRequest request) {
        try {
            userEditorService.editUser(userData);
            final String contextPath = request.getContextPath();
            modelAndView.setView(new RedirectView(contextPath.isEmpty() ? "/api/admin/manager" : contextPath));

        } catch (UserValidationException e) {
            modelAndView.setViewName("UserEdit");
            setModelStuff(e.validationResult, userData, modelAndView);
        }

        return modelAndView;
    }

    @GetMapping("/creation")
    public ModelAndView goToCreateUser(HttpServletResponse response) throws IOException {

        final UserDomainObject loggedOnUser = Imcms.getUser();
        if (!loggedOnUser.isSuperAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        final ModelAndView modelAndView = new ModelAndView("UserCreate");

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
            modelAndView.setView(new RedirectView(contextPath.isEmpty() ? "/api/admin/manager" : contextPath));

        } catch (UserValidationException e) {
            modelAndView.setViewName("UserCreate");
            setModelStuff(e.validationResult, userData, modelAndView);
        }

        return modelAndView;
    }

    private void setModelStuff(UserValidationResult validationResult, UserFormData userData, ModelAndView modelAndView) {
        final UserDomainObject loggedOnUser = Imcms.getUser();

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
