<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<head>
<title>Delete a role named "Test role"</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>
<H3>Delete a role named "Test role"</H3>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
%>
Before:<br>
<ul>
<%
    String[] roleNames = userService.getAllRolesNames();
    for( int i = 0; i < roleNames.length; i++ ) {%>
        <li><%=roleNames[i]%></li><%
    }
%>
</ul>

<%
   String role = "Test role";
   userService.deleteRole( role );
%><br>
After delete the role named "<%=role%>":<br>

<ul>
<%
    roleNames = userService.getAllRolesNames();
    for( int i = 0; i < roleNames.length; i++ ) {%>
        <li><%=roleNames[i]%></li><%
    }
%>
</ul>
</body>
</html>
