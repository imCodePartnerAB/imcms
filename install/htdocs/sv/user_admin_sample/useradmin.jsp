<%@ page import="com.imcode.imcms.*,
                 org.apache.log4j.Logger"
 %>
<%
   try {
      UserMapper userMapper = (UserMapper)request.getAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME );
      User[] users = userMapper.getAllUsers();
      for( int i = 0; i < users.length ; i++ ){
         out.print( users[i].getLoginName() );
         out.println("<BR>");
      }
   }
   catch( NoPermissionException ex ) {
      Logger log = (Logger)request.getAttribute( WebAppConstants.LOGGER_ATTRIBUTE_NAME );
      log.info( "A NoPermissionException was thrown ", ex ) ;
      out.println("The user logged in has not the permission to do this operation.<br>");
      out.println("Please log in as a different user<br>");
   }
%>