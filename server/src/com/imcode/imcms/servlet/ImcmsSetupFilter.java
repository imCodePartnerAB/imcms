package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.DefaultContentManagementSystem;
import com.imcode.imcms.api.RequestConstants;
import imcode.server.ApplicationServer;
import imcode.server.IMCService;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.NDC;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ImcmsSetupFilter implements Filter {

    private static final String LOGGED_IN_USER = "logon.isDone";

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {

        HttpSession session = ( (HttpServletRequest)request ).getSession( true );
        UserDomainObject user = (UserDomainObject)session.getAttribute( LOGGED_IN_USER );

        if ( user == null ) {
            String ip = request.getRemoteAddr();
            user = getUserUserOrIpLoggedInUser( ip );
            session.setAttribute( LOGGED_IN_USER, user );  // just a marker object
        }

        initRequestWithImcmsSystemAPI( user, request );

        NDC.setMaxDepth(0);
        String contextPath = ((HttpServletRequest)request).getContextPath() ;
        if (!"".equals( contextPath )) {
            NDC.push( contextPath );
        }
        NDC.push(StringUtils.substringAfterLast(((HttpServletRequest)request).getRequestURI(), "/")) ;
        chain.doFilter( request, response );
        NDC.setMaxDepth(0);
    }

    private void initRequestWithImcmsSystemAPI( UserDomainObject currentUser, ServletRequest request ) {
        ContentManagementSystem imcmsSystem;
        IMCService service = (IMCService)ApplicationServer.getIMCServiceInterface();
        imcmsSystem = new DefaultContentManagementSystem( service, currentUser );
        request.setAttribute( RequestConstants.SYSTEM, imcmsSystem );
    }

    public void init( FilterConfig config ) throws ServletException {
    }

    public void destroy() {
    }

    /**
     * Ip login  - check if user exist in ip-table
     */
    private static UserDomainObject getUserUserOrIpLoggedInUser( String remote_ip ) {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user;

        long ip = Utility.ipStringToLong( remote_ip );

        // Todo: Remove this sql-abomination!
        String sqlStr;
        sqlStr = "select distinct login_name,login_password,ip_access_id from users,user_roles_crossref,ip_accesses\n";
        sqlStr += "where user_roles_crossref.user_id = ip_accesses.user_id\n";
        sqlStr += "and users.user_id = user_roles_crossref.user_id\n";
        sqlStr += "and ip_accesses.ip_start <= ?\n";
        sqlStr += "and ip_accesses.ip_end >= ?\n";
        sqlStr += "order by ip_access_id desc";

        String user_data[] = imcref.sqlQuery( sqlStr, new String[]{"" + ip, "" + ip} );

        if ( user_data.length > 0 ) {
            user = imcref.verifyUser( user_data[0], user_data[1] );
            user.setLoginType( "ip_access" );
        } else {
            user = imcref.verifyUser( "User", "user" );
            user.setLoginType( "extern" );
        }

        return user;
    }
}
