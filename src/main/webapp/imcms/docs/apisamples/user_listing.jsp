<%@ page import="com.imcode.imcms.api.ContentManagementSystem" errorPage="error.jsp" %><%@ page import="com.imcode.imcms.api.User"%><%@ page import="com.imcode.imcms.api.UserService"%>

<h2>Listing current users in the IMCMS system</h2>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
    User[] users = userService.getAllUsers();
    for( int i = 0; i < users.length ; i++ ){%>
        <p>
        User "<%= users[i].getLoginName()%>" has the following attributes:<br><%
        out.print( "getAddress(): " + users[i].getAddress() + "<br>");
        out.print( "getCity(): " + users[i].getCity() + "<br>");
        out.print( "getCompany(): " + users[i].getCompany() + "<br>");
        out.print( "getCountry(): " + users[i].getCountry() + "<br>");
        out.print( "getProvince(): " + users[i].getProvince() + "<br>");
        out.print( "getEmailAddress(): " + users[i].getEmailAddress() + "<br>");
        out.print( "getFirstName(): " + users[i].getFirstName() + "<br>");
        out.print( "getHomePhone(): " + users[i].getHomePhone() + "<br>");
        out.print( "getLastName(): " + users[i].getLastName() + "<br>");
        out.print( "getMobilePhone(): " + users[i].getMobilePhone() + "<br>");
        out.print( "getMobilePhone(): " + users[i].getMobilePhone() + "<br>");
        out.print( "getWorkPhone(): " + users[i].getWorkPhone() + "<br>");
        out.print( "getZip(): " + users[i].getZip() + "<br>");%>
        </p><%
    }
%>