package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import com.imcode.imcms.util.l10n.LocalizedMessage;

public class ImcmsMultipleUserLoginFilter implements Filter {

    public final static String TOO_MANY_SESSIONS = "TooManySessions";

    public final static LocalizedMessage LOGIN_MSG_TOO_MANY_SESSIONS
            = new LocalizedMessage("templates/login/TooManySessions");

    public void init(FilterConfig config)
    throws ServletException {}

    public void destroy() {}
    
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {       
    	req.setCharacterEncoding(Imcms.DEFAULT_ENCODING);
    	
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        HttpSession session = request.getSession();
        ImcmsServices service = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser(request) ;

        // In case system denies multiple login for same user
        // invalidate current session if it does not match to
        // last user's session and redirect user to the login page.
        if (user != null && !user.isDefaultUser()
                && service.getConfig().isDenyMultipleUserLogin()) {

            String sessionId = session.getId();
            String lastUserSessionId = service
                    .getImcmsAuthenticatorAndUserAndRoleMapper()
                    .getUserSessionId(user);

            if (lastUserSessionId != null
                    && !lastUserSessionId.equals(sessionId)) {

                session.invalidate();

                String redirectURL = request.getContextPath() + "/login?"
                        + TOO_MANY_SESSIONS;

                response.sendRedirect(redirectURL);
                return;
            }
        }

        if (request.getParameter(TOO_MANY_SESSIONS) != null) {
            request.setAttribute(VerifyUser.REQUEST_ATTRIBUTE__ERROR,
                    LOGIN_MSG_TOO_MANY_SESSIONS);
        }

        chain.doFilter(request, response);
    }
}