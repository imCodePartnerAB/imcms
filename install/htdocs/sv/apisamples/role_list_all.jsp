<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<head>
<title>Delete a role named "Test role"</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
%>
All roles in the system: <br>
<%=java.util.Arrays.asList( userService.getAllRolesNames() )%>
</body>
</html>
