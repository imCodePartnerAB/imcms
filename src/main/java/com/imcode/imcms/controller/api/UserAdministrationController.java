package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.component.UserLockValidator;
import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/user")
class UserAdministrationController {

    private final UserCreationService userCreationService;
    private final UserEditorService userEditorService;
    private final UserService userService;
    private final UserRolesService userRolesService;
    private final UserLockValidator userLockValidator;
    private final LanguageService languageService;

    @Autowired
    public UserAdministrationController(UserCreationService userCreationService,
                                        UserEditorService userEditorService,
                                        UserService userService,
                                        UserRolesService userRolesService,
                                        UserLockValidator userLockValidator,
                                        LanguageService languageService) {

        this.userCreationService = userCreationService;
        this.userEditorService = userEditorService;
        this.userService = userService;
        this.userRolesService = userRolesService;
        this.userLockValidator = userLockValidator;
        this.languageService = languageService;
    }

    @GetMapping("/edition/{userId}")
    public ModelAndView goToEditUser(@PathVariable("userId") Integer userId) {
        final UserDomainObject loggedOnUser = Imcms.getUser();
        final UserFormData user = userService.getUserData(userId);

        if(!loggedOnUser.isSuperAdmin() &&
                IntStream.of(user.getRoleIds()).anyMatch(id -> id == Roles.SUPER_ADMIN.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit Superadmin");
        }

        final ModelAndView modelAndView = new ModelAndView("UserEdit");
        modelAndView.addObject("editedUser", user);
        modelAndView.addObject("isSuperAdmin", loggedOnUser.isSuperAdmin());
        modelAndView.addObject("loggedOnUser", loggedOnUser);
        modelAndView.addObject("userLanguage", loggedOnUser.getLanguage());
        modelAndView.addObject("availableLanguages", languageService.getAvailableLanguages());
        modelAndView.addObject("isBlockedNow", userLockValidator.isUserBlocked(user));
        return modelAndView;
    }

    @PostMapping("/edit")
    public ModelAndView editUser(@ModelAttribute UserFormData userData,
                                 ModelAndView modelAndView,
                                 HttpServletRequest request) {
        if(!Imcms.getUser().isSuperAdmin() && !validateRoles(userData)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit Superadmin");
        }

        try {
            userData.setPassword(Utility.unescapeValue(userData.getPassword()));
            userData.setPassword2(Utility.unescapeValue(userData.getPassword2()));

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
    public ModelAndView goToCreateUser() {
        final UserDomainObject loggedOnUser = Imcms.getUser();
        final ModelAndView modelAndView = new ModelAndView("UserCreate");

        modelAndView.addObject("isSuperAdmin", loggedOnUser.isSuperAdmin());
        modelAndView.addObject("loggedOnUser", loggedOnUser);
        modelAndView.addObject("userLanguage", loggedOnUser.getLanguage());
        modelAndView.addObject("availableLanguages", languageService.getAvailableLanguages());
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createUser(@ModelAttribute UserFormData userData,
                                   ModelAndView modelAndView,
                                   HttpServletRequest request) {
        if(!Imcms.getUser().isSuperAdmin() && !validateRoles(userData)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot create Superadmin");
        }

        try {
            userData.setPassword(Utility.unescapeValue(userData.getPassword()));
            userData.setPassword2(Utility.unescapeValue(userData.getPassword2()));

            userCreationService.createUser(userData);

            final String contextPath = request.getContextPath();
            modelAndView.setView(new RedirectView(contextPath.isEmpty() ? "/api/admin/manager" : contextPath));
        } catch (UserValidationException e) {
            modelAndView.setViewName("UserCreate");
            setModelStuff(e.validationResult, userData, modelAndView);
        }

        return modelAndView;
    }

    /**
     * Only Superadmin can editing others Superadmin and add/remove Superadmin role.
     */
    private boolean validateRoles(UserFormData userData){
        boolean valid;

        final Integer userId = userData.getId();
        final int[] roleIds = userData.getRoleIds() != null ? userData.getRoleIds() : new int[]{};
        boolean containSuperAdminInForm = Arrays.stream(roleIds).anyMatch(roleId -> Roles.SUPER_ADMIN.getId().equals(roleId));

        if(userId != null){
            boolean containSuperAdmin = userRolesService.getRoleIdsByUser(userId).stream()
                    .anyMatch(id -> Roles.SUPER_ADMIN.getId().equals(id));
            valid = !containSuperAdmin && !containSuperAdminInForm;
        }else{
            valid = !containSuperAdminInForm;
        }

        return valid;
    }

    private void setModelStuff(UserValidationResult validationResult, UserFormData userData, ModelAndView modelAndView) {
        final UserDomainObject loggedOnUser = Imcms.getUser();

        modelAndView.addObject("editedUser", userData);
        modelAndView.addObject("errorMessages", extractErrorMessageKeys(validationResult));
        modelAndView.addObject("isSuperAdmin", loggedOnUser.isSuperAdmin());
        modelAndView.addObject("loggedOnUser", loggedOnUser);
        modelAndView.addObject("userLanguage", loggedOnUser.getLanguage());
        modelAndView.addObject("availableLanguages", languageService.getAvailableLanguages());
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
