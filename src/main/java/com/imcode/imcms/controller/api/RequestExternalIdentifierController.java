package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.AuthenticationProvidersService;
import com.imcode.imcms.model.AuthenticationProvider;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.util.List;

import static com.imcode.imcms.controller.api.RequestExternalIdentifierController.EXTERNAL_IDENTIFIERS_PATH;
import static com.imcode.imcms.servlet.VerifyUser.REQUEST_PARAMETER__NEXT_URL;
import static imcode.server.ImcmsConstants.API_PREFIX;

/**
 * Handles requests from users for external identifiers.
 * Builds required URL and redirects to specified identifier.
 * Handles external identifier redirect with success/fail authentication data.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.07.18.
 */
@Controller
@RequestMapping(EXTERNAL_IDENTIFIERS_PATH)
class RequestExternalIdentifierController {

    static final String EXTERNAL_IDENTIFIERS_PATH = "/external-identifiers/";
    static final String EXTERNAL_IDENTIFIER_REDIRECT_URI = "logged-in";

    private final AuthenticationProvidersService authenticationProvidersService;

    RequestExternalIdentifierController(AuthenticationProvidersService authenticationProvidersService) {
        this.authenticationProvidersService = authenticationProvidersService;
    }

    @RequestMapping("login/{identifierId}")
    public ModelAndView goToExternalIdentifierLoginPage(@PathVariable("identifierId") String identifierId,
                                                        @RequestParam(value = REQUEST_PARAMETER__NEXT_URL, required = false) String nextUrl,
                                                        HttpServletRequest request,
                                                        HttpSession session) {

        final AuthenticationProvider provider = authenticationProvidersService.getAuthenticationProvider(identifierId);

        return new ModelAndView(new RedirectView(provider.buildAuthenticationURL(
                getRedirectURL(identifierId, request), session.getId(), nextUrl
        )));
    }

    @SneakyThrows
    String getRedirectURL(String identifierId, HttpServletRequest request) {
        final URL url = new URL(request.getRequestURL().toString());
        final String protocol = url.getProtocol();
        final String host = url.getHost();
        final int port = url.getPort();

        final String protocolHostPort = (port == -1)// if the port is not explicitly specified in the input, it will be -1.
                ? String.format("%s://%s", protocol, host)
                : String.format("%s://%s:%d", protocol, host, port);

        return protocolHostPort + request.getContextPath() + API_PREFIX + EXTERNAL_IDENTIFIERS_PATH
                + EXTERNAL_IDENTIFIER_REDIRECT_URI + "/" + identifierId;
    }

    @RequestMapping(EXTERNAL_IDENTIFIER_REDIRECT_URI + "/{identifierId}")
    public ModelAndView processExternalAuthResponse(@PathVariable("identifierId") String identifierId,
                                                    HttpServletRequest request) {

        final AuthenticationProvider provider = authenticationProvidersService.getAuthenticationProvider(
                identifierId
        );

        String nextURL = provider.processAuthentication(request);
        nextURL = (StringUtils.isBlank(nextURL) ? (request.getContextPath() + "/") : nextURL);

        final UserDomainObject user = provider.getUser(request);
        Utility.makeUserLoggedIn(request, user);

        return new ModelAndView(new RedirectView(nextURL));
    }

    @RequestMapping("/{identifierId}/roles")
    public List<Object> getExternalRoles(@PathVariable String identifierId) {
        return authenticationProvidersService.getAuthenticationProvider(identifierId).getRoles();
    }

}
