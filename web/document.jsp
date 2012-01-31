<%@ page import="imcode.server.user.UserDomainObject" %>
<%@ page import="imcode.util.Utility" %>
<%@ page import="com.imcode.imcms.api.ContentManagementSystem" %>
<%@ page import="imcode.server.document.DocumentDomainObject" %>
<%@ page import="com.imcode.imcms.api.Document" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="com.imcode.imcms.api.DocumentService" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="static java.util.GregorianCalendar.*" %>

<%
    UserDomainObject user = Utility.getLoggedOnUser(request) ;
    ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
    DocumentService docService = cms.getDocumentService();
    Document doc = docService.getDocument(1001);
    Calendar calendar = GregorianCalendar.getInstance();

    if (request.getMethod().equalsIgnoreCase("POST")) {
        calendar.set(DAY_OF_MONTH, Integer.parseInt(request.getParameter("DAY_OF_MONTH")));
        calendar.set(MONTH, Integer.parseInt(request.getParameter("MONTH")) - 1);
        calendar.set(YEAR, Integer.parseInt(request.getParameter("YEAR")));
        calendar.set(HOUR_OF_DAY, Integer.parseInt(request.getParameter("HOUR_OF_DAY")));
        calendar.set(MINUTE, Integer.parseInt(request.getParameter("MINUTE")));

        doc.setModifiedDatetime(calendar.getTime());
        docService.saveChanges(doc);
        doc = docService.getDocument(1001);
    }

    calendar.setTime(doc.getModifiedDatetime());
%>

<html>
    <body>
        Last modified by <%=doc.getModifier()%>:<br/>
        <form method="POST">
            <input type="text" size="4" name="DAY_OF_MONTH" value="<%=calendar.get(Calendar.DAY_OF_MONTH)%>"/>
            -
            <input type="text" size="4" name="MONTH" value="<%=calendar.get(Calendar.MONTH) + 1%>"/>
            -
            <input type="text" size="4" name="YEAR" value="<%=calendar.get(Calendar.YEAR)%>"/>
            #
            <input type="text" size="4" name="HOUR_OF_DAY" value="<%=calendar.get(Calendar.HOUR_OF_DAY)%>"/>
            :
            <input type="text" size="4" name="MINUTE" value="<%=calendar.get(Calendar.MINUTE)%>"/>

            <input type="submit" value="Update">
        </form>
    </body>
</html>