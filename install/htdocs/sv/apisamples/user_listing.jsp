<%@ page import="com.imcode.imcms.*" %>

<h2>Listing current users in the IMCMS system</h2>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    UserService userService = imcmsSystem.getUserService();
    User[] users = userService.getAllUsers();
    for( int i = 0; i < users.length ; i++ ){%>
        <p>
        User "<%= users[i].getLoginName()%>" has the following attributes:<br><%
        out.print( "getAddress(): " + users[i].getAddress() + "<br>");
        out.print( "getCity(): " + users[i].getCity() + "<br>");
        out.print( "getCompany(): " + users[i].getCompany() + "<br>");
        out.print( "getCountry(): " + users[i].getCountry() + "<br>");
        out.print( "getCountyCouncil(): " + users[i].getCountyCouncil() + "<br>");
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