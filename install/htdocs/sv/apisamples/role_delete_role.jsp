<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<H3>Delete a role named "Test role"</H3>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
%>
Before:<br>
<%=java.util.Arrays.asList( userService.getAllRolesNames() )%>
<%
   String role = "Test role";
   userService.deleteRole( role );
%><br>
After delete the role named "<%=role%>":<br>
<%=java.util.Arrays.asList( userService.getAllRolesNames() )%>
