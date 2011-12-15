<%@ page import="com.imcode.imcms.flow.OkCancelPage"%><%@ page  import="com.imcode.imcms.flow.Page"%>
<%@ page import="com.imcode.imcms.servlet.superadmin.UserEditorPage"%><%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage"%>
<%@ page import="imcode.server.user.UserDomainObject"%><%@  page import="imcode.util.DateConstants"%>
<%@ page import="imcode.util.Utility"%><%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="java.text.SimpleDateFormat, imcode.server.Imcms"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    UserEditorPage userEditorPage = (UserEditorPage) Page.fromRequest(request);
    UserDomainObject editedUser = userEditorPage.getEditedUser() ;
		try {
			if (editedUser != null && (editedUser.getLanguageIso639_2() == null || editedUser.getLanguageIso639_2().equals(""))) {
				String defaultLanguage = Imcms.getServices().getLanguageMapper().getDefaultLanguage();
				editedUser.setLanguageIso639_2(defaultLanguage);
			}
		} catch(Exception e) {}
    UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
    LocalizedMessage errorMessage = userEditorPage.getErrorMessage() ;
%><%@taglib prefix="vel" uri="imcmsvelocity"%><vel:velocity><html>
<head>
<title><? templates/sv/AdminUserResp.htm/1 ?></title>
<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
<script type="text/javascript">
var $pw1, $pw2 ;

jQ(document).ready(function($) {
	$pw1 = $('input[name=password1]') ;
	$pw2 = $('input[name=password2]') ;
	window.setTimeout(function() {
		$pw1.attr('autocomplete', 'off').focus().val('') ;<%-- FF has a bug. Ignores autocomplete=off sometimes --%>
	}, 100) ;
}) ;

function evalPrepareAdd() {
	if ('' == jQ('input[name=login_name]').val()) {
		alert("<? templates/sv/AdminUserResp.htm/2/1 ?>") ;
		return false ;
	}
	var $pw1 = jQ('input[name=password1]') ;
	var $pw2 = jQ('input[name=password2]') ;
	if ($pw1.val() != $pw2.val()){
		$pw1.val('') ;
		$pw2.val('') ;
		$pw1.focus() ;
		alert("<? templates/sv/AdminUserResp.htm/2/2 ?>") ;
		return false ;
	}
	return true ;
}

function activateUseradmin_roles(){
	if ( document.forms[0].<%= UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS %> ){
		var list = document.forms[0].<%= UserEditorPage.REQUEST_PARAMETER__ROLE_IDS %>;
		document.forms[0].<%= UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS %>.disabled = true;
		for ( i = 0 ; i < list.length ; i++ ){
			if ( list.options[i].text == "Useradmin"  && list.options[i].selected ){
				document.forms[0].<%= UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS %>.disabled = false;
			}
		}
	}
}
</script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(0,'<%= UserEditorPage.REQUEST_PARAMETER__LOGIN_NAME %>'); activateUseradmin_roles(); return true">


#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<form method="post" action="$contextPath/servlet/PageDispatcher">
<%= Page.htmlHidden(request) %>
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<? templates/sv/AdminUserResp.htm/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? templates/sv/AdminUserResp.htm/2002 ?>" title="<? templates/sv/AdminUserResp.htm/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('UserEdit')"></td>
</tr>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="0" width="660" align="center">
<tr>
	<td colspan="2">#gui_heading( "<? templates/sv/AdminUserResp.htm/5/1 ?>" )</td>
</tr>
<tr>
	<td colspan="2" class="imcmsAdmText">
	<? templates/sv/AdminUserResp.htm/6 ?></td>
</tr>
<tr>
	<td colspan="2">#gui_hr( "cccccc" )</td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/8 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__LOGIN_NAME %>" size="25" maxlength="50" value="<%= StringEscapeUtils.escapeHtml(editedUser.getLoginName()) %>"></td>
</tr>
<tr>
	<td class="imcmsAdmText" nowrap><? templates/sv/AdminUserResp.htm/10 ?> <span class="imcmsAdmDim"><? templates/sv/AdminUserResp.htm/11 ?></span> &nbsp;</td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="password" name="<%= UserEditorPage.REQUEST_PARAMETER__PASSWORD1 %>" size="16" maxlength="15" value=""></td>
		<td class="imcmsAdmText" nowrap>&nbsp; <? templates/sv/AdminUserResp.htm/1001 ?> &nbsp;</td>
		<td><input type="password" name="<%= UserEditorPage.REQUEST_PARAMETER__PASSWORD2 %>" size="16" maxlength="15" value=""></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/14 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__FIRST_NAME %>" size="25" maxlength="25" value="<%= StringEscapeUtils.escapeHtml(editedUser.getFirstName()) %>"></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/16 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__LAST_NAME %>" size="25" maxlength="30" value="<%= StringEscapeUtils.escapeHtml(editedUser.getLastName()) %>"></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/18 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__TITLE %>" size="25" maxlength="30" value="<%= StringEscapeUtils.escapeHtml(editedUser.getTitle()) %>"></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/20 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__COMPANY %>" size="25" maxlength="30" value="<%= StringEscapeUtils.escapeHtml(editedUser.getCompany())%>"></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/22 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__ADDRESS %>" size="25" maxlength="30" value="<%= StringEscapeUtils.escapeHtml(editedUser.getAddress())%>"></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/24 ?></td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__ZIP %>" size="7" maxlength="30" value="<%= StringEscapeUtils.escapeHtml(editedUser.getZip())%>"></td>
		<td>&nbsp;</td>
		<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__CITY %>" size="25" maxlength="30" value="<%= StringEscapeUtils.escapeHtml(editedUser.getCity())%>"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/27 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__DISTRICT %>" size="25" maxlength="30" value="<%= StringEscapeUtils.escapeHtml(editedUser.getProvince())%>"></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/29 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__COUNTRY %>" size="25" maxlength="30" value="<%= StringEscapeUtils.escapeHtml(editedUser.getCountry())%>"></td>
</tr>
<tr>
    <td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/30 ?></td>
    <td><select name="<%= UserEditorPage.REQUEST_PARAMETER__LANGUAGE %>"><%= userEditorPage.createLanguagesHtmlOptionList( loggedOnUser, editedUser ) %></select></td>
</tr>
<tr>
	<td colspan="2">#gui_hr( "cccccc" )</td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/32 ?></td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
	  <select name="<%= UserEditorPage.REQUEST_PARAMETER__PHONE_NUMBER_TYPE_ID %>" size="1" >
			<%= userEditorPage.createPhoneTypesHtmlOptionList(loggedOnUser,userEditorPage.getCurrentPhoneNumber().getType()) %>
	  </select></td>
		<td>&nbsp;</td>
		<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__EDITED_PHONE_NUMBER %>" size="16" maxlength="25" value="<%= userEditorPage.getCurrentPhoneNumber() %>"></td>
		<td>&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtnSmall" value="<? templates/sv/AdminUserResp.htm/2004 ?>" name="<%= UserEditorPage.REQUEST_PARAMETER__ADD_PHONE_NUMBER %>"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td class="imcmsAdmText">&nbsp;</td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
		<select size="1" name="<%= UserEditorPage.REQUEST_PARAMETER__SELECTED_PHONE_NUMBER %>">
		    <%= userEditorPage.getUserPhoneNumbersHtmlOptionList(request) %>
		</select></td>
		<td>&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtnSmall" name="<%= UserEditorPage.REQUEST_PARAMETER__EDIT_PHONE_NUMBER %>" value="<? templates/sv/AdminUserResp.htm/2005 ?>"></td>
		<td>&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtnSmall" name="<%= UserEditorPage.REQUEST_PARAMETER__REMOVE_PHONE_NUMBER %>" value="<? templates/sv/AdminUserResp.htm/2006 ?>"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp.htm/36 ?></td>
	<td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__EMAIL %>" size="50" maxlength="50" value="<%= StringEscapeUtils.escapeHtml(editedUser.getEmailAddress())%>"></td>
</tr>
<tr>
	<td colspan="2">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/AdminUserResp_superadmin_part.htm/2 ?></td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="checkbox" name="active" value="1" <% if (editedUser.isActive()) { %>checked<% } %>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;
            <% if (null != editedUser.getCreateDate()) { %>
                &nbsp; <? templates/sv/AdminUserResp_superadmin_part.htm/12 ?> &nbsp; <%= new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(editedUser.getCreateDate()) %>
            <% } %>
        </td>
	</tr>
	</table></td>
</tr>
<% if (loggedOnUser.canEditRolesFor(userEditorPage.getUneditedUser())) { %>
<tr>
	<td colspan="2">&nbsp;<br>#gui_heading( "<? templates/sv/AdminUserResp_superadmin_part.htm/3/1 ?>" )</td>
</tr>
<tr valign="top">
	<td class="imcmsAdmText" nowrap>
	<? templates/sv/AdminUserResp_superadmin_part.htm/1001 ?> &nbsp;</td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr valign="top">
		<td>
		<select name="<%= UserEditorPage.REQUEST_PARAMETER__ROLE_IDS %>" size="5" multiple onchange="activateUseradmin_roles(); return true;">
		    <%= userEditorPage.createRolesHtmlOptionList(request) %>
		</select></td>
        <% if (loggedOnUser.isSuperAdmin()) { %>
            <td>&nbsp;</td>
            <td class="imcmsAdmText" nowrap><? templates/sv/AdminUserResp_superadmin_part.htm/8 ?></td>
            <td>&nbsp;</td>
            <td>
            <select name="<%= UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS %>" size="5" multiple>
                <%= userEditorPage.createUserAdminRolesHtmlOptionList() %>
            </select></td>
        <% } %>
    </tr>
	<tr valign="top">
		<td class="imcmsAdmDim"><? templates/sv/AdminUserResp_superadmin_part.htm/10 ?></td>
        <% if (loggedOnUser.isSuperAdmin()) { %>
            <td colspan="3">&nbsp;</td>
            <td class="imcmsAdmDim"><? templates/sv/AdminUserResp_superadmin_part.htm/11 ?></td>
        <% } %>
    </tr>
	</table></td>
</tr>
<% } %>
<tr>
	<td colspan="2">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="0" width="656">
	<tr>
		<td class="imcmsAdmComment"><? templates/sv/AdminUserResp.htm/40 ?></td>
		<td align="right">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
            <%
                if (null != errorMessage) {
                    %><td><span class="error"><%= errorMessage.toLocalizedString(request) %></span></td><td>&nbsp;</td><%
                }
            %>
			<td><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__OK %>" value="<? templates/sv/AdminUserResp.htm/2007 ?>" onClick="if( !evalPrepareAdd() ) return false;"></td>
			<td>&nbsp;</td>
			<td><input type="submit" class="imcmsFormBtn" value="<? templates/sv/AdminUserResp.htm/2008 ?>"></td>
			<td>&nbsp;</td>
			<td><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<? templates/sv/AdminUserResp.htm/2009 ?>"></td>
		</tr>
		</table></td>
	</tr>
	</table></td>
</tr>
</table>
</form>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
