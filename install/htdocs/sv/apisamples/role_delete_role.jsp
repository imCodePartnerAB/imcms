<%@ page import="com.imcode.imcms.*" %>

<H3>Delete a role named "Test role"</H3>

<%
    ImcmsSystem imcmsSystem = (ImcmsSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserMapperBean userMapper = imcmsSystem.getUserMapperBean();
%>
Before:<br>
<%=java.util.Arrays.asList( userMapper.getAllRolesNames() )%>
<%
   String role = "Test role";
   userMapper.deleteRole( role );
%><br>
After delete the role named "<%=role%>":<br>
<%=java.util.Arrays.asList( userMapper.getAllRolesNames() )%>
