<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<head>
<title>Create a new role</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>

<H3>Create a new role</H3>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
%>
Before:<br>
<%=java.util.Arrays.asList( userService.getAllRolesNames() )%>
<%
   String role = "Test role";
   userService.addNewRole( role );
%><br>
After adding a new role named "<%=role%>":<br>
<%=java.util.Arrays.asList( userService.getAllRolesNames() )%>
</body>
</html>
