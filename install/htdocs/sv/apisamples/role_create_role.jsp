<%@ page import="com.imcode.imcms.*" %>

<H2>Hanling roles</H2>
<H3>Create a new role</H3>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserMapperBean userMapper = imcmsSystem.getUserMapperBean();
%>
Before:<br>
<%=java.util.Arrays.asList( userMapper.getAllRolesNames() )%>
<%
   String role = "Test role";
   userMapper.addNewRole( role );
%><br>
After adding a new role named "<%=role%>":<br>
<%=java.util.Arrays.asList( userMapper.getAllRolesNames() )%>
