package com.imcode.imcms.servlet;

import com.imcode.imcms.WebAppConstants;
import com.imcode.imcms.UserMapper;
import com.imcode.imcms.UserMapperImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.IMCServiceInterface;
import imcode.server.IMCConstants;
import imcode.util.IMCServiceRMI;

import java.io.IOException;

public class ImcmsSetupFilter implements Filter {
   private Logger webAppLog;

   public void doFilter( ServletRequest request,
                         ServletResponse response,
                         FilterChain chain ) throws ServletException, IOException {

      request.setAttribute( WebAppConstants.LOGGER_ATTRIBUTE_NAME, webAppLog );

      try {
         IMCServiceInterface service = null ;
         service = IMCServiceRMI.getIMCServiceInterface((HttpServletRequest)request);
         ImcmsAuthenticatorAndUserMapper imcmsAAUM = new ImcmsAuthenticatorAndUserMapper(service) ;
         UserMapper mapper = new UserMapperImpl(imcmsAAUM);

         request.setAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME, mapper );
      } catch( IOException e ) {
         webAppLog.fatal("Unable to get service object.", e) ;
      }
      chain.doFilter( request, response );
   }

   public void destroy() {
   }

   public void init( FilterConfig config ) throws ServletException {
      webAppLog = Logger.getLogger( IMCConstants.ERROR_LOG );

   }
}
