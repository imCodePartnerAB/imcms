<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<html>
<body>
<H3>Create a new role</H3>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
%>
Before:<br>
<%=java.util.Arrays.asList( userService.getAllRoles() )%>
<%
   String roleName = "Test role";
   Role role = userService.createNewRole( roleName );
   role.setPasswordMailPermission( false );
   userService.saveRole( role );
%><br>
After adding a new role named "<%= role.getName() %>":<br>
<%=java.util.Arrays.asList( userService.getAllRoles() )%>
</body>
</html>
