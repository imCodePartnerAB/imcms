package imcode.server.user.saml2;

import com.imcode.imcms.servlet.VerifyUser;
import imcode.server.user.UserDomainObject;
import imcode.server.user.saml2.store.SAMLSessionInfo;
import imcode.server.user.saml2.store.SAMLSessionManager;
import imcode.server.user.saml2.utils.SAMLUtils;
import imcode.util.Utility;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.imcode.imcms.servlet.VerifyUser.REQUEST_PARAMETER__NEXT_URL;

/**
 * This class provide filtering all request, which reference to CGI-IDP
 */
public class SAMLSPFilter implements Filter {
    private static final String SAML_AUTHN_RESPONSE_PARAMETER_NAME = "SAMLResponse";
    private static Logger log = LoggerFactory.getLogger(SAMLSPFilter.class);
    private FilterConfig filterConfig;
    private SAMLResponseVerifier checkSAMLResponse;
    private SAMLRequestSender samlRequestSender;

    @Override
    public void init(javax.servlet.FilterConfig config) {
        filterConfig = FilterConfig.getInstance(config);
        if (filterConfig.isEnabled()) {
            checkSAMLResponse = new SAMLResponseVerifier();
            samlRequestSender = new SAMLRequestSender();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /*
         * Check if request is not refer to CGI-IDP - ignore it;
         */
        if (!filterConfig.isEnabled() || !isFilteredRequest(request)) {
            log.debug("According to {} configuration parameter request is ignored + {}",
                    new Object[]{FilterConfig.EXCLUDED_URL_PATTERN_PARAMETER, request.getRequestURI()});
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        log.debug("Attempt to secure resource  is intercepted : {}", request.getRequestURL().toString());
		/*
		  Check if response message is received from identity provider;
          In case of successful response system redirects user to relayState (initial) request
        */
        String responseMessage = servletRequest.getParameter(SAML_AUTHN_RESPONSE_PARAMETER_NAME);
        String spProviderId = filterConfig.getSpProviderId();
        if (responseMessage != null) {
            log.debug("Response from Identity Provider is received");
            try {
                log.debug("Decoding of SAML message");
                SAMLMessageContext<Response, SAMLObject, NameID> samlMessageContext =
                        SAMLUtils.decodeSamlMessage(request, response);
                log.debug("SAML message has been decoded successfully");
                samlMessageContext.setLocalEntityId(spProviderId);
                checkSAMLResponse.verify(samlMessageContext);
                log.debug("Starting and store SAML session..");
                SAMLSessionManager.getInstance().createSAMLSession(request, response,
                        samlMessageContext);
                if (!response.isCommitted()) {
                    new VerifyUser.GoToLoginSuccessfulPageCommand().dispatch(request, response);
                } else {
                    return;
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }

        String nextUrl = request.getParameter(REQUEST_PARAMETER__NEXT_URL);
        if (null != nextUrl && !nextUrl.isEmpty()) {
            request.getSession().setAttribute(REQUEST_PARAMETER__NEXT_URL, nextUrl);
        }
        /*
         * Check if request is logout request
         */
        if (this.getCorrectURL(request).equals(this.filterConfig.getLogoutUrl())) {

            SAMLSessionInfo samlSessionInfo = SAMLSessionManager.getInstance().getSAMLSession(request.getSession(true));
            if (samlSessionInfo != null) {
                log.debug("Logout action: destroying SAML session.");
                try {
                    samlRequestSender.sendSAMLLogoutRequest(request, response, spProviderId, filterConfig.getIdpSSOLogoutUrl(), samlSessionInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SAMLSessionManager.getInstance().destroySAMLSession(request.getSession(true));
                Utility.makeUserLoggedOut(request);
            } else {
                chain.doFilter(servletRequest, servletResponse);
            }

            return;
        }
        /*
         *   Check if user has already authorised
         */
        if (SAMLSessionManager.getInstance().isSAMLSessionValid(request.getSession(true))) {
            SAMLSessionInfo samlSessionInfo = SAMLSessionManager.getInstance().getSAMLSession(request.getSession(true));
            SAMLSessionManager.getInstance().loginUser(samlSessionInfo, request, response);
            log.debug("SAML session exists and valid: grant access to secure resource");
            //chain.doFilter(request, response);
            if (!response.isCommitted()) {
                new VerifyUser.GoToLoginSuccessfulPageCommand().dispatch(request, response);
            } else {
                return;
            }
        }
        /*
         * Create SAML request and redirect user to CGI service for authentication
         */
        log.debug("Sending authentication request to idP");
        try {
            UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
            if (loggedOnUser != null && loggedOnUser.isDefaultUser()) {
                samlRequestSender.sendSAMLAuthRequest(request, response,
                        spProviderId, filterConfig.getAcsUrl(),
                        filterConfig.getIdpSSOLoginUrl());
            } else if (!response.isCommitted()) {
                new VerifyUser.GoToLoginSuccessfulPageCommand().dispatch(request, response);
            } else {
                return;
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private boolean isFilteredRequest(HttpServletRequest request) {
        return !(filterConfig.getExcludedUrlPattern() != null &&
                getCorrectURL(request).matches(filterConfig.getExcludedUrlPattern()));
    }

    // Check if URL is correct
    private String getCorrectURL(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        int contextBeg = requestUri.indexOf(contextPath);
        int contextEnd = contextBeg + contextPath.length();
        String slash = "/";
        String url = (contextBeg < 0 || contextEnd == (requestUri.length() - 1))
                ? requestUri : requestUri.substring(contextEnd);
        if (!url.startsWith(slash)) {
            url = slash + url;
        }

        return url;
    }

    @Override
    public void destroy() {
        log = null;
    }
}
