<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<%
ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
User currentLoggedinUser = imcmsSystem.getCurrentUser();
%>
<html>
    <body>
        <p>
            You are logged in as "<%=currentLoggedinUser.getLoginName()%>".
        </p>
        <p>
            Your roles are:
        </p>
        <ul>
            <%
                String[] roleNames = currentLoggedinUser.getRoleNames() ;
                for ( int i = 0; i < roleNames.length; i++ ) {
                    %><li><%= roleNames[i] %></li><%
                }
            %>
        </ul>
    </body>
</html>
