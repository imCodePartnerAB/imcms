<%@ page import="com.imcode.imcms.ImcmsSystem,
                 com.imcode.imcms.RequestConstants,
                 com.imcode.imcms.UserBean"%>
<%
ImcmsSystem imcmsSystem = (ImcmsSystem)request.getAttribute( RequestConstants.SYSTEM );
UserBean accessingUser = imcmsSystem.getAccessionUser();
%>

You are logged in as "<%=accessingUser.getLoginName()%>"
