package com.imcode.imcms.servlet;

import com.imcode.imcms.DocumentMapperBean;
import com.imcode.imcms.SecurityChecker;
import com.imcode.imcms.UserMapperBean;
import com.imcode.imcms.WebAppConstants;
import imcode.server.IMCConstants;
import imcode.server.IMCService;
import imcode.server.document.DocumentMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.User;
import imcode.util.IMCServiceRMI;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.*;
import java.io.IOException;

public class ImcmsSetupFilter implements Filter {
    private Logger webAppLog;

    private final static String USER_SESSION_ATTRIBUTE_NAME = "logon.isDone";

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws ServletException, IOException {

        request.setAttribute( WebAppConstants.LOGGER_ATTRIBUTE_NAME, webAppLog );

        try {
            HttpSession session = ((HttpServletRequest)request).getSession( true );
            User user = (User)session.getAttribute( USER_SESSION_ATTRIBUTE_NAME );
            if( null != user ) {
                IMCService service = (IMCService)IMCServiceRMI.getIMCServiceInterface( (HttpServletRequest)request );
                ImcmsAuthenticatorAndUserMapper imcmsAAUM = new ImcmsAuthenticatorAndUserMapper( service );
                DocumentMapper documentMapper = new DocumentMapper( service, imcmsAAUM );

                String[] roleNames = imcmsAAUM.getRoleNames( user );
                SecurityChecker securityChecker = new SecurityChecker( documentMapper, user, roleNames );

                UserMapperBean userMapper = new UserMapperBean( securityChecker, imcmsAAUM );
                request.setAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME, userMapper );

                DocumentMapperBean docMapper = new DocumentMapperBean( securityChecker, documentMapper );
                request.setAttribute( WebAppConstants.DOCUMENT_MAPPER_ATTRIBUTE_NAME, docMapper );
            }
        } catch( IOException e ) {
            webAppLog.fatal( "Unable to get service object.", e );
        }
        chain.doFilter( request, response );
    }

    public void destroy() {
    }

    public void init( FilterConfig config ) throws ServletException {
        webAppLog = Logger.getLogger( IMCConstants.ERROR_LOG );

    }
}
