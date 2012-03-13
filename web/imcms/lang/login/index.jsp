<%@ page import="com.imcode.imcms.servlet.VerifyUser"%><%@ page import="imcode.server.user.UserDomainObject"%><%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage"%>
<%@ page import="imcode.util.Utility"%><%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<%
    UserDomainObject user = Utility.getLoggedOnUser(request);
%><html>
<head>
<title><? templates/login/index.html/1 ?></title>


<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
<script src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'name')">
#gui_outer_start()
#gui_head( "<? templates/login/index.html/2 ?>" )
<table border="0" cellspacing="0" cellpadding="0" width="310">
<form action="">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="button" class="imcmsFormBtn" style="width:100" value="<? templates/login/index.html/2001 ?>" onClick="top.location='<%= request.getContextPath() %>/servlet/StartDoc';"></td>
		<td>&nbsp;</td>
        <td><input type="button" class="imcmsFormBtn" style="width:115" value="<? templates/login/index.html/2002 ?>" onClick="top.location='<%= request.getContextPath() %>/servlet/PasswordReset';"></td>
        <td>&nbsp;</td>
        <td><input type="button" value="<? templates/login/index.html/2003 ?>" title="<? templates/login/index.html/2004 ?>" class="imcmsFormBtn" onClick="openHelpW('LogIn')"></td>
	</tr>
	</table></td>
</tr>
</form>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="310">
<tr>
	<td colspan="2" nowrap><span class="imcmsAdmText">
	<% LocalizedMessage error = (LocalizedMessage) request.getAttribute("error");
        if (null != error) {
            %><p><b><%= error.toLocalizedString(request) %></b></p><%
        }
    %>        
    <? templates/login/index.html/4 ?>
	<img alt="" src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif" width="1" height="5"><? templates/login/index.html/1001 ?></span></td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>
</tr>
<tr>
	<td colspan="2" align="center">
	<table border="0" cellspacing="0" cellpadding="1">
	<form action="<%= request.getContextPath() %>/servlet/VerifyUser" method="post">
    <%
        String next_meta = request.getParameter(VerifyUser.REQUEST_PARAMETER__NEXT_META);
        String next_url = request.getParameter(VerifyUser.REQUEST_PARAMETER__NEXT_URL);
    if( null != next_meta )  { %>
    <input type="hidden" name="<%= VerifyUser.REQUEST_PARAMETER__NEXT_META %>" value="<%=StringEscapeUtils.escapeHtml(next_meta)%>">
    <%}else if( null != next_url ) { %>
    <input type="hidden" name="<%= VerifyUser.REQUEST_PARAMETER__NEXT_URL %>" value="<%=StringEscapeUtils.escapeHtml(next_url)%>">
	<%}%>
    <tr>
		<td><span class="imcmsAdmText"><? templates/login/index.html/5 ?></span></td>
		<td>&nbsp;</td>
		<td><input type="text" name="<%= VerifyUser.REQUEST_PARAMETER__USERNAME %>" size="15" style="width:180"></td>
	</tr>
	<tr>
		<td><span class="imcmsAdmText"><? templates/login/index.html/6 ?></span></td>
		<td>&nbsp;</td>
		<td><input type="password" name="<%= VerifyUser.REQUEST_PARAMETER__PASSWORD %>" size="15" style="width:180"></td>
	</tr>
	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input class="imcmsFormBtn" type="submit" value="<? templates/login/index.html/2005 ?>" style="width:80"></td>
			<td>&nbsp;</td>
			<td><input class="imcmsFormBtn" type="submit" name="<%= VerifyUser.REQUEST_PARAMETER__EDIT_USER %>" value="<? templates/login/index.html/2006 ?>" style="width:80"></td>
		</tr>
		</table></td>
	</tr>
	</form>
	</table></td>
</tr>
</table>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
