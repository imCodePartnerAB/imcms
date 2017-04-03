<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<html>
<body>
<H3>Getting all users with a specific role</H3>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
    Role usersRole = userService.getRole( Role.USERS_ID ) ;
    Role useradminRole = userService.getRole( Role.USERADMIN_ID ) ;
    Role superadminRole = userService.getRole( Role.SUPERADMIN_ID ) ;
    User[] users = userService.getAllUsersWithRole( usersRole );
%>
All users in the system with the role "<%= usersRole %>":<br>
<%= java.util.Arrays.asList( users ) %><br>
<br>
Users that have the role "<%= useradminRole %>":<br>
<%
    User[] userAdministrators = userService.getAllUsersWithRole( useradminRole );
%>
<%= java.util.Arrays.asList( userAdministrators ) %><br>
<br>
Users that have the role "<%= superadminRole %>":<br>
<%
    User[] userSuperAdmin = userService.getAllUsersWithRole( superadminRole );
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