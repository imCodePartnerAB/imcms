<%@ page import="com.imcode.imcms.*"%>

<%
ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
User accessingUser = imcmsSystem.getCurrentUser();
%>

You are logged in as "<%=accessingUser.getLoginName()%>"
