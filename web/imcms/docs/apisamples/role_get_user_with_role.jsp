<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<H3>Getting all users with a specific role</H3>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
    User[] users = userService.getAllUserWithRole( RoleConstants.USERS );
%>
All users in the system with the role "<%=RoleConstants.USERS%>":<br>
<%= java.util.Arrays.asList( users ) %><br>
<br>
Users that has the role "<%=RoleConstants.USER_ADMIN%>":<br>
<%
    User[] userAdministrators = userService.getAllUserWithRole( RoleConstants.USER_ADMIN );
%>
<%= java.util.Arrays.asList( userAdministrators ) %><br>
<br>
Users that has the role "<%=RoleConstants.SUPER_ADMIN%>":<br>
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
