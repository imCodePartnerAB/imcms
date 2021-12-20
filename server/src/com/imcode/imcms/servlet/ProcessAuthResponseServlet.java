package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.model.AuthenticationProvider;
import com.imcode.imcms.model.ExternalUser;
import imcode.server.Imcms;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProcessAuthResponseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String identifierId = request.getPathInfo().replaceAll("/", "").trim();

        final AuthenticationProvider provider =
                Imcms.getServices().getAuthenticationProviderService().getAuthenticationProvider(identifierId);

        String nextURL = provider.processAuthentication(request);
        nextURL = (StringUtils.isBlank(nextURL) ? (request.getContextPath() + "/") : nextURL);

        final ExternalUser user = ContentManagementSystem.fromRequest(request)
                .getUserService()
                .saveExternalUser(provider.getUser(request));

        Utility.makeUserLoggedIn(request, response, user);
        response.sendRedirect(nextURL);
    }
}
