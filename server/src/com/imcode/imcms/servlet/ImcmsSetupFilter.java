package com.imcode.imcms.servlet;

import com.imcode.imcms.*;
import imcode.server.IMCService;
import imcode.server.user.UserDomainObject;
import imcode.util.IMCServiceRMI;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.*;
import java.io.IOException;

public class ImcmsSetupFilter implements Filter {
    private Logger log = Logger.getLogger( ImcmsSetupFilter.class );
    public static final String USER = "logon.isDone";

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest)request).getSession( true );
        UserDomainObject accessor = (UserDomainObject)session.getAttribute( USER );

        initRequestWithImcmsSystemAPI( accessor, request );

        chain.doFilter( request, response );
    }

    private void initRequestWithImcmsSystemAPI( UserDomainObject accessor, ServletRequest request ) {
        if( null != accessor ) {
            try {
                IMCService service = (IMCService)IMCServiceRMI.getIMCServiceInterface( (HttpServletRequest)request );
                ContentManagementSystem imcmsSystem = new ContentManagementSystem( service, accessor );
                request.setAttribute( RequestConstants.SYSTEM, imcmsSystem );
            } catch( IOException e ) {
                log.fatal( "Unable to get service object.", e );
            }
        }
    }

    public void init( FilterConfig config ) throws ServletException {
    }

    public void destroy() {
    }
}
