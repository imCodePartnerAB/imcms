<%@ page import="com.imcode.imcms.api.*,
java.util.*" errorPage="error.jsp" %>
<html>
    <body>
        <%
            ContentManagementSystem contentManagementSystem = ContentManagementSystem.fromRequest( request );
            UserService userService = contentManagementSystem.getUserService();
            String loginName = "test";
            String password = "test";
            User newUser = userService.createNewUser(loginName, password) ;
            newUser.setFirstName( loginName );
            newUser.setLastName( loginName );
            newUser.setEmailAddress( contentManagementSystem.getCurrentUser().getEmailAddress() );
            try {
                userService.saveUser( newUser );
                %>Created a user named "<%= userService.getUser( loginName ).getLoginName() %>".<%
            } catch (SaveException se) {
                %>A user with the name "<%= loginName %>" already exists.<%
            }
        %>
    </body>
</html>
