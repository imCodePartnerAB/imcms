<%@ page import="com.imcode.imcms.servlet.superadmin.AdminManager,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility,
                 java.util.Date,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 imcode.util.DateConstants,
                 java.util.List,
                 imcode.server.document.DocumentMapper,
                 imcode.server.Imcms,
                 java.util.HashMap"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.beans.AdminManagerSubReportListItemBean" scope="request" />
<%! private String IMG_PATH;
    private String TAB_TO_SHOW;
    private String LIST_TYPE;
    private String SORTORDER_OPTION_SELECTED;
%>
<%

    AdminManager.AdminManagerPage adminManagerPage = (AdminManager.AdminManagerPage) request.getAttribute(AdminManager.AdminManagerPage.REQUEST_ATTRIBUTE__PAGE);
    List documents_archived_less_then_one_week = adminManagerPage.getDocuments_archived_less_then_one_week();
    List documents_publication_end_less_then_one_week = adminManagerPage.getDocuments_publication_end_less_then_one_week();
    List documents_not_changed_in_six_month = adminManagerPage.getDocuments_not_changed_in_six_month();
    DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
    HashMap current_sortorderMap = adminManagerPage.getCurrent_sortorderMap();
    HashMap expand_listMap = adminManagerPage.getExpand_listMap();

    IMG_PATH  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin" ;
    TAB_TO_SHOW = "reminders";
%>

<%@ include file="gui_tabs.jsp" %>

<%
    String[][] arrTabs = {
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0 ?>" , "AdminManager?show=new" },
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1 ?>" , "AdminManager?show=reminders" },
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2 ?>" , "AdminManager?show=summary" },
	{ "<? global/Search ?>"                                         , "AdminManager?show=search" }
} ;

    String tabs = getTabs(arrTabs, 1) ;
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
#gui_head( "<? web/imcms/lang/jsp/admin/admin_manager.jsp/1 ?> - <? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1 ?>" )
<!-- /gui_head -->

<table  border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td colspan="4" id="adm">
	    <table border="0" cellspacing="0" cellpadding="0">
        <form method="post" action="AdminManager" name="AdminManager">
         <% if ( null != adminManagerPage.getHtml_admin_part() ) { %>
	    <tr>
		    <td><%= adminManagerPage.getHtml_admin_part() %></td>
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
<%= tabs %>
#gui_mid_tabs2()

<jsp:useBean id="subreport" scope="request" class="com.imcode.imcms.servlet.beans.AdminManagerSubreportBean" />

<jsp:setProperty name="subreport" property="documents" value="<%= documents_archived_less_then_one_week %>" />
<jsp:setProperty name="subreport" property="heading" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/2 ?>"/>
<jsp:setProperty name="subreport" property="name" value="<%= AdminManager.LIST_TYPE__list_documents_archived_less_then_one_week %>"/>
<jsp:setProperty name="subreport" property="expanded" value="<%= expand_listMap.get(subreport.getName()).toString().equals("expand") %>"/>
<jsp:setProperty name="subreport" property="sortorder" value="<%= current_sortorderMap.get( subreport.getName() ) %>"/>

<jsp:include page="admin_manager_subreport.jsp"/>


<jsp:setProperty name="subreport" property="documents" value="<%= documents_publication_end_less_then_one_week %>" />
<jsp:setProperty name="subreport" property="heading" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/3 ?>"/>
<jsp:setProperty name="subreport" property="name" value="<%= AdminManager.LIST_TYPE__list_documents_publication_end_less_then_one_week %>"/>
<jsp:setProperty name="subreport" property="expanded" value="<%= expand_listMap.get(subreport.getName()).toString().equals("expand") %>"/>
<jsp:setProperty name="subreport" property="sortorder" value="<%= current_sortorderMap.get( subreport.getName() ) %>"/>

<jsp:include page="admin_manager_subreport.jsp"/>


<jsp:setProperty name="subreport" property="documents" value="<%= documents_not_changed_in_six_month %>" />
<jsp:setProperty name="subreport" property="heading" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/4 ?>"/>
<jsp:setProperty name="subreport" property="name" value="<%= AdminManager.LIST_TYPE__list_documents_not_changed_in_six_month %>"/>
<jsp:setProperty name="subreport" property="expanded" value="<%= expand_listMap.get(subreport.getName()).toString().equals("expand") %>"/>
<jsp:setProperty name="subreport" property="sortorder" value="<%= current_sortorderMap.get( subreport.getName() ) %>"/>

<jsp:include page="admin_manager_subreport.jsp"/>
</table>

#gui_end_of_page()

</vel:velocity>

