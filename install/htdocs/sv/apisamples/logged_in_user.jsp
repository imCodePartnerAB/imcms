<%@ page import="com.imcode.imcms.ContentManagementSystem,
                 com.imcode.imcms.RequestConstants,
                 com.imcode.imcms.UserBean"%>
<%
ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
UserBean accessingUser = imcmsSystem.getAccessionUser();
%>

You are logged in as "<%=accessingUser.getLoginName()%>"
