<%@ page import="com.imcode.imcms.*" %>

<h2>A simple sample of using the UserMapperBean and basic operations on a UserBean  </h2>
<%
   UserMapperBean userMapper = (UserMapperBean)request.getAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME );
   UserBean[] users = userMapper.getAllUsers();
   for( int i = 0; i < users.length ; i++ ){
      out.println( "<p>" );
      out.print( "Login: " + users[i].getLoginName() + "<br>" );
      out.print( "Company: " + users[i].getCompany() + "<br>");
      out.print( "Country" + users[i].getCountry() + "<br>");
      out.println("</p>");
   }
%>

<H2>NoPermissionException</H2>
<P>
If the user that is accessing the page dosen't have the right permissions a NoPermissionException is thrown from the methods.
There are two normal ways to handle this. <BR>
1. In the jsp pages page-tag set the errorpage attribute and let that page handle the response to the user<BR>
2. Use try/catch in an normal Java way.<BR>
In this sample page, the first choice is made, see error.jsp for details.
</P>

<h2>Test, Things that not everywone should be able to do </h2>
<a href="sample_restrictedoperations.jsp">sample_restrictedoperations.jsp</a>
</a>

<H2>Hanling roles</H2>
<H3>Create a new role</H3>

Before:<br>
<%=java.util.Arrays.asList( userMapper.getAllRolesNames() )%>
<%
   String role = "Test role";
   userMapper.addNewRole( role );
%><br>
After adding a new role named "<%=role%>":<br>
<%=java.util.Arrays.asList( userMapper.getAllRolesNames() )%>

<H3>Getting users with a specific role</H3>
Users with role "<%= Role.USERS %>"
<%
   UserBean[] usersWithASpecificRole = userMapper.getAllUserWithRole( Role.USERS );
%>
<%= java.util.Arrays.asList( usersWithASpecificRole ) %>


