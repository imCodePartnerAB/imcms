<%@ page import="com.imcode.imcms.ContentManagementSystem,
                 com.imcode.imcms.RequestConstants,
                 com.imcode.imcms.User"%>
<%
ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
User accessingUser = imcmsSystem.getCurrentUser();
%>

You are logged in as "<%=accessingUser.getLoginName()%>"
