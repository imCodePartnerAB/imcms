<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<%
ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
User currentLoggedinUser = imcmsSystem.getCurrentUser();
%>
<html>
    <body>
        <p>
            You are logged in as "<%=currentLoggedinUser.getLoginName() %>" with user-id <%= currentLoggedinUser.getId() %><% if (currentLoggedinUser.isDefaultUser()) { %>, the default user<% } %>.
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
