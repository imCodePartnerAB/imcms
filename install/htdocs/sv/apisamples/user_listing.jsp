<%@ page import="com.imcode.imcms.*" %>

<h2>Listing current users in the IMCMS system</h2>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userMapper = imcmsSystem.getUserService();
    User[] users = userMapper.getAllUsers();
    for( int i = 0; i < users.length ; i++ ){
      out.println( "<p>" );
      out.print( "Login: " + users[i].getLoginName() + "<br>" );
      out.print( "Company: " + users[i].getCompany() + "<br>");
      out.print( "Country" + users[i].getCountry() + "<br>");
      out.println("</p>");
    }
%>