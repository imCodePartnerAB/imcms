<%@ page import="com.imcode.imcms.servlet.beans.AdminManagerSubreport,
                 com.imcode.imcms.servlet.superadmin.AdminManager,
                 imcode.server.document.DocumentTypeDomainObject,
                 imcode.util.jscalendar.JSCalendar,
                 java.util.Iterator,
                 java.util.List, com.imcode.imcms.api.ContentManagementSystem, com.imcode.db.DataSourceDatabase, com.imcode.imcms.mapping.ProfileMapper, imcode.server.document.Profile, org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html; charset=UTF-8" %><%@taglib prefix="vel" uri="imcmsvelocity"%>
<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.beans.AdminManagerSubReportListItemBean" scope="request" />
<%
    AdminManager.AdminManagerPage adminManagerPage = (AdminManager.AdminManagerPage) request.getAttribute(AdminManager.AdminManagerPage.REQUEST_ATTRIBUTE__PAGE) ;
    String imagesPath  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    JSCalendar jsCalendar = adminManagerPage.getJSCalendar(request);
%>

<%@ include file="gui_tabs.jsp" %>

<%
    Tab[] tabs = {
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_CREATE, "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0 ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_CREATE ),
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_RECENT, "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1 ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_RECENT ),
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_REMINDERS, "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2 ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_REMINDERS),
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_SUMMARY, "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/3 ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_SUMMARY),
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_SEARCH, "<? global/Search ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_SEARCH)
    } ;

    String tabString = getTabs(tabs, adminManagerPage.getTabName(), request ) ;
%>

<vel:velocity>
<html>
<head>

<title><? web/imcms/lang/jsp/admin/admin_manager.jsp/6 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
<%= jsCalendar.getHeadTagScripts()%>

</head>
<body id="body" onLoad="focusField(1,'AdminTask')">


<!--gui_outer_start -->
#gui_outer_start()
<!--gui_head -->
#gui_head( "<? web/imcms/lang/jsp/admin/admin_manager.jsp/1 ?> - <%= adminManagerPage.getHeading().toLocalizedString( request ) %>" )
<!-- /gui_head -->
		<table border="0" cellspacing="0" cellpadding="0" width="656">
        <tr>
			<td id="adm">
			<table border="0" cellspacing="0" cellpadding="0">
			<form method="post" action="AdminManager" name="AdminManager"><%
			if ( null != adminManagerPage.getHtmlAdminPart() ) { %>
			<tr>
				<td><%= adminManagerPage.getHtmlAdminPart() %></td>
				<td>&nbsp; &nbsp;</td>
				<td><input type="submit"<%
				%> class="imcmsFormBtnSmall" <%
				%>name="submit"<%
				%> value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/2 ?>"></td>
			</tr><%
			} else { %>
			<tr>
				<td>&nbsp;</td>
			</tr><%
			} %>
			</form>
			</table></td>
			<form action="StartDoc">
			    <td colspan="2" align="right"><input type="submit" class="imcmsFormBtnSmall" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/3 ?>">&nbsp;&nbsp;
                    <input type="button" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/4 ?>" title="<? web/imcms/lang/jsp/admin/admin_manager.jsp/5 ?>" 
                        class="imcmsFormBtnSmall" onClick="openHelpW('MyPages')" ></td>
		    </form>
		</tr>
		</table>
#gui_mid_tabs1()
<%= tabString %>
#gui_mid_tabs2()<%
if (!AdminManager.PARAMETER_VALUE__SHOW_SEARCH.equals(adminManagerPage.getTabName())) {
	if (null != adminManagerPage.getErrorMessage()) { %>
	<div style="color: red"><%=
		adminManagerPage.getErrorMessage().toLocalizedString(request)
		%></div><%
	} %>
	<form method="POST" action="AdminManager">
	<table border="0" cellspacing="0" cellpadding="2" width="656">
    <input type="hidden" name="<%= AdminManager.REQUEST_PARAMETER__SHOW %>" value="<%= adminManagerPage.getTabName() %>"><%
    if (AdminManager.PARAMETER_VALUE__SHOW_CREATE.equals( adminManagerPage.getTabName() ) || AdminManager.PARAMETER_VALUE__SHOW_RECENT.equals( adminManagerPage.getTabName() ) ) {%>
	<tr>
		<td><img src="<%= imagesPath %>/1x1.gif" width="1" height="26"></td>
	</tr>
	<tr>
		<td colspan="2" height="22"><span class="imcmsAdmHeading"><? web/imcms/lang/jsp/admin/admin_manager.jsp/heading_create_new ?></span></td>
	</tr>
	<tr>
		<td colspan="2"><img src="<%= imagesPath %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
	</tr>
	<tr>
		<td>
		<? web/imcms/lang/jsp/admin/admin_manager.jsp/create_new ?>
		<select name="<%= AdminManager.REQUEST_PARAMETER__CREATE_DOCUMENT_ACTION %>"><%
			DocumentTypeDomainObject[] documentTypes = {
				DocumentTypeDomainObject.TEXT,
				DocumentTypeDomainObject.URL,
				DocumentTypeDomainObject.FILE,
			} ;

			for ( int i = 0; i < documentTypes.length; i++ ) {
				DocumentTypeDomainObject documentType = documentTypes[i] ; %>
			<option value="<%= documentType.getId() %>"><%= documentType.getName().toLocalizedString( request ) %></option><%
			} %>
			<option value="<%= AdminManager.REQUEST_PARAMETER__ACTION__COPY %>"><? global/Copy ?></option>
		</select>
		&nbsp;<? web/imcms/lang/jsp/admin/admin_manager.jsp/based_on ?>&nbsp;
        <select onchange="getElementById('document_id').value = this.value;">
            <option value=""></option>
            <%
                ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
                DataSourceDatabase database = new DataSourceDatabase(cms.getDatabaseService().getDataSource());
                ProfileMapper profileMapper = new ProfileMapper(database);
                List<Profile> profiles = profileMapper.getAll();
                for ( Profile profile : profiles ) {
                    %><option value="<%= StringEscapeUtils.escapeHtml(profile.getDocumentName()) %>"><%= StringEscapeUtils.escapeHtml(profile.getName()) %></option><%
                }
            %>
        </select>
		&nbsp;<? web/imcms/lang/jsp/admin/admin_manager.jsp/with_document ?>&nbsp;
		<input type="text" id="document_id" name="<%= AdminManager.REQUEST_PARAMETER__NEW_DOCUMENT_PARENT_ID %>" value="" size="40" maxlength="255">&nbsp;
		<input type="submit" name="<%= AdminManager.REQUEST_PARAMETER__CREATE_NEW_DOCUMENT %>" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/create_button ?>" class="imcmsFormBtnSmall"></td>
	</tr>
    <tr>
		<td><img src="<%= imagesPath %>/1x1.gif" width="1" height="10"></td>
	</tr>
    </table><%
	}

	List subreports = adminManagerPage.getSubreports() ;
	for ( Iterator iterator = subreports.iterator(); iterator.hasNext(); ) {
		AdminManagerSubreport subreport = (AdminManagerSubreport)iterator.next();
		request.setAttribute( "subreport", subreport );
		%><jsp:include page="admin_manager_subreport.jsp"/><%
	} %>
	</form><%
} else { %>
	<table border="0" cellspacing="0" cellpadding="2" width="656">
	<tr>
		<td colspan="2"><img src="<%= imagesPath %>/1x1.gif" width="1" height="26"></td>
	</tr>
	<tr>
		<td colspan="2" height="22"><span class="imcmsAdmHeading"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/1 ?></span></td>
	</tr>
	<tr>
		<td colspan="2"><img src="<%= imagesPath %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
	</tr>
	</table>
	<form method="GET" action="SearchDocuments">
		<input type="hidden" name="<%= AdminManager.REQUEST_PARAMETER__FROMPAGE %>" value="<%= AdminManager.PAGE_SEARCH %>">
		<jsp:include page="../search_documents_form.jsp" />
		<jsp:include page="../search_documents_results.jsp" />
	</form><%
} %>
	#gui_bottom()
	#gui_outer_end()
</vel:velocity>
</body>
</html>
