<%@ page import="com.imcode.imcms.servlet.admin.UserBrowser,
                 com.imcode.imcms.servlet.admin.UserFinder,
                 com.imcode.imcms.servlet.superadmin.UserEditorPage,
                 imcode.server.Imcms,
                 imcode.server.user.RoleDomainObject,
                 imcode.server.user.UserDomainObject,
                imcode.util.Html,
                 imcode.util.HttpSessionUtils,
                 imcode.util.Utility,
                 org.apache.commons.lang3.StringUtils,
                 org.apache.commons.text.StringEscapeUtils,
                 java.util.Arrays"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%
    UserFinder userFinder = (UserFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
    UserBrowser.UserBrowserPage userBrowserPage = (UserBrowser.UserBrowserPage)request.getAttribute( UserBrowser.REQUEST_ATTRIBUTE__FORM_DATA ) ;
    RoleDomainObject[] allRoles = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getAllRolesExceptUsersRole();
    Utility.setDefaultHtmlContentType(response);
%>
<html>
<head>
<title><? templates/sv/AdminManager_adminTask_element.htm/2 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

</head>
<body onLoad="focusField(0,'<%= UserBrowser.REQUEST_PARAMETER__SEARCH_STRING %>');">
    <ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/sv/AdminManager_adminTask_element.htm/2"/>
</c:set>
    <ui:imcms_gui_head heading="${heading}"/>
    <table border="0" cellspacing="0" cellpadding="0">
<form name="argumentForm" action="UserBrowser" method="GET" target="_top">
<tr>
	<td><input type="submit"  class="imcmsFormBtn" name="<%= UserBrowser.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/back ?>"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>" class="imcmsFormBtn" onClick="openHelpW('UserAdmin')"></td>
</tr>
</table>
        <ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="0" width="600" align="center">
            <input type="hidden" name="<%= UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE %>"
                                value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest( request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE ) %>">
    <tr>
        <td colspan="2"><ui:imcms_gui_heading
                heading="<%= userFinder.getHeadline().toLocalizedString(request) %>"/></td>
    </tr>
    <tr>
        <td width="30%" class="imcmsAdmText"><? templates/sv/AdminChangeUser.htm/10 ?></td>
        <td width="70%">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="text" name="<%= UserBrowser.REQUEST_PARAMETER__SEARCH_STRING %>" size="20" maxlength="250" value="<%= StringEscapeUtils.escapeHtml4(userBrowserPage.getSearchString()) %>"></td>
            <td class="imcmsAdmDim">&nbsp; <? templates/sv/AdminChangeUser.htm/1001 ?></td>
        </tr>
        </table></td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/AdminChangeUser.htm/16 ?> </td>
        <td>
            <select name="<%= UserBrowser.REQUEST_PARAMETER__ROLE_ID %>" size="5" multiple >
                <%= Html.createOptionList(Arrays.asList(allRoles), Arrays.asList(userBrowserPage.getSelectedRoles()), new UserEditorPage.RoleToStringPairTransformer()) %>
		    </select>
        </td>
    </tr>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/AdminChangeUser.htm/12 ?></td>
        <td><input type="checkbox" name="<%= UserBrowser.REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS %>" value="0" <% if (userBrowserPage.isIncludeInactiveUsers()) { %>checked<%}%>> </td>
    </tr>
    <tr>
        <td class="imcmsAdmText">&nbsp;</td>
        <td>
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td><input type="submit" class="imcmsFormBtnSmall" name="<%= UserBrowser.REQUEST_PARAMETER__SHOW_USERS_BUTTON %>" value="<? templates/sv/AdminChangeUser.htm/2004 ?>"></td>
        </tr>
        </table>
    </td>
    </tr>
    <tr>
        <td colspan="2"><ui:imcms_gui_hr wantedcolor="cccccc"/></td>
    </tr>
    <tr>
        <td colspan="2" class="imcmsAdmText"><% UserDomainObject[] users = userBrowserPage.getUsers();
        int numberOfUsers =  users.length;
        if (numberOfUsers > 0 ) { %><? templates/sv/AdminChangeUser.htm/15 ?>&nbsp;(<%=numberOfUsers%>) <%
        }else{ %><? templates/sv/AdminChangeUser.htm/14 ?><% } %> </td>
    </tr>
    <tr>
        <td colspan="2">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr valign="top">
            <td width="80%">
            <select name="<%= UserBrowser.REQUEST_PARAMETER__USER_ID %>" size="15" style="width: 100%;">
                <%

                    for ( int i = 0; i < users.length; i++ ) {
                    UserDomainObject user = users[i];
                    %><option value="<%= user.getId() %>"><%= user.getLastName() %>, <%= user.getFirstName() %> [<%= user.getLoginName() %>] <%= StringUtils.isBlank(user.getTitle()) ? "" : ", "+user.getTitle() %> <%= StringUtils.isBlank(user.getCompany()) ? "" : ", "+user.getCompany() %> <%= StringUtils.isBlank(user.getEmailAddress()) ? "" : "&lt;"+user.getEmailAddress()+"&gt;" %></option><%
                } %>
            </select></td>
            <td width="20%" align="right">
            <input type="submit" class="imcmsFormBtnSmall"
                name="<%= UserBrowser.REQUEST_PARAMETER__SELECT_USER_BUTTON %>"
                value="<%= userFinder.getSelectButtonText().toLocalizedString(request) %>"
                style="width:10em">

                <div><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="3"></div>
                <input type="submit" class="imcmsFormBtnSmall"
                       name="<%= UserBrowser.REQUEST_PARAMETER__ARCHIVE_USER_BUTTON %>"
                       value="<? templates/sv/AdminChangeUser.htm/2008 ?>"
                       style="width:10em">
            <%
                UserDomainObject user = Utility.getLoggedOnUser(request);
                if (userFinder.isUsersAddable() && (user.isSuperAdmin())) { %>
                <div><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="3"></div>
                    <input type="submit" class="imcmsFormBtnSmall"
                        name="<%= UserBrowser.REQUEST_PARAMETER__ADD_USER %>"
                        value="<? templates/sv/AdminChangeUser.htm/2005 ?>" style="width:10em">
            <% } %>
            </td>
        </tr>
        </table></td>
    </tr>
</form>
</table>

<script language="JavaScript">
<!--
var sSearch = unescape(getParam("search")) ;
if (sSearch != "" && sSearch != " ") document.forms[1].searchstring.value = sSearch ;

function evalEditUser() {
	var userId = document.forms.argumentForm.user_Id.selectedIndex;
	if( userId == -1 ) {
		alert("<? templates/sv/AdminChangeUser.htm/3001 ?>");
		return false;
	} else {
		return true;
	}
}
//-->
</script>
        <ui:imcms_gui_end_of_page/>
