<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<html>
<body>
<H3>Getting all users with a specific role</H3>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
    User[] users = userService.getAllUsersWithRole( Role.USERS );
%>
All users in the system with the role "<%= Role.USERS %>":<br>
<%= java.util.Arrays.asList( users ) %><br>
<br>
Users that have the role "<%= Role.USERADMIN %>":<br>
<%
    User[] userAdministrators = userService.getAllUsersWithRole( Role.USERADMIN );
%>
<%= java.util.Arrays.asList( userAdministrators ) %><br>
<br>
Users that have the role "<%= Role.SUPERADMIN %>":<br>
<%
    User[] userSuperAdmin = userService.getAllUsersWithRole( Role.SUPERADMIN );
%>
<%= java.util.Arrays.asList( userSuperAdmin ) %><br>
<br>
<%
    Role ldapRole = userService.getRole( "LDAP" );
    if (null != ldapRole) {
        %>Users that are administrated by an external ldap-system (and have logged in at least once):<br><%
        User[] ldapUsers = userService.getAllUsersWithRole( ldapRole );
        %><%= java.util.Arrays.asList( ldapUsers ) %><br><%
    }
%>
</body>
</html>