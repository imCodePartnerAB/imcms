<%@ page

	import="com.imcode.imcms.servlet.admin.ChangeText,
	        imcode.server.Imcms,
	        org.apache.commons.lang.StringEscapeUtils, imcode.util.Utility, imcode.server.LanguageMapper"

    contentType="text/html; charset=UTF-8"

%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"
%><%
	response.setContentType( "text/html; charset=" + Imcms.DEFAULT_ENCODING );
ChangeText.TextEditPage textEditPage = (ChangeText.TextEditPage) request.getAttribute(ChangeText.TextEditPage.REQUEST_ATTRIBUTE__PAGE);

%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/change_text.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
<script src="<%= request.getContextPath() %>/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
</head>
<body bgcolor="#FFFFFF" style="margin-bottom:0px;">
<script type="text/javascript">
    _editor_url  = "<%=request.getContextPath()%>/imcms/xinha/"  // (preferably absolute) URL (including trailing slash) where Xinha is installed
    _editor_lang = "<%= LanguageMapper.convert639_2to639_1(Utility.getLoggedOnUser(request).getLanguageIso639_2()) %>";      // And the language we need to use in the editor.
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/imcms/xinha/XinhaCore.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/imcms/xinha/plugins/ImcmsIntegration/init.js.jsp"></script>
<form method="POST" action="<%= request.getContextPath() %>/servlet/SaveText">
<input type="hidden" name="meta_id"  value="<%= textEditPage.getDocumentId() %>">
<input type="hidden" name="txt_no"   value="<%= textEditPage.getTextIndex() %>">

#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
		<input type="button" tabindex="10" value="<? global/back ?>" name="cancel" class="imcmsFormBtn"></td>
	</tr>
	</table></td>

	<td align="right">
	<input type="button" tabindex="12" value="<? templates/sv/change_text.html/2004 ?>" title="<? templates/sv/change_text.html/2005 ?>" class="imcmsFormBtn" onClick="openHelpW('EditText')"></td>

</tr>
</table>

#gui_mid()
<div id="theLabel"><%= StringEscapeUtils.escapeHtml( textEditPage.getLabel() ) %></div>

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
</vel:velocity>
<tr>
	<td colspan="2" class="imcmsAdmForm">
	<textarea name="text" tabindex="1" id="text" cols="125" rows="18" style="overflow: auto; width: 100%" wrap="virtual"><%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %></textarea></td>
</tr>
<vel:velocity>
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td align="right">
            <input type="hidden" name="format_type" value="1" />
            <input tabindex="2" type="SUBMIT" class="imcmsFormBtn" name="ok" value="  <? templates/sv/change_text.html/2006 ?>  ">
            <input tabindex="3" type="SUBMIT" class="imcmsFormBtn" name="save" value="  <? templates/sv/change_text.html/save ?>  ">
            <input tabindex="4" type="RESET" class="imcmsFormBtn" value="<? templates/sv/change_text.html/2007 ?>">
            <input tabindex="5" type="SUBMIT" class="imcmsFormBtn" name="cancel" value=" <? templates/sv/change_text.html/2008 ?> ">
        </td>
	</tr>
	</table></td>
</tr>
</table>
#gui_bottom()
#gui_outer_end()
</form>
</body>
</html>
</vel:velocity>
