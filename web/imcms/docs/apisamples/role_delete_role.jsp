<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<html>
<body>
<H3>Delete a role named "Test role"</H3>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
%>
Before:<br>
<%=java.util.Arrays.asList( userService.getAllRoles() )%><br>
<%
    String roleName = "Test role";
    Role role = userService.getRole(roleName);
    if (null != role) {
        userService.deleteRole( role );
        %>After delete of the role named "<%=role%>":<br>
        <%=java.util.Arrays.asList( userService.getAllRoles() )%><%
    } else {
        %>Role "<%= roleName %>" does not exist.<%
    }
%>
</body>
</html>
