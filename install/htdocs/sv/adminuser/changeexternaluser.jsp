<%@ page import="java.util.*,
                 javax.servlet.http.HttpServletResponse,
                 javax.servlet.ServletException,
                 java.io.IOException,
                 com.imcode.imcms.*,
                 imcode.server.WebAppGlobalConstants,
                 com.imcode.imcms.api.*"%>
<%!

private final static String ACTION_SAVE_USER       = "SAVE_USER" ;
private final static String ACTION_CANCEL          = "CANCEL" ;

private final static String FORM_SELECT_ROLES      = "roles" ;

private final static String SERVLET_ADMIN_USER_URL = "/servlet/AdminUser" ;
private final static String MY_LOCATION_URL        = "/adminuser/changeexternaluser.jsp" ;

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

ContentManagementSystem  imcms = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
UserService  userMapper = imcms.getUserService();

String userLoginName = request.getParameter( WebAppGlobalConstants.USER_LOGIN_NAME );
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
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>Redigera extern användare</title>

</head>
<body bgcolor="#ffffff">

<form method="POST" action="<%=request.getContextPath() + MY_LOCATION_URL%>">
<table width="550" border="0" cellspacing="0" bgcolor="#bababa">
<tr bgcolor="#333366">
	<td width="5%">&nbsp;</td>
	<td colspan="3" align="center"><font face="Verdana, Arial, Helvetica, sans-serif" color="#ffffff" size="2"><b>Redigera extern användare</b></font></td>
	<td width="5%">&nbsp;</td>
</tr>
<tr>
	<td colspan="5">&nbsp;</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="3"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Informationen
	som ej går att ändra är hämtad från ett system
	utanför imCMS. Informationen ändras i det systemet.</font></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td colspan="5">&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td width="23%"><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Användarnamn:</b></font></td>
	<td width="2%">&nbsp;</td>
	<td width="65%" nowrap><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getLoginName()%></font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Förnamn:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"> <%=user.getFirstName()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Efternamn:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"> <%=user.getLastName()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Titel:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getTitle()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Arbetsplats:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCompany()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Gatuadress:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getAddress()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Postnummer:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getZip()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Postort:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCity()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Telefon, arbetet:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif" ><%=user.getWorkPhone()%></font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Telefon, mobil:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getMobilePhone()%></font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Telefon, hem:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getHomePhone()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td colspan="5">&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Län:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCountyCouncil()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Land:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCountry()%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Email:</b></font></td>
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
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Aktiv:</b></font></td>
	<td>&nbsp;</td>
	<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.isActive()?"Ja":"Nej"%>&nbsp;</font></td>
	<td>&nbsp;</td>
</tr>
<tr valign="top">
	<td>&nbsp;</td>
	<td nowrap><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b>Användarens Roller:</b></font></td>
	<td>&nbsp;</td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr valign="top">
		<td>
		<font size="2" face="Verdana, Arial, Helvetica, sans-serif">
		<select name="<%= FORM_SELECT_ROLES %>" size="5" multiple><%
		
		String[] allRoleNames = userMapper.getAllRolesNames();
		Set setOfAllRoleNames = new TreeSet( Arrays.asList(allRoleNames) );
		
		String[] userRoleNames = userMapper.getRoleNames( user );
		Set setOfUserRoleNames = new HashSet( Arrays.asList( userRoleNames ));
		
		for( Iterator iterator = setOfAllRoleNames.iterator(); iterator.hasNext(); ) {
			String roleName = (String)iterator.next();
			%>
			<option value="<%= roleName %>"<%= (setOfUserRoleNames.contains(roleName) ? " selected" : "") %>><%= roleName %></option><%
		} %>
		</select></font></td>
		
		<td>&nbsp;</td>
		
		<td><font size="1" face="Verdana, Arial, Helvetica, sans-serif">(Varning!
		Endast de roller som hanteras internt i Imcms blir varaktigt lagrade
		på användaren. Roller som hanteras av det externa systemet
		uppdateras vid inloggning.)</font></td>
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
	<input type="hidden" name="<%=WebAppGlobalConstants.USER_LOGIN_NAME%>" value="<%=userLoginName%>">
	<input type="submit" name="<%= ACTION_SAVE_USER %>" value="Spara">
	<input type="submit" name="<%= ACTION_CANCEL %>" value="Avbryt"></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td colspan="5">&nbsp;</td>
</tr>
</table>
</form>

</body>
</html>
