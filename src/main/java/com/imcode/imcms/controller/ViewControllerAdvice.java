package com.imcode.imcms.controller;

import com.imcode.imcms.controller.api.SuperAdminController;
import com.imcode.imcms.controller.api.UserAdministrationController;
import com.imcode.imcms.controller.core.MultiFactorAuthenticationController;
import com.imcode.imcms.controller.core.UserLoginController;
import com.imcode.imcms.controller.core.ViewDocumentController;
import com.imcode.imcms.domain.service.LanguageService;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(assignableTypes = {
        ViewDocumentController.class,
        SuperAdminController.class,
        UserAdministrationController.class,
        UserLoginController.class,
        MultiFactorAuthenticationController.class
})
public class ViewControllerAdvice {

    private final LanguageService languageService;
    private final String imagesPath;
    private final String documentationLink;
    private final boolean isImageEditorAltTextRequired;

    public ViewControllerAdvice(LanguageService languageService,
                                @Qualifier("storageImagePath") String imagesPath,
                                @Value("${documentation-host}") String documentationLink,
                                @Value("${image.editor.alt-text.required}") boolean isImageEditorAltTextRequired){
        this.languageService = languageService;
        this.imagesPath = imagesPath;
        this.documentationLink = documentationLink;
        this.isImageEditorAltTextRequired = isImageEditorAltTextRequired;
    }

    @ModelAttribute
    public void addCommonAttributes(HttpServletRequest request, Model model) {
        final UserDomainObject loggedOnUser = Imcms.getUser();

        model.addAttribute("contextPath", request.getContextPath());
        model.addAttribute("availableLanguages", languageService.getAvailableLanguages());
        model.addAttribute("imagesPath", imagesPath);
        model.addAttribute("documentationLink", documentationLink);
        model.addAttribute("isImageEditorAltTextRequired", isImageEditorAltTextRequired);
        model.addAttribute("loggedOnUser", loggedOnUser);
        if(loggedOnUser != null){
            model.addAttribute("isSuperAdmin", loggedOnUser.isSuperAdmin());
            model.addAttribute("userLanguage", loggedOnUser.getLanguage());
        }
    }
}
