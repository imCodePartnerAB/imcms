<%@ page import="com.imcode.imcms.*"%><H3>Getting all users with a specific role</H3>
Users with role "<%= Role.USERS %>"
<%
    ImcmsSystem imcmsSystem = (ImcmsSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserMapperBean userMapper = imcmsSystem.getUserMapperBean();
    UserBean[] usersWithASpecificRole = userMapper.getAllUserWithRole( Role.USERS );
%>
<%= java.util.Arrays.asList( usersWithASpecificRole ) %>