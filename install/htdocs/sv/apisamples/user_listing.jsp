<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<h2><? sv/apisamples/user_listing.jsp/1 ?></h2>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
    User[] users = userService.getAllUsers();
    for( int i = 0; i < users.length ; i++ ){%>
        <p>
        <? sv/apisamples/user_listing.jsp/2 ?>
        </p><%
    }
%>