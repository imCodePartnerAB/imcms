<%@ page

	import="org.apache.commons.lang.StringUtils"

	pageEncoding="UTF-8"

%><%

String host = StringUtils.defaultString(request.getHeader("Host")) ;

String heading = "Info" ;
String text = (!"".equals(host)) ? "<b>Info!</b> The site " + host + request.getContextPath() + " is temporarily down for maintenance!" :
              "<b>Info!</b> The site is temporarily down for maintenance!" ;

String imcms_url = "http://doc.imcms.net/4.0.0/" ;

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

	<title><%= heading %></title>
	<link rel="stylesheet" type="text/css" href="<%= imcms_url %>/imcms/css/imcms_admin.css.jsp">

</head>
<body>


<table border="0" cellspacing="0" cellpadding="0" class="imcmsAdmTable" align="center" style="margin: 0 auto;">
<tr>
	<td class="imcmsAdmTable">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="imcmsAdmBgHead" colspan="6"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="20" alt="" /></td>
	</tr>
	<tr class="imcmsAdmBgHead">
		<td colspan="2"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td nowrap="nowrap"><span class="imcmsAdmHeadingTop"><%= heading %></span></td>
		<td align="right"><a href="http://www.imcms.net/" target="_blank"><img src="<%= imcms_url %>/imcms/eng/images/admin/logo_imcms_admin.gif" width="100" height="20" alt="www.imcms.net" border="0" /></a></td>
		<td colspan="2"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
	</tr>
	<tr>
		<td colspan="6" class="imcmsAdmBgHead"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="20" alt="" /></td>
	</tr>
	<tr class="imcmsAdmBgHead">
		<td colspan="2"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td colspan="2"><input type="button" class="imcmsFormBtn" value="Try again" onclick="document.location='<%= request.getContextPath() %>/'; return false" /></td>
		<td colspan="2"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
	</tr>
	<tr>
		<td class="imcmsAdmBgHead" colspan="6"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="20" alt="" /></td>
	</tr>
	<tr>
		<td height="10" class="imcmsAdmBorder"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td class="imcmsAdmBgCont" colspan="4"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td class="imcmsAdmBorder"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
	</tr>
	<tr class="imcmsAdmBgCont">
		<td class="imcmsAdmBorder"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td colspan="2">


		<div class="imcmsAdmText" style="width:350px; padding: 10px 0; font-size:14px;">
			<%= text %>
		</div>


		</td>
		<td><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td class="imcmsAdmBorder"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
	</tr>
	<tr>
		<td height="10" class="imcmsAdmBorder"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td colspan="4" class="imcmsAdmBgCont"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td class="imcmsAdmBorder"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
	</tr>
	<tr class="imcmsAdmBgCont">
		<td><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="24" height="1" alt="" /></td>
		<td colspan="2"><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
		<td><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="24" height="1" alt="" /></td>
		<td><img src="<%= imcms_url %>/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /></td>
	</tr>
	</table></td>
	<td align="right" valign="top" style="background: transparent url(<%= imcms_url + "/imcms/eng/images/admin/imcms_admin_shadow_right.gif" %>) top right repeat-y;">
	<img src="<%= imcms_url %>/imcms/eng/images/admin/imcms_admin_shadow_right_top.gif" width="12" height="12" alt="" border="0" /></td>
</tr>
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td style="background: transparent url(<%= imcms_url + "/imcms/eng/images/admin/imcms_admin_shadow_bottom.gif" %>) top left repeat-x;">
		<img src="<%= imcms_url %>/imcms/eng/images/admin/imcms_admin_shadow_bottom_left.gif" width="12" height="12" alt="" border="0" /></td>
		<td style="background: transparent url(<%= imcms_url + "/imcms/eng/images/admin/imcms_admin_shadow_bottom.gif" %>) top left repeat-x;" align="right">
		<img src="<%= imcms_url %>/imcms/eng/images/admin/imcms_admin_shadow_bottom_right.gif" width="12" height="12" alt="" border="0" /></td>
	</tr>
	</table></td>
</tr>
</table>


</body>
</html>