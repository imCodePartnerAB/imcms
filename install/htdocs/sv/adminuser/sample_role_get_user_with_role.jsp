<%@ page import="com.imcode.imcms.Role,
                 com.imcode.imcms.UserBean,
                 com.imcode.imcms.UserMapperBean,
                 com.imcode.imcms.WebAppConstants"%><H3>Getting all users with a specific role</H3>
Users with role "<%= Role.USERS %>"
<%
    UserMapperBean userMapper = (UserMapperBean)request.getAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME );
   UserBean[] usersWithASpecificRole = userMapper.getAllUserWithRole( Role.USERS );
%>
<%= java.util.Arrays.asList( usersWithASpecificRole ) %>