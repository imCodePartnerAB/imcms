<%@ page import="com.imcode.imcms.servlet.superadmin.AdminManager,
                 imcode.util.Utility,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 imcode.util.DateConstants,
                 imcode.server.Imcms,
                 com.imcode.imcms.servlet.beans.AdminManagerSubreport,
                 java.util.*,
                 imcode.server.document.*,
                 com.imcode.imcms.servlet.beans.Tab"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.beans.AdminManagerSubReportListItemBean" scope="request" />
<%
    AdminManager.AdminManagerPage adminManagerPage = (AdminManager.AdminManagerPage) request.getAttribute(AdminManager.AdminManagerPage.REQUEST_ATTRIBUTE__PAGE) ;
    String imagesPath  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
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
<% if (!AdminManager.PARAMETER_VALUE__SHOW_SEARCH.equals(adminManagerPage.getTabName())) { %>
    <form method="POST" action="AdminManager">
    <input type="hidden" name="<%= AdminManager.REQUEST_PARAMETER__SHOW %>" value="<%= adminManagerPage.getTabName() %>">
    <% if (AdminManager.PARAMETER_VALUE__SHOW_NEW.equals( adminManagerPage.getTabName() ) ) { %>
       #gui_heading( "<? web/imcms/lang/jsp/admin/admin_manager.jsp/heading_create_new ?>" )
        <%
            DocumentTypeDomainObject[] documentTypes = {
                DocumentDomainObject.DOCTYPE_TEXT,
                DocumentDomainObject.DOCTYPE_URL,
                DocumentDomainObject.DOCTYPE_FILE,
            } ;
        %>
        <? web/imcms/lang/jsp/admin/admin_manager.jsp/create_new ?>
        <select name="<%= AdminManager.REQUEST_PARAMETER__CREATE_DOCUMENT_ACTION %>">
            <%
                for ( int i = 0; i < documentTypes.length; i++ ) {
                    DocumentTypeDomainObject documentType = documentTypes[i];
                    %><option value="<%= documentType.getId() %>"><%= documentType.getName().toLocalizedString( request ) %></option><%
                }
            %>
            <option value="<%= AdminManager.REQUEST_PARAMETER__ACTION__COPY %>"><? global/Copy ?></option>
        </select>
        <? web/imcms/lang/jsp/admin/admin_manager.jsp/based_on ?>
        <input type="text" name="<%= AdminManager.REQUEST_PARAMETER__NEW_DOCUMENT_PARENT_ID %>" value="" size="5">
        <input type="submit" name="<%= AdminManager.REQUEST_PARAMETER__CREATE_NEW_DOCUMENT %>" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/create_button ?>">
    <% } %>

        <%
          List subreports = adminManagerPage.getSubreports() ;
          for ( Iterator iterator = subreports.iterator(); iterator.hasNext(); ) {
              AdminManagerSubreport subreport = (AdminManagerSubreport)iterator.next();
              request.setAttribute( "subreport", subreport );
              %><jsp:include page="admin_manager_subreport.jsp"/><%
          }
        %>
    </form>
    <% } else { %>

    <table border="0" cellspacing="0" cellpadding="2" width="656" align="center">
        <tr>
            <td colspan="2"><img src="<%= imagesPath %>/1x1.gif" width="1" height="25"></td>
        </tr>
        <tr>
            <td colspan="2" height="22"><span class="imcmsAdmHeading"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/1 ?></span></td>
        </tr>
        <tr>
            <td colspan="2"><img src="<%= imagesPath %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
        </tr>
        <tr>
            <td colspan="2">

                <form method="GET" action="SearchDocuments">
                    <input type="hidden" name="<%= AdminManager.REQUEST_PARAMETER__FROMPAGE %>" value="<%= AdminManager.PAGE_SEARCH %>">
                    <jsp:include page="../search_documents_form.jsp" />
                    <jsp:include page="../search_documents_results.jsp" />
                </form>

            </td>

        </tr>
    </table>
<% } %>

#gui_end_of_page()

</vel:velocity>

