<%@ page import="com.imcode.imcms.*"%>

<%
ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
User currentLoggedinUser = imcmsSystem.getCurrentUser();
%>
You are logged in as "<%=currentLoggedinUser.getLoginName()%>"
