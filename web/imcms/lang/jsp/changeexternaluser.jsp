<%@ page import="java.util.*,
                 javax.servlet.http.HttpServletResponse,
                 javax.servlet.ServletException,
                 java.io.IOException,
                 imcode.server.WebAppGlobalConstants,
                 com.imcode.imcms.api.*,
                 imcode.util.Utility"%>
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

private static void updateUserRoles( HttpServletRequest request, UserService userMapper, User user ) throws NoPermissionException {
	String[] roleNames = request.getParameterValues(FORM_SELECT_ROLES) ;
	userMapper.setUserRoles(user,roleNames) ;
}


%><%

DefaultContentManagementSystem  imcms = (DefaultContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
UserService  userMapper = imcms.getUserService();

String userLoginName = request.getParameter( WebAppGlobalConstants.USER_LOGIN_NAME_PARAMETER_NAME );
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

</head>
<body bgcolor="#ffffff">

<form method="POST" action="<%=request.getContextPath() %>/imcms/<%= Utility.getLoggedOnUser( request ).getLanguageIso639_2() %>/jsp/changeexternaluser.jsp">
<table width="550" border="0" cellspacing="0" bgcolor="#bababa">
<tr bgcolor="#333366">
	<td width="5%">&nbsp;</td>
	<td colspan="3" align="center"><font face="Verdana, Arial, Helvetica, sans-serif" color="#ffffff" size="2"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/2 ?></font></td>
	<td width="5%">&nbsp;</td>
</tr>
<tr>
	<td colspan="5">&nbsp;</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="3"><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/3 ?></font></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td colspan="5">&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td width="23%"><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/4 ?></font></td>
	<td width="2%">&nbsp;</td>
	<td width="65%" nowrap><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getLoginName()%></font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/5 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"> <%=user.getFirstName()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/6 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"> <%=user.getLastName()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/7 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getTitle()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/8 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCompany()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/9 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getAddress()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/10 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getZip()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/11 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCity()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/12 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif" ><%=user.getWorkPhone()%></font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/13 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getMobilePhone()%></font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/14 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getHomePhone()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td colspan="5">&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/15 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCountyCouncil()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/16 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCountry()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/17 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getEmailAddress()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="3"><hr></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/18 ?></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.isActive()?"Ja":"Nej"%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td nowrap><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/19 ?></font></td>
	<td>&nbsp;</td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr valign="top">
		<td>
		<font size="2" face="Verdana, Arial, Helvetica, sans-serif">
		<select name="<%= FORM_SELECT_ROLES %>" size="5" multiple><%

		Role[] allRoles = userMapper.getAllRoles();
		Set allRolesSet = new TreeSet( Arrays.asList(allRoles) );

		Role[] userRoles = user.getRoles();
		Set userRolesSet = new HashSet( Arrays.asList( userRoles ));

		for( Iterator iterator = allRolesSet.iterator(); iterator.hasNext(); ) {
			Role role = (Role)iterator.next();
			%><option value="<%= role.getName() %>"<%= (userRolesSet.contains(role) ? " selected" : "") %>><%= role.getName() %></option><%
		} %>
		</select></font></td>

		<td>&nbsp;</td>

		<td><font size="1" face="Verdana, Arial, Helvetica, sans-serif"><? install/htdocs/sv/adminuser/changeexternaluser.jsp/21 ?></font></td>
	</tr>
	</table></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="3"><hr></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="3">
	<input type="hidden" name="<%=WebAppGlobalConstants.USER_LOGIN_NAME_PARAMETER_NAME%>" value="<%=userLoginName%>">
	<input type="submit" name="<%= ACTION_SAVE_USER %>" value="<? install/htdocs/sv/adminuser/changeexternaluser.jsp/2001 ?>">
	<input type="submit" name="<%= ACTION_CANCEL %>" value="<? install/htdocs/sv/adminuser/changeexternaluser.jsp/2002 ?>"></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td colspan="5">&nbsp;</td>
</tr>
</table>
</form>

</body>
</html>
