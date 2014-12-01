package imcode.server.user.saml2;

import imcode.server.user.saml2.store.SAMLSessionManager;
import imcode.server.user.saml2.utils.*;
import org.opensaml.common.binding.SAMLMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Hello world!
 */
public class SAMLSPFilter implements Filter {
    public static final String SAML_AUTHN_RESPONSE_PARAMETER_NAME = "SAMLResponse";
    private static Logger log = LoggerFactory.getLogger(SAMLSPFilter.class);
    private FilterConfig filterConfig;
    private SAMLResponseVerifier checkSAMLResponse;
    private SAMLRequestSender samlRequestSender;

    @Override
    public void init(javax.servlet.FilterConfig config) {
        OpenSamlBootstrap.init();
        filterConfig = new FilterConfig(config);
        checkSAMLResponse = new SAMLResponseVerifier();
        samlRequestSender = new SAMLRequestSender();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
  /*
  ШАГ 1: Игнорируем запросы, предназначенные не для фильтра
  */
        if (!isFilteredRequest(request)||!filterConfig.isEnabled()) {
            log.debug("According to {} configuration parameter request is ignored + {}",
                    new Object[]{FilterConfig.EXCLUDED_URL_PATTERN_PARAMETER, request.getRequestURI()});
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        /*
  ШАГ 2: Если пришел ответ от Shibboleth idP, обрабатываем его
  */
        log.debug("Attempt to secure resource  is intercepted : {}", ((HttpServletRequest) servletRequest).getRequestURL().toString());
/*
  Check if response message is received from identity provider;
  In case of successful response system redirects user to relayState (initial) request
*/
        String responseMessage = servletRequest.getParameter(SAML_AUTHN_RESPONSE_PARAMETER_NAME);
        if (responseMessage != null) {
            log.debug("Response from Identity Provider is received");
            try {
                log.debug("Decoding of SAML message");
                SAMLMessageContext samlMessageContext =
                        SAMLUtils.decodeSamlMessage((HttpServletRequest) servletRequest,
                                (HttpServletResponse) servletResponse);
                log.debug("SAML message has been decoded successfully");
                samlMessageContext.setLocalEntityId(filterConfig.getSpProviderId());
                //String relayState = getInitialRequestedResource(samlMessageContext);
                checkSAMLResponse.verify(samlMessageContext);
                log.debug("Starting and store SAML session..");
                SAMLSessionManager.getInstance().createSAMLSession(request, response,
                        samlMessageContext);
                // log.debug("User has been successfully authenticated in idP. Redirect to initial requested resource {}", relayState);
                //response.sendRedirect(relayState);
                return;
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
        /*
  ШАГ 3: Если получен запрос на logout, удаляем локальную сессию
  */
        if (getCorrectURL(request).equals(filterConfig.getLogoutUrl())) {
            log.debug("Logout action: destroying SAML session.");
            SAMLSessionManager.getInstance().destroySAMLSession(request.getSession());
            chain.doFilter(request, response);
            return;
        }
        /*
  ШАГ 4: Если пользователь уже аутентифицирован, даем доступ к ресурсу
  */
        if (SAMLSessionManager.getInstance().isSAMLSessionValid(request.getSession())) {
            log.debug("SAML session exists and valid: grant access to secure resource");
            chain.doFilter(request, response);
            try {
                response.sendRedirect("StartDoc");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        /*
  ШАГ 5: Создаем SAML запрос на аутентификацию и отправляем пользователя к
         Shibboleth idP
  */
        log.debug("Sending authentication request to idP");
        try {
            samlRequestSender.sendSAMLAuthRequest(request, response,
                    filterConfig.getSpProviderId(), filterConfig.getAcsUrl(),
                    filterConfig.getIdpSSOUrl());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private boolean isFilteredRequest(HttpServletRequest request) {
        return !(filterConfig.getExcludedUrlPattern() != null &&
                getCorrectURL(request).matches(filterConfig.getExcludedUrlPattern()));
    }

    // Также добавляем вспомогательный метод получения корректного URL
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
