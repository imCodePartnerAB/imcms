<%@ page import="com.imcode.imcms.servlet.beans.AdminManagerSubreport,
                 com.imcode.imcms.servlet.superadmin.AdminManager,
                 imcode.util.jscalendar.JSCalendar,
                 java.util.Iterator,
                 java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.beans.AdminManagerSubReportListItemBean" scope="request" />
<%
    AdminManager.AdminManagerPage adminManagerPage = (AdminManager.AdminManagerPage) request.getAttribute(AdminManager.AdminManagerPage.REQUEST_ATTRIBUTE__PAGE) ;
    String imagesPath = request.getContextPath() + "/imcms/" + Utility.getLoggedOnUser(request).getLanguage() + "/images/admin/";
    JSCalendar jsCalendar = adminManagerPage.getJSCalendar(request);
%>

<%@ include file="gui_tabs.jsp" %>

<%
    Tab[] tabs = {
// IMCMS-94: commented out because we don't create docs in this old menu any more
//        new Tab(AdminManager.PARAMETER_VALUE__SHOW_CREATE, "<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0 ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_CREATE ),
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_RECENT, "<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1 ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_RECENT ),
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_REMINDERS, "<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2 ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_REMINDERS),
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_SUMMARY, "<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/3 ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_SUMMARY),
        new Tab(AdminManager.PARAMETER_VALUE__SHOW_SEARCH, "<? global/Search ?>", "AdminManager?show=" + AdminManager.PARAMETER_VALUE__SHOW_SEARCH)
    } ;

    String tabString = getTabs(tabs, adminManagerPage.getTabName(), request ) ;
%>

<html>
<head>

<title><? webapp/imcms/lang/jsp/admin/admin_manager.jsp/6 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
	<script src="//ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>
<%= jsCalendar.getHeadTagScripts()%>

</head>
<body id="body" onLoad="focusField(1,'AdminTask')">


<!--gui_outer_start -->
    <ui:imcms_gui_outer_start/>
<!--gui_head -->
    <c:set var="heading">
        <fmt:message
                key="webapp/imcms/lang/jsp/admin/admin_manager.jsp/1"/> - <%= adminManagerPage.getHeading().toLocalizedString(request) %>
    </c:set>
    <ui:imcms_gui_head heading="${heading}"/>
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
				%> value="<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/2 ?>"></td>
			</tr><%
			} else { %>
			<tr>
				<td>&nbsp;</td>
			</tr><%
			} %>
			</form>
			</table></td>
			<form action="StartDoc">
			    <td colspan="2" align="right"><input type="submit" class="imcmsFormBtnSmall" value="<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/3 ?>">&nbsp;&nbsp;
                    <input type="button" value="<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/4 ?>" title="<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/5 ?>"
                        class="imcmsFormBtnSmall" onClick="openHelpW('MyPages')" ></td>
		    </form>
		</tr>
		</table>
<ui:imcms_gui_mid_tabs1/>
<%= tabString %>
<ui:imcms_gui_mid_tabs2/>
<%
if (!AdminManager.PARAMETER_VALUE__SHOW_SEARCH.equals(adminManagerPage.getTabName())) {
	if (null != adminManagerPage.getErrorMessage()) { %>
	<div style="color: red"><%=
		adminManagerPage.getErrorMessage().toLocalizedString(request)
		%></div><%
	} %>
	<form method="POST" action="AdminManager">
	<table border="0" cellspacing="0" cellpadding="2" width="656">
    <input type="hidden" name="<%= AdminManager.REQUEST_PARAMETER__SHOW %>" value="<%= adminManagerPage.getTabName() %>">
        <%-- IMCMS-94: commented out because we don't create docs in this old menu any more --%>
        <%--<%--%>
    <%--if (AdminManager.PARAMETER_VALUE__SHOW_CREATE.equals( adminManagerPage.getTabName() )--%>
            <%--|| AdminManager.PARAMETER_VALUE__SHOW_RECENT.equals( adminManagerPage.getTabName() ) ) {--%>
    <%--%>--%>
	<%--<tr>--%>
		<%--<td><img src="<%= imagesPath %>/1x1.gif" width="1" height="26"></td>--%>
	<%--</tr>--%>
	<%--<tr>--%>
		<%--<td colspan="2" height="22"><span class="imcmsAdmHeading"><? webapp/imcms/lang/jsp/admin/admin_manager.jsp/heading_create_new ?></span></td>--%>
	<%--</tr>--%>
	<%--<tr>--%>
		<%--<td colspan="2"><img src="<%= imagesPath %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>--%>
	<%--</tr>--%>
	<%--<tr>--%>
		<%--<td>--%>
		<%--<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/create_new ?>--%>
		<%--<select name="<%= AdminManager.REQUEST_PARAMETER__CREATE_DOCUMENT_ACTION %>"><%--%>
			<%--DocumentTypeDomainObject[] documentTypes = {--%>
				<%--DocumentTypeDomainObject.TEXT,--%>
				<%--DocumentTypeDomainObject.URL,--%>
				<%--DocumentTypeDomainObject.FILE,--%>
			<%--} ;--%>
			<%--for ( int i = 0; i < documentTypes.length; i++ ) {--%>
				<%--DocumentTypeDomainObject documentTypeId = documentTypes[i] ; %>--%>
			<%--<option value="<%= documentTypeId.getId() %>"><%= documentTypeId.getName().toLocalizedString( request ) %></option><%--%>
			<%--} %>--%>
			<%--<option value="<%= AdminManager.REQUEST_PARAMETER__ACTION__COPY %>"><? global/Copy ?></option>--%>
		<%--</select>--%>
		<%--&nbsp;<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/based_on ?>&nbsp;--%>
        <%--<select onchange="getElementById('document_id').value = this.value;">--%>
            <%--<option value=""></option>--%>
            <%--<%--%>
                <%--ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);--%>
                <%--DataSourceDatabase database = new DataSourceDatabase(cms.getDatabaseService().getDataSource());--%>
                <%--ProfileMapper profileMapper = new ProfileMapper(database);--%>
                <%--List<Profile> profiles = profileMapper.getAll();--%>
                <%--for ( Profile profile : profiles ) {--%>
                    <%--%><option value="<%= StringEscapeUtils.escapeHtml4(profile.getDocumentName()) %>"><%= StringEscapeUtils.escapeHtml4(profile.getName()) %></option><%--%>
                <%--}--%>
            <%--%>--%>
        <%--</select>--%>
		<%--&nbsp;<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/with_document ?>&nbsp;--%>
		<%--<input type="text" id="document_id" name="<%= AdminManager.REQUEST_PARAMETER__NEW_DOCUMENT_PARENT_ID %>" value="" size="40" maxlength="255">&nbsp;--%>
		<%--<input type="submit" name="<%= AdminManager.REQUEST_PARAMETER__CREATE_NEW_DOCUMENT %>" value="<? webapp/imcms/lang/jsp/admin/admin_manager.jsp/create_button ?>" class="imcmsFormBtnSmall"></td>--%>
	<%--</tr>--%>
    <%--<tr>--%>
		<%--<td><img src="<%= imagesPath %>/1x1.gif" width="1" height="10"></td>--%>
	<%--</tr>--%>
    <%--</table><%--%>
	<%--}%>--%>
        <%
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
		<td colspan="2" height="22"><span class="imcmsAdmHeading"><? webapp/imcms/lang/jsp/admin/admin_manager_search.jsp/1 ?></span></td>
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
    <ui:imcms_gui_bottom/>
    <ui:imcms_gui_outer_end/>
</body>
</html>
