package com.imcode.imcms.servlet;

import com.imcode.imcms.model.AuthenticationProvider;
import imcode.server.Imcms;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;

import static com.imcode.imcms.servlet.VerifyUser.REQUEST_PARAMETER__NEXT_URL;


public class RequestExternalIdentifierToLoginServlet extends HttpServlet {

    private static final String EXTERNAL_IDENTIFIERS_PATH = "/external-identifiers/";
    private static final String EXTERNAL_IDENTIFIER_REDIRECT_URI = "logged-in";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final String identifierId = request.getPathInfo().replaceAll("/", "").trim();
        final String nextUrl = request.getParameter(REQUEST_PARAMETER__NEXT_URL);
        final AuthenticationProvider provider =
                Imcms.getServices().getAuthenticationProviderService().getAuthenticationProvider(identifierId);

        response.sendRedirect(provider.buildAuthenticationURL(getRedirectURL(identifierId, request), session.getId(), nextUrl));
    }

    @SneakyThrows
    private String getRedirectURL(String identifierId, HttpServletRequest request) {

        final URL url = new URL(request.getRequestURL().toString());
        final String protocol = url.getProtocol();
        final String host = url.getHost();
        final int port = url.getPort();

        final String protocolHostPort = (port == -1)// if the port is not explicitly specified in the input, it will be -1.
                ? String.format("%s://%s", protocol, host)
                : String.format("%s://%s:%d", protocol, host, port);

        return protocolHostPort + request.getContextPath() + "/api" + EXTERNAL_IDENTIFIERS_PATH
                + EXTERNAL_IDENTIFIER_REDIRECT_URI + "/" + identifierId;
    }
}
