<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
%>
All roles in the system: <br>
<%=java.util.Arrays.asList( userService.getAllRoles() )%>
