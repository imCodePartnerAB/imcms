<%@ page import="com.imcode.imcms.servlet.superadmin.AdminManager,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility,
                 java.util.Date,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 imcode.util.DateConstants,
                 java.util.List,
                 com.imcode.imcms.api.User,
                 imcode.server.document.DocumentMapper,
                 imcode.server.Imcms,
                 java.util.HashMap,
                 imcode.util.Html,
                 java.util.Arrays,
                 org.apache.commons.collections.Transformer,
                 org.apache.commons.lang.StringEscapeUtils,
                 com.imcode.imcms.servlet.AdminManagerSearchPage,
                 com.imcode.imcms.flow.Page"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>

<%! private String IMG_PATH;
    private String TAB_TO_SHOW;
%>
<%

    AdminManagerSearchPage adminManagerSearchPage = (AdminManagerSearchPage) Page.fromRequest(request) ;
    AdminManager.AdminManagerPage adminManagerPage = adminManagerSearchPage.getAdminManagerPage() ;

    IMG_PATH  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin" ;
    TAB_TO_SHOW = "search";
%>

<%@ include file="gui_tabs.jsp" %>

<%
    String[][] arrTabs = {
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0 ?>" , "AdminManager?show=new" },
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1 ?>" , "AdminManager?show=reminders" },
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2 ?>" , "AdminManager?show=summary" },
	{ "<? global/Search ?>"                                         , "AdminManager?show=search" }
} ;

    String tabs = getTabs(arrTabs, 3) ;

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
#gui_head( "<? web/imcms/lang/jsp/admin/admin_manager.jsp/1 ?> - <? global/Search ?>" )
<!-- /gui_head -->
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
    <form method="post" action="AdminManager">
	<td colspan="4" id="adm">
        <table border="0" cellspacing="0" cellpadding="0">
	    <% if ( null != adminManagerPage.getHtml_admin_part() ) { %>
        <tr>
		    <td><%= adminManagerPage.getHtml_admin_part() %></td>
		    <td>&nbsp; &nbsp;</td>
		    <td><input type="submit" class="imcmsFormBtnSmall" style="height:20"  name="submit" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/2 ?>"></td>
        </tr>
        <% }else { %>
        <tr><td colspan="3">&nbsp;</td></tr>
        <%}%>
	    </table></td>
        </form>
        <form action="StartDoc">
    <td colspan="2" align="right"><input type="submit" class="imcmsFormBtnSmall" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/3 ?>">
	    &nbsp;&nbsp;<input type="button" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/4 ?>" title="<? web/imcms/lang/jsp/admin/admin_manager.jsp/5 ?>" class="imcmsFormBtnSmall" onClick="openHelpW(28)"></td>
        </form>
</tr>
</table>
#gui_mid_tabs1()
<%= tabs %>
#gui_mid_tabs2()

<table border="0" cellspacing="0" cellpadding="2" width="656" align="center">

<tr>
    <td colspan="2"><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="25"></td>
</tr>
<tr>
    <td colspan="2" height="22"><span class="imcmsAdmHeading"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/1 ?></span></td>
</tr>
<tr>
    <td colspan="2"><img src="<%= IMG_PATH %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
</tr>
<tr>
<td colspan="2">

<form method="post" action="PageDispatcher">
<input type="hidden" name="<%= AdminManager.REQUEST_PARAMETER__FROMPAGE %>" value="<%= AdminManager.PAGE_SEARCH %>">
<jsp:include page="../search_documents_form.jsp" />

<jsp:include page="../search_documents_results.jsp" />
</td>

</tr>
</table>
</form>
#gui_end_of_page()

</vel:velocity>
