<%@ page import="com.imcode.imcms.*" %>

<h2>A simple sample of using the UserMapperBean and basic operations on a UserBean  </h2>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserMapperBean userMapper = imcmsSystem.getUserMapperBean();
    UserBean[] users = userMapper.getAllUsers();
    for( int i = 0; i < users.length ; i++ ){
      out.println( "<p>" );
      out.print( "Login: " + users[i].getLoginName() + "<br>" );
      out.print( "Company: " + users[i].getCompany() + "<br>");
      out.print( "Country" + users[i].getCountry() + "<br>");
      out.println("</p>");
    }
%>