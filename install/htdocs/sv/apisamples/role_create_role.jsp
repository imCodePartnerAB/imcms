<%@ page import="com.imcode.imcms.*" %>

<H3>Create a new role</H3>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
%>
Before:<br>
<%=java.util.Arrays.asList( userService.getAllRolesNames() )%>
<%
   String role = "Test role";
   userService.addNewRole( role );
%><br>
After adding a new role named "<%=role%>":<br>
<%=java.util.Arrays.asList( userService.getAllRolesNames() )%>
