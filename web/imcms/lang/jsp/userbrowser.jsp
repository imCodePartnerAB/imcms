<%@ page import="com.imcode.imcms.servlet.admin.UserBrowser,
                 com.imcode.imcms.servlet.admin.UserFinder,
                 imcode.server.user.UserDomainObject,
                 imcode.util.HttpSessionUtils,
                 imcode.util.Utility,
                 org.apache.commons.lang.StringEscapeUtils,
                 org.apache.commons.lang.StringUtils,
                 com.imcode.imcms.servlet.superadmin.UserEditorPage,
                 imcode.server.Imcms,
                 imcode.server.user.RoleDomainObject,
                 imcode.util.Html,
                 java.util.Arrays"%>
<%@page contentType="text/html; charset=UTF-8"%><%@taglib prefix="vel" uri="imcmsvelocity"%>
<%
    UserFinder userFinder = (UserFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
    UserBrowser.UserBrowserPage userBrowserPage = (UserBrowser.UserBrowserPage)request.getAttribute( UserBrowser.REQUEST_ATTRIBUTE__FORM_DATA ) ;
    RoleDomainObject[] allRoles = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getAllRolesExceptUsersRole();
    Utility.setDefaultHtmlContentType(response);
%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/AdminManager_adminTask_element.htm/2 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body onLoad="focusField(1,'<%= UserBrowser.REQUEST_PARAMETER__SEARCH_STRING %>');">
	#gui_outer_start()
	#gui_head( "<? templates/sv/AdminManager_adminTask_element.htm/2 ?>" )

<form name="argumentForm" action="UserBrowser" method="GET" target="_top">
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><input type="submit"  class="imcmsFormBtn" name="<%= UserBrowser.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/back ?>"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>" class="imcmsFormBtn" onClick="openHelpW('UserAdmin')"></td>
</tr>
</table>
</form>
#gui_mid()

<form name="argumentForm" action="UserBrowser" method="GET" target="_top">
<input type="hidden" name="<%= UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE %>"
       value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest( request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE ) %>">
<table border="0" cellspacing="0" cellpadding="0" width="600" align="center">
    <tr>
        <td colspan="2">#gui_heading( "<%= userFinder.getHeadline().toLocalizedString(request) %>" )</td>
    </tr>
    <tr>
        <td width="30%" class="imcmsAdmText"><? templates/sv/AdminChangeUser.htm/10 ?></td>
        <td width="70%">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="text" id="<%= UserBrowser.REQUEST_PARAMETER__SEARCH_STRING %>" name="<%= UserBrowser.REQUEST_PARAMETER__SEARCH_STRING %>" size="20" maxlength="20" value="<%= StringEscapeUtils.escapeHtml(userBrowserPage.getSearchString()) %>"></td>
            <td class="imcmsAdmDim">&nbsp; <? templates/sv/AdminChangeUser.htm/1001 ?></td>
        </tr>
        </table></td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/AdminChangeUser.htm/16 ?> </td>
        <td>
            <select id="<%= UserBrowser.REQUEST_PARAMETER__ROLE_ID %>" name="<%= UserBrowser.REQUEST_PARAMETER__ROLE_ID %>" size="5" multiple >
            <%= Html.createOptionList(Arrays.asList(allRoles), userBrowserPage.getSelectedRoles(), new UserEditorPage.RoleToStringPairTransformer()) %>
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
            <td><input type="submit" class="imcmsFormBtnSmall" id="<%= UserBrowser.REQUEST_PARAMETER__SHOW_USERS_BUTTON %>" name="<%= UserBrowser.REQUEST_PARAMETER__SHOW_USERS_BUTTON %>" value="<? templates/sv/AdminChangeUser.htm/2004 ?>"></td>
        </tr>
        </table>
    </td>
    </tr>
</table>
</form>
<form name="argumentForm" action="UserBrowser" method="GET" target="_top">
<input type="hidden" name="<%= UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE %>"
       value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest( request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE ) %>">
<table border="0" cellspacing="0" cellpadding="0" width="600" align="center">
    <tr>
        <td colspan="2">#gui_hr( "cccccc" )</td>
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
            <select id="<%= UserBrowser.REQUEST_PARAMETER__USER_ID %>" name="<%= UserBrowser.REQUEST_PARAMETER__USER_ID %>" size="15" style="width: 100%;">
                <%

                    for ( int i = 0; i < users.length; i++ ) {
                    UserDomainObject user = users[i];
                    %><option value="<%= user.getId() %>"><%= user.getLastName() %>, <%= user.getFirstName() %> [<%= user.getLoginName() %>] <%= StringUtils.isBlank(user.getTitle()) ? "" : ", "+user.getTitle() %> <%= StringUtils.isBlank(user.getCompany()) ? "" : ", "+user.getCompany() %> <%= StringUtils.isBlank(user.getEmailAddress()) ? "" : "&lt;"+user.getEmailAddress()+"&gt;" %></option><%
                } %>
            </select></td>
            <td width="20%" align="right">
            <input type="submit" class="imcmsFormBtnSmall"
                id="<%= UserBrowser.REQUEST_PARAMETER__SELECT_USER_BUTTON %>"
                name="<%= UserBrowser.REQUEST_PARAMETER__SELECT_USER_BUTTON %>"
                value="<%= userFinder.getSelectButtonText().toLocalizedString(request) %>"
                style="width:10em">
            <%
                UserDomainObject user = Utility.getLoggedOnUser(request);
                if (userFinder.isUsersAddable() && (user.isSuperAdmin() || user.isUserAdminAndCanEditAtLeastOneRole())) { %>
                    <div><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="3"></div>
                    <input type="submit" class="imcmsFormBtnSmall"
                        name="<%= UserBrowser.REQUEST_PARAMETER__ADD_USER %>"
                        value="<? templates/sv/AdminChangeUser.htm/2005 ?>" style="width:10em">
            <% } %>
            </td>
        </tr>
        </table></td>
    </tr>
</table>
</form>

<script type="text/javascript">
jQ(document).ready(function($) {
	<%--
	When somethings selected in a selectBox and you click [Enter], then the default button is clicked.
	--%>
	$('#<%= UserBrowser.REQUEST_PARAMETER__ROLE_ID %>').keypress(function (e) {
		if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
			$('#<%= UserBrowser.REQUEST_PARAMETER__SHOW_USERS_BUTTON %>').click() ;
			return false ;
		} else {
			return true ;
		}
	}) ;
	
	$('#<%= UserBrowser.REQUEST_PARAMETER__USER_ID %>').keypress(function (e) {
		if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
			$('#<%= UserBrowser.REQUEST_PARAMETER__SELECT_USER_BUTTON %>').click() ;
			return false ;
		} else {
			return true ;
		}
	}) ;
	
}) ;

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
</script>
#gui_end_of_page()
</vel:velocity>