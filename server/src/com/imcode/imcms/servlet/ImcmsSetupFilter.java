package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.NDC;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class ImcmsSetupFilter implements Filter {

    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpSession session = httpServletRequest.getSession();

        ImcmsServices service = Imcms.getServices();
        if ( session.isNew() ) {
            service.incrementSessionCounter();
            setDomainSessionCookie( response, session );
        }

        UserDomainObject user = Utility.getLoggedOnUser(httpServletRequest) ;
        if ( user == null ) {
            String ip = request.getRemoteAddr();
            user = getUserUserOrIpLoggedInUser( ip );
            Utility.makeUserLoggedIn(httpServletRequest, user);
        }

        Utility.initRequestWithApi(request, user);

        NDC.setMaxDepth( 0 );
        String contextPath = ( (HttpServletRequest)request ).getContextPath();
        if ( !"".equals( contextPath ) ) {
            NDC.push( contextPath );
        }
        NDC.push( StringUtils.substringAfterLast( ( (HttpServletRequest)request ).getRequestURI(), "/" ) );

        handleDocumentUri( httpServletRequest, service, request, response, chain );
        NDC.setMaxDepth( 0 );
    }

    private void handleDocumentUri( HttpServletRequest httpServletRequest, ImcmsServices service,
                                    ServletRequest request, ServletResponse response, FilterChain chain ) throws ServletException, IOException {
        String path = httpServletRequest.getRequestURI() ;
        path = StringUtils.substringAfter( path, httpServletRequest.getContextPath() ) ;
        String documentPathPrefix = service.getConfig().getDocumentPathPrefix() ;
        String documentIdString = null ;
        if (StringUtils.isNotBlank( documentPathPrefix ) && path.startsWith( documentPathPrefix )) {
            documentIdString = path.substring( documentPathPrefix.length() );
        }
        if (null != documentIdString && NumberUtils.isDigits( documentIdString )) {
            try {
                int documentId = Integer.parseInt( documentIdString ) ;
                GetDoc.output( documentId, httpServletRequest, (HttpServletResponse)response );
            } catch( NumberFormatException nfe ) {
                chain.doFilter( request, response );
            }
        } else {
            chain.doFilter( request, response );
        }
    }

    private void setDomainSessionCookie( ServletResponse response, HttpSession session ) {

        String domain = Imcms.getServices().getConfig().getSessionCookieDomain();
        if (StringUtils.isNotBlank(domain)) {
            Cookie cookie = new Cookie( JSESSIONID_COOKIE_NAME, session.getId());
            cookie.setDomain( domain );
            cookie.setPath( "/" );
            ((HttpServletResponse)response).addCookie( cookie );
        }
    }

    public void init( FilterConfig config ) throws ServletException {
    }

    public void destroy() {
    }

    /**
     * Ip login  - check if user exist in ip-table
     */
    private static UserDomainObject getUserUserOrIpLoggedInUser( String remote_ip ) {
        ImcmsServices imcref = Imcms.getServices();
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

        String user_data[] = imcref.getExceptionUnhandlingDatabase().executeArrayQuery( sqlStr, new String[]{"" + ip, "" + ip} );

        if ( user_data.length > 0 ) {
            user = imcref.verifyUser( user_data[0], user_data[1] );
            user.setLoginType( "ip_access" );
        } else {
            user = Utility.getDefaultUser();
            user.setLoginType( "extern" );
        }

        return user;
    }

}
