package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.exception.ExternalIdentifierNotEnabledException;
import com.imcode.imcms.domain.service.AuthenticationProvidersService;
import com.imcode.imcms.model.AuthenticationProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.imcode.imcms.servlet.VerifyUser.REQUEST_PARAMETER__NEXT_URL;

/**
 * Handles requests from users for external identifiers.
 * Builds required URL and redirects to specified identifier.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.07.18.
 */
@Controller
@RequestMapping("/external-identifiers/")
class RequestExternalIdentifierController {

    private final AuthenticationProvidersService authenticationProvidersService;

    RequestExternalIdentifierController(AuthenticationProvidersService authenticationProvidersService) {
        this.authenticationProvidersService = authenticationProvidersService;
    }

    @RequestMapping("login/{identifierId}")
    public ModelAndView goToExternalIdentifierLoginPage(@PathVariable("identifierId") String identifierId,
                                                        @RequestParam(value = REQUEST_PARAMETER__NEXT_URL, required = false) String nextUrl,
                                                        HttpServletRequest request,
                                                        HttpSession session) {

        final AuthenticationProvider provider = authenticationProvidersService.getAuthenticationProvider(identifierId)
                .orElseThrow(ExternalIdentifierNotEnabledException::new); // or maybe redirect to login page?

        // prepare session stuff here

        return new ModelAndView(new RedirectView(provider.getAuthenticationURL()));
    }

}
