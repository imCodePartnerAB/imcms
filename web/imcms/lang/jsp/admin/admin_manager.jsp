<%@ page import="com.imcode.imcms.servlet.superadmin.AdminManager,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 imcode.util.DateConstants,
                 imcode.server.document.DocumentMapper,
                 imcode.server.Imcms,
                 com.imcode.imcms.servlet.beans.AdminManagerSubreport,
                 java.util.*"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.beans.AdminManagerSubReportListItemBean" scope="request" />
<%
    AdminManager.AdminManagerPage adminManagerPage = (AdminManager.AdminManagerPage) request.getAttribute(AdminManager.AdminManagerPage.REQUEST_ATTRIBUTE__PAGE) ;
%>

<%@ include file="gui_tabs.jsp" %>

<%
    Tab[] tabs = {
        new Tab("new", "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0 ?>", "AdminManager?show=new" ),
        new Tab("reminders", "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1 ?>", "AdminManager?show=reminders"),
        new Tab("summary", "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2 ?>", "AdminManager?show=summary"),
        new Tab("search", "<? global/Search ?>", "AdminManager?show=search")
    } ;

    String tabString = getTabs(tabs, adminManagerPage.getTabName(), request ) ;
%>

<vel:velocity>
<html>
<head>

<title><? web/imcms/lang/jsp/admin/admin_manager.jsp/6 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

</head>
<body id="body" onLoad="focusField(1,'AdminTask')">
<!--gui_outer_start -->
#gui_outer_start()
<!--gui_head -->
#gui_head( "<? web/imcms/lang/jsp/admin/admin_manager.jsp/1 ?> - <%= adminManagerPage.getHeading().toLocalizedString( request ) %>" )
<!-- /gui_head -->

    <table  border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td colspan="4" id="adm">
                <table border="0" cellspacing="0" cellpadding="0">
                    <form method="post" action="AdminManager" name="AdminManager">
                         <% if ( null != adminManagerPage.getHtmlAdminPart() ) { %>
                            <tr>
                                <td><%= adminManagerPage.getHtmlAdminPart() %></td>
                                <td>&nbsp; &nbsp;</td>
                                <td><input type="submit" class="imcmsFormBtnSmall" style="height:20"  name="submit" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/2 ?>"></td>
                            </tr>
                        <% }else { %>
                            <tr><td colspan="3">&nbsp;</td></tr>
                        <%}%>
                    </form>
                </table></td>
                <form action="StartDoc">
            <td colspan="2" align="right"><input type="submit" class="imcmsFormBtnSmall" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/3 ?>">
                &nbsp;&nbsp;<input type="button" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/4 ?>" title="<? web/imcms/lang/jsp/admin/admin_manager.jsp/5 ?>" class="imcmsFormBtnSmall" onClick="openHelpW(28)"></td>
                </form>
        </tr>
    </table>
#gui_mid_tabs1()
<%= tabString %>
#gui_mid_tabs2()
    <form method="GET" name="subreport" action="AdminManager">
    <input type="hidden" name="<%= AdminManager.REQUEST_PARAMETER__SHOW %>" value="<%= adminManagerPage.getTabName() %>">
        <%
          List subreports = adminManagerPage.getSubreports() ;
          for ( Iterator iterator = subreports.iterator(); iterator.hasNext(); ) {
              AdminManagerSubreport subreport = (AdminManagerSubreport)iterator.next();
              request.setAttribute( "subreport", subreport );
              %><jsp:include page="admin_manager_subreport.jsp"/><%
          }
        %>
    </form>

</table>

#gui_end_of_page()

</vel:velocity>

