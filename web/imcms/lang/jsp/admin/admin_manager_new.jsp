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
                 java.util.HashMap"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>

<%! private String IMG_PATH;
    private String TAB_TO_SHOW;
    private String LIST_TYPE;
    private String SORTORDER_OPTION_SELECTED;


%>
<%

    AdminManager.AdminManagerPage adminManagerPage = (AdminManager.AdminManagerPage) request.getAttribute(AdminManager.AdminManagerPage.REQUEST_ATTRIBUTE__PAGE);
    List documents_new = adminManagerPage.getDocuments_new();
    DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
    HashMap current_sortorderMap = adminManagerPage.getCurrent_sortorderMap();
    HashMap expand_listMap = adminManagerPage.getExpand_listMap();

    IMG_PATH  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    TAB_TO_SHOW = "new";
%><%!



String formatDatetime(Date datetime) {
    if (null == datetime) {
        return "" ;
    }
    DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING + "'&nbsp;'"+DateConstants.TIME_NO_SECONDS_FORMAT_STRING ) ;
    return dateFormat.format(datetime) ;
}

%>

<%@ include file="gui_tabs.jsp" %>

<%
    String[][] arrTabs = {
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0 ?>" , "AdminManager?show=new" },
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1 ?>" , "AdminManager?show=reminders" },
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2 ?>" , "AdminManager?show=summary" },
	{ "<? global/Search ?>"                                         , "AdminManager?show=search" }
} ;

    String tabs = getTabs(arrTabs, 0) ;

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
#gui_head( "<? web/imcms/lang/jsp/admin/admin_manager.jsp/1 ?> - <? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0 ?>" )
<!-- /gui_head -->

<table  border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td colspan="4" id="adm">
	    <table border="0" cellspacing="0" cellpadding="0">
        <form method="post" action="AdminManager">
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

<% String subreport_heading = "<? web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/1 ?>";
   LIST_TYPE = AdminManager.LIST_TYPE__list_new_not_approved ;
   int documents_found = documents_new.size(); %>
<%@include file="admin_manager_inc_subreport_header.jsp"%>

    <!-- list item -->
   <% DocumentDomainObject document;
      boolean expand = false;
      for (int i = 0; i < documents_new.size(); i++) {
        expand = i < 2 || expand_listMap.get(LIST_TYPE).toString().equals("expand") ? true : false;
        document = (DocumentDomainObject) documents_new.get(i); %>
         <%@include file="admin_manager_inc_list_item.jsp"%>
    <!-- / list item -->
  <% } %>

</table>

</td>
</tr>
</table>

#gui_end_of_page()

</vel:velocity>
