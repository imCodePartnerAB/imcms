<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<head>
<title>Delete a role named "Test role"</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>
<H3>Getting all users with a specific role</H3>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
    User[] users = userService.getAllUserWithRole( RoleConstants.USERS );
%>
All users in the system:<br>
<%= java.util.Arrays.asList( users ) %><br>
<br>
Users that has the role user administrator:<br>
<%
    User[] userAdministrators = userService.getAllUserWithRole( RoleConstants.USER_ADMIN );
%>
<%= java.util.Arrays.asList( userAdministrators ) %><br>
<br>
Users that has the role super administrator:<br>
<%
    User[] userSuperAdmin = userService.getAllUserWithRole( RoleConstants.SUPER_ADMIN );
%>
<%= java.util.Arrays.asList( userSuperAdmin ) %><br>
<br>
Users that is administrated by an external ldap-system "LDAP" (and has logged in at least once):<br>
<%
    User[] ldapExternalUser = userService.getAllUserWithRole( "LDAP" );
%>
<%= java.util.Arrays.asList( ldapExternalUser ) %><br>
<br>
</body>
</html>
