<%@ page import="com.imcode.imcms.*" %>

<H3>Delete a role named "Test role"</H3>

<%
    UserMapperBean userMapper = (UserMapperBean)request.getAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME );
%>
Before:<br>
<%=java.util.Arrays.asList( userMapper.getAllRolesNames() )%>
<%
   String role = "Test role";
   userMapper.deleteRole( role );
%><br>
After delete the role named "<%=role%>":<br>
<%=java.util.Arrays.asList( userMapper.getAllRolesNames() )%>
