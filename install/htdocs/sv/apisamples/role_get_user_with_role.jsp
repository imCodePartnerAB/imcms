<%@ page import="com.imcode.imcms.*"%><H3>Getting all users with a specific role</H3>
Users with role "<%= RoleConstants.USERS %>"
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userMapper = imcmsSystem.getUserMapperBean();
    User[] usersWithASpecificRole = userMapper.getAllUserWithRole( RoleConstants.USERS );
%>
<%= java.util.Arrays.asList( usersWithASpecificRole ) %>