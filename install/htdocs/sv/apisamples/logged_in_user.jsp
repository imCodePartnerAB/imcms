<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Loged in user page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>
<%
ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
User currentLoggedinUser = imcmsSystem.getCurrentUser();
%>
You are logged in as "<%=currentLoggedinUser.getLoginName()%>"
</body>
</html>
