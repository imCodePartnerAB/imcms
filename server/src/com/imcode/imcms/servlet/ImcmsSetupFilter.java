package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.DefaultContentManagementSystem;
import com.imcode.imcms.api.RequestConstants;
import com.imcode.imcms.api.NotLoggedInContentManagementSystem;
import imcode.server.ApplicationServer;
import imcode.server.IMCService;
import imcode.server.user.UserDomainObject;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ImcmsSetupFilter implements Filter {
    private Logger log = Logger.getLogger(ImcmsSetupFilter.class);
    private static final String USER = "logon.isDone";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest) request).getSession(true);
        UserDomainObject currentUser = (UserDomainObject) session.getAttribute(USER);

        initRequestWithImcmsSystemAPI(currentUser, request);

        chain.doFilter(request, response);
    }

    private void initRequestWithImcmsSystemAPI(UserDomainObject currentUser, ServletRequest request) {
        ContentManagementSystem imcmsSystem = null ;
        if (null != currentUser) {
            try {
                IMCService service = (IMCService) ApplicationServer.getIMCServiceInterface();
                imcmsSystem = new DefaultContentManagementSystem(service, currentUser);
            } catch (IOException e) {
                log.fatal("Unable to get service object.", e);
            }
        } else {
            imcmsSystem = new NotLoggedInContentManagementSystem() ; 
        }
        request.setAttribute(RequestConstants.SYSTEM, imcmsSystem);
    }

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }
}
