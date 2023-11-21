<%@ page import="com.imcode.imcms.api.ContentManagementSystem,
                 com.imcode.imcms.api.Role,
                 com.imcode.imcms.api.User,
                 com.imcode.imcms.api.UserService,
                 com.imcode.imcms.api.exception.NoPermissionException,
                 com.imcode.imcms.api.exception.SaveException"
%>
<%@ page import="com.imcode.imcms.servlet.superadmin.AdminUser" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%!

private final static String ACTION_SAVE_USER       = "SAVE_USER" ;
private final static String ACTION_CANCEL          = "CANCEL" ;

private final static String FORM_SELECT_ROLES      = "roles" ;

private final static String SERVLET_ADMIN_USER_URL = "/servlet/AdminUser" ;

private static boolean buttonPressed (HttpServletRequest request, String buttonName) {
	boolean buttonPressed = null != request.getParameter(buttonName) ;
	return buttonPressed ;
}

private static void redirectToAdminUser (HttpServletRequest request, HttpServletResponse response) throws IOException {
	String url = request.getContextPath() + SERVLET_ADMIN_USER_URL;
	response.sendRedirect( url );
}

private static void updateUserRoles( HttpServletRequest request, UserService userService, User user ) throws NoPermissionException {
	user.setRoles(new Role[] {});
	
	String[] roleNames = request.getParameterValues(FORM_SELECT_ROLES) ;

    for(int i = 0; i < roleNames.length; i++) {
        String roleName = roleNames[i];
        Role role = userService.getRole(roleName) ;
        if (null != role) {
            user.addRole(role);
        }
    }
    try {
        userService.saveUser(user);
    } catch(SaveException se) {
    }
}


%><%

ContentManagementSystem  imcms = ContentManagementSystem.fromRequest(request);
UserService  userMapper = imcms.getUserService();

String userLoginName = request.getParameter( AdminUser.USER_LOGIN_NAME_PARAMETER_NAME );
User user = userMapper.getUser( userLoginName );

if ( buttonPressed(request, ACTION_CANCEL) ) {
	redirectToAdminUser(request,response) ;
	return ;
} else if ( buttonPressed(request, ACTION_SAVE_USER) ) {
	updateUserRoles( request, userMapper, user );
	redirectToAdminUser(request,response) ;
	return ;
}

%>
<html>
<head>
<title><? install/htdocs/sv/adminuser/changeexternaluser.jsp/1 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

</head>
<body>

    <ui:imcms_gui_outer_start/>
    <c:set var="heading">
        <fmt:message key="install/htdocs/sv/adminuser/changeexternaluser.jsp/1"/>
    </c:set>
    <ui:imcms_gui_head heading="${heading}"/>

    <form method="POST" action="${contextPath}/imcms/${language}/jsp/changeexternaluser.jsp">
<table border="0" cellspacing="0" cellpadding="0">
<input type="hidden" name="<%= AdminUser.USER_LOGIN_NAME_PARAMETER_NAME %>" value="<%= userLoginName %>">
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="<%= ACTION_CANCEL %>" value="<? global/back ?>"></td>
</tr>
</table>
    <ui:imcms_gui_mid/>
<table border="0" cellspacing="0" cellpadding="2" width="400">
<tr>
    <td colspan="2">
        <c:set var="heading">
            <fmt:message key="install/htdocs/sv/adminuser/changeexternaluser.jsp/2"/>
        </c:set>
        <ui:imcms_gui_heading heading="${heading}"/>
    </td>
</tr>
<tr>
	<td colspan="2"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/3 ?><br>&nbsp;</td>
</tr>
<tr>
	<td width="30%"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/4 ?></td>
	<td width="70%" nowrap><%=user.getLoginName()%></td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/5 ?></td>
	<td><%= user.getFirstName() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/6 ?></td>
	<td><%= user.getLastName() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/7 ?></td>
	<td><%= user.getTitle() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/8 ?></td>
	<td><%= user.getCompany() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/9 ?></td>
	<td><%= user.getAddress() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/10 ?></td>
	<td><%= user.getZip() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/11 ?></td>
	<td><%= user.getCity() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/12 ?></td>
	<td><%= user.getWorkPhone() %></td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/13 ?></td>
	<td><%= user.getMobilePhone() %></td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/14 ?></td>
	<td><%= user.getHomePhone() %>&nbsp;</td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/15 ?></td>
	<td><%= user.getProvince() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/16 ?></td>
	<td><%= user.getCountry() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/17 ?></td>
	<td><%= user.getEmailAddress() %>&nbsp;</td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/18 ?></td>
	<td><%= user.getRef() %>&nbsp;</td>
</tr>
<tr>
    <td colspan="2"><ui:imcms_gui_hr wantedcolor="cccccc"/></td>
</tr>
<tr>
	<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/18 ?></td>
    <td><img src="${contextPath}/imcms/${language}/images/admin/btn_checked_<%= user.isActive() ? "1" : "0" %>.gif"
             width="13" height="12" alt="">&nbsp;
    </td>
</tr>
<tr valign="top">
	<td nowrap><? install/htdocs/sv/adminuser/changeexternaluser.jsp/19 ?></td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
		<select name="<%= FORM_SELECT_ROLES %>" size="5" multiple><%
		Role[] allRoles = userMapper.getAllRoles();
		Set allRolesSet = new TreeSet( Arrays.asList(allRoles) );
		Role[] userRoles = user.getRoles();
		Set userRolesSet = new HashSet( Arrays.asList( userRoles ));
		for( Iterator iterator = allRolesSet.iterator(); iterator.hasNext(); ) {
			Role role = (Role)iterator.next(); %>
			<option value="<%= role.getName() %>"<%= (userRolesSet.contains(role) ? " selected" : "") %>><%= role.getName() %></option><%
		} %>
		</select></td>

		<td>&nbsp;</td>

		<td><? install/htdocs/sv/adminuser/changeexternaluser.jsp/21 ?></td>
	</tr>
	</table></td>
</tr>
<tr>
    <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
</tr>
<tr>
	<td colspan="2" align="right">
	<input type="submit" class="imcmsFormBtn" name="<%= ACTION_SAVE_USER %>" value="<? global/save ?>">
	<input type="submit" class="imcmsFormBtn" name="<%= ACTION_CANCEL %>" value="<? global/cancel ?>"></td>
</tr>
</table>
</form>
    <ui:imcms_gui_end_of_page/>
