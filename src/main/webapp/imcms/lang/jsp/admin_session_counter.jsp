<%@ page import="com.imcode.imcms.servlet.superadmin.AdminCounter,
                 imcode.util.jscalendar.JSCalendar" %>
<%@page contentType="text/html; charset=UTF-8"%><%@taglib prefix="vel" uri="imcmsvelocity"%>
<%
    AdminCounter.AdminSessionCounterPage adminSessionCounterPage = (AdminCounter.AdminSessionCounterPage)request.getAttribute(AdminCounter.AdminSessionCounterPage.REQUEST_ATTRIBUTE__PAGE);;
    JSCalendar jsCalendar = adminSessionCounterPage.getJSCalendar(request);
    String calendarButtonTitle = "<? webapp/imcms/lang/jscalendar/show_calendar_button ?>";

%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/search/search_documents.html/1 ?></title>

<link rel="stylesheet" href="$contextPath/imcms/css/imcms_admin.css.jsp" type="text/css">
<script src="$contextPath/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>
<%= jsCalendar.getHeadTagScripts() %>

</head>

<body bgcolor="#FFFFFF">
#gui_outer_start()
#gui_head( "<? webapp/imcms/lang/jsp/admin_session_counter.jsp/headline ?>" )


<table border="0" cellspacing="0" cellpadding="0">
    <form action="AdminManager">
    <tr>
        <td><input class="imcmsFormBtn" type="submit" name="<? global/back ?>" value="<? global/back ?>">&nbsp;</td>
        <td><input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>" class="imcmsFormBtn" onClick="openHelpW('SessionCounter')"></td>
    </tr>
    </form>
</table>


#gui_mid()

<table width="370">
    <form method="post" action="AdminCounter">
    <tr>
        <td><? webapp/imcms/lang/jsp/admin_session_counter.jsp/value ?></td>
        <td><input type="text" name="<%=AdminCounter.AdminSessionCounterPage.REQUEST_PARAMETER__COUNTER_VALUE%>" value="<%=adminSessionCounterPage.getCounterValue()%>" maxlength="80" size="10"></td>
        <td><input type="submit" class="imcmsFormBtnSmall" name="setSessionCounter" value="<? webapp/imcms/lang/jsp/admin_session_counter.jsp/change ?>"></td>
    </tr>
    <tr>
        <td><? webapp/imcms/lang/jsp/admin_session_counter.jsp/startdate ?></td>
        <td><input type="text" id="<%=AdminCounter.AdminSessionCounterPage.REQUEST_PARAMETER__DATE_VALUE%>" name="<%=AdminCounter.AdminSessionCounterPage.REQUEST_PARAMETER__DATE_VALUE%>" value="<%=adminSessionCounterPage.getNewDateStr()%>" maxlength="80" size="10">
        <%= jsCalendar.getInstance(AdminCounter.AdminSessionCounterPage.REQUEST_PARAMETER__DATE_VALUE, null).getButton(calendarButtonTitle) %>
        </td>
        <td><input type="submit" class="imcmsFormBtnSmall" name="setDate" value="<? webapp/imcms/lang/jsp/admin_session_counter.jsp/change ?>"></td>
    </tr>
    <tr>
        <td colspan="3"><font color="#FF0000"><%=adminSessionCounterPage.getErrormsg()%></font></td>
    </tr>
    </form>
</table>

#gui_end_of_page()
</vel:velocity>
