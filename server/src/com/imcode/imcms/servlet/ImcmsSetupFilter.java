package com.imcode.imcms.servlet;

import com.imcode.imcms.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.User;
import imcode.server.IMCServiceInterface;
import imcode.server.IMCConstants;
import imcode.util.IMCServiceRMI;

import java.io.IOException;

public class ImcmsSetupFilter implements Filter {
   private Logger webAppLog;

   private final static String USER_SESSION_ATTRIBUTE_NAME = "logon.isDone";

   public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
      throws ServletException, IOException {

      request.setAttribute( WebAppConstants.LOGGER_ATTRIBUTE_NAME, webAppLog );

      try {
         HttpSession session = ((HttpServletRequest)request).getSession( false );
         if( null != session ) {
            User user = (User)session.getAttribute( USER_SESSION_ATTRIBUTE_NAME );
            if( null != user ) {
               IMCServiceInterface service = IMCServiceRMI.getIMCServiceInterface( (HttpServletRequest)request );
               ImcmsAuthenticatorAndUserMapper imcmsAAUM = new ImcmsAuthenticatorAndUserMapper( service );

               String[] roleNames = imcmsAAUM.getRoleNames(user);
               SecurityChecker securityChecker = new SecurityChecker( user, roleNames );

               UserMapperBean mapper = new UserMapperBean( securityChecker, imcmsAAUM );
               request.setAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME, mapper );
            }
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
