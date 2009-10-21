<%@ page import="com.imcode.imcms.api.ContentManagementSystem" errorPage="error.jsp" %><%@ page import="com.imcode.imcms.api.User"%><%@ page import="com.imcode.imcms.api.UserService"%>

<h2>Listing attributes for user "admin" in the IMCMS system</h2>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
    User user = userService.getUser("admin"); %>

    <p>
    User "<%= user.getLoginName()%>" has the following attributes:<br><%
    out.print( "getAddress(): " + user.getAddress() + "<br>");
    out.print( "getCity(): " + user.getCity() + "<br>");
    out.print( "getCompany(): " + user.getCompany() + "<br>");
    out.print( "getCountry(): " + user.getCountry() + "<br>");
    out.print( "getProvince(): " + user.getProvince() + "<br>");
    out.print( "getEmailAddress(): " + user.getEmailAddress() + "<br>");
    out.print( "getFirstName(): " + user.getFirstName() + "<br>");
    out.print( "getHomePhone(): " + user.getHomePhone() + "<br>");
    out.print( "getLastName(): " + user.getLastName() + "<br>");
    out.print( "getMobilePhone(): " + user.getMobilePhone() + "<br>");
    out.print( "getMobilePhone(): " + user.getMobilePhone() + "<br>");
    out.print( "getWorkPhone(): " + user.getWorkPhone() + "<br>");
    out.print( "getZip(): " + user.getZip() + "<br>");%>
    </p>


<h2>Listing attributes for user with id=1 in the IMCMS system</h2>
<%
    int userid = 1;
    user = userService.getUser(userid); %>

    <p>
    User "<%= user.getLoginName()%>" has the following attributes:<br><%
    out.print( "getAddress(): " + user.getAddress() + "<br>");
    out.print( "getCity(): " + user.getCity() + "<br>");
    out.print( "getCompany(): " + user.getCompany() + "<br>");
    out.print( "getCountry(): " + user.getCountry() + "<br>");
    out.print( "getProvince(): " + user.getProvince() + "<br>");
    out.print( "getEmailAddress(): " + user.getEmailAddress() + "<br>");
    out.print( "getFirstName(): " + user.getFirstName() + "<br>");
    out.print( "getHomePhone(): " + user.getHomePhone() + "<br>");
    out.print( "getLastName(): " + user.getLastName() + "<br>");
    out.print( "getMobilePhone(): " + user.getMobilePhone() + "<br>");
    out.print( "getMobilePhone(): " + user.getMobilePhone() + "<br>");
    out.print( "getWorkPhone(): " + user.getWorkPhone() + "<br>");
    out.print( "getZip(): " + user.getZip() + "<br>");%>
    </p>