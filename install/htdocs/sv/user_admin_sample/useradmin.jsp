<%@ page import="com.imcode.imcms.*,
                 org.apache.log4j.Logger"
 %>

<h3>Lista alla användare och några av deras attribut</h3>
<%
   try {
      UserMapper userMapper = (UserMapper)request.getAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME );
      User[] users = userMapper.getAllUsers();
      for( int i = 0; i < users.length ; i++ ){
         out.println( "<p>" );
         out.print( "Login: " + users[i].getLoginName() + "<br>" );
         out.print( "Password: " + users[i].getPassword() + "<br>" );
         out.print( "Company: " + users[i].getCompany() + "<br>");
         out.println("</p>");
      }
   }
   catch( NoPermissionException ex ) {
      Logger log = (Logger)request.getAttribute( WebAppConstants.LOGGER_ATTRIBUTE_NAME );
      log.info( "A NoPermissionException was thrown ", ex ) ; %>
      The user logged in has not the permission to do this operation.<br>
      Please log in as a different user<br> <%
   }
%>