<%@ page import="com.imcode.imcms.servlet.VerifyUser" %>
<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>
<%@ page import="imcode.server.user.UserDomainObject" %>
<%@ page import="imcode.util.Utility" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    UserDomainObject user = Utility.getLoggedOnUser(request);
%><html>
<head>
<title><? templates/login/index.html/1 ?></title>


<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
<script src="<%= request.getContextPath() %>/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'name')">
<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/login/index.html/2"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<table border="0" cellspacing="0" cellpadding="0" width="310">
<form action="">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
        <td><input type="button" class="imcmsFormBtn" style="width:100px" value="<? templates/login/index.html/2001 ?>"
                   onClick="top.location='<%= request.getContextPath() %>/servlet/StartDoc';"></td>
		<td>&nbsp;</td>
        <td><input type="button" class="imcmsFormBtn" style="width:115px" value="<? templates/login/index.html/2002 ?>"
                   onClick="top.location='<%= request.getContextPath() %>/servlet/PasswordReset';"></td>
		<td>&nbsp;</td>
        <td><input type="button" value="<? templates/login/index.html/2003 ?>" title="<? templates/login/index.html/2004 ?>" class="imcmsFormBtn" onClick="openHelpW('LogIn')"></td>
	</tr>
	</table></td>
</tr>
</form>
</table>
<ui:imcms_gui_mid/>
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
    <input type="hidden" name="<%= VerifyUser.REQUEST_PARAMETER__NEXT_META %>" value="<%=StringEscapeUtils.escapeHtml4(next_meta)%>">
    <%}else if( null != next_url ) { %>
    <input type="hidden" name="<%= VerifyUser.REQUEST_PARAMETER__NEXT_URL %>" value="<%=StringEscapeUtils.escapeHtml4(next_url)%>">
	<%}%>
    <tr>
		<td><span class="imcmsAdmText"><? templates/login/index.html/5 ?></span></td>
		<td>&nbsp;</td>
        <td><input type="text" name="<%= VerifyUser.REQUEST_PARAMETER__USERNAME %>" size="15" style="width:180px"></td>
	</tr>
	<tr>
		<td><span class="imcmsAdmText"><? templates/login/index.html/6 ?></span></td>
		<td>&nbsp;</td>
        <td><input type="password" name="<%= VerifyUser.REQUEST_PARAMETER__PASSWORD %>" size="15" style="width:180px">
        </td>
	</tr>
	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
            <td><input class="imcmsFormBtn" type="submit" value="<? templates/login/index.html/2005 ?>"
                       style="width:80px"></td>
			<td>&nbsp;</td>
            <td><input class="imcmsFormBtn" type="submit" name="<%= VerifyUser.REQUEST_PARAMETER__EDIT_USER %>"
                       value="<? templates/login/index.html/2006 ?>" style="width:80px"></td>
		</tr>
		</table></td>
	</tr>
	</form>
	</table></td>
</tr>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>
</body>
</html>
