<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<html>
<body>
<H3>Rename a role</H3>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
%>
Before:<br>
<%=java.util.Arrays.asList( userService.getAllRoles() )%><br>
<%
    String roleName = "Test role";
    Role role = userService.getRole( roleName );
    if (null == role) {
        %>The role "<%= roleName %>" does not exist.<%
    } else {
        role.setName("Test") ;
        userService.saveRole( role );
        %>After renaming "<%= roleName %>" to "<%= role.getName() %>":<br>
        <%=java.util.Arrays.asList( userService.getAllRoles() )%><br><%
        role.setName(roleName) ;
        userService.saveRole( role );
        %>After renaming it back:<br>
        <%=java.util.Arrays.asList( userService.getAllRoles() )%><%
    }
%>
</body>
</html>
