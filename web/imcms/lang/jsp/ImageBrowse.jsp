<%@ page import="com.imcode.imcms.servlet.admin.ImageBrowse,
                 com.imcode.imcms.servlet.admin.ImageBrowse,
                 org.apache.commons.lang.StringEscapeUtils"%>
<%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<vel:velocity>
<html>
<head>


<title><? install/htdocs/sv/jsp/ImageBrowse.html/1 ?></title>

<link rel="stylesheet" href="$contextPath/imcms/css/imcms_admin.css" type="text/css">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(0,'text')">
#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")

<%
    ImageBrowse.FormData imageBrowseFormData;
    imageBrowseFormData = (ImageBrowse.FormData)request.getAttribute(ImageBrowse.REQUEST_ATTRIBUTE__IMAGE_BROWSE_FORM_DATA);
%>
<table border="0" cellspacing="0" cellpadding="0">
<form action="ImageBrowse">
<% if (null != imageBrowseFormData.getCaller() ) { %><input type="HIDDEN" name="caller" value="<%=imageBrowseFormData.getCaller()%>"><% } %>
<% if (null != imageBrowseFormData.getMetaId() ) { %><input type="HIDDEN" name="meta_id" value="<%=imageBrowseFormData.getMetaId()%>"><% } %>
<% if (null != imageBrowseFormData.getImageNumber() ) { %><input type="HIDDEN" name="img_no" value="<%=imageBrowseFormData.getImageNumber()%>"><% } %>
<% if (null != imageBrowseFormData.getLabel() ) { %><input type="HIDDEN" name="label" value="<%=imageBrowseFormData.getLabel()%>"><% } %>
<tr>
	<td><input type="Submit" class="imcmsFormBtn" name="<%= ImageBrowse.PARAMETER_BUTTON__CANCEL %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2002 ?>" title="<? install/htdocs/sv/jsp/ImageBrowse.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(44)"></td>
</tr>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<% if (null != imageBrowseFormData.getImageNumber()) { %>
<tr>
	<td colspan="3">
        #gui_heading( "<? install/htdocs/sv/jsp/ImageBrowse.html/4/1 ?> <%=imageBrowseFormData.getImageNumber()%> <? install/htdocs/sv/jsp/ImageBrowse.html/4/2 ?> <%=imageBrowseFormData.getMetaId()%>" )
    </td>
</tr>
<% } %>
<tr valign="top">
	<td width="45%" align="right">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/ImageBrowse.html/5 ?></td>
	</tr>
	<tr>
		<td>
		<select name="dirlist" size="15" onDblClick="document.forms[0].change.click();" style="width:270">
			<%=imageBrowseFormData.getFolders()%>
		</select></td>
	</tr>
	</table></td>
	<td width="10%" align="center">
	&nbsp;<br><input type="submit" class="imcmsFormBtnSmall" name="change" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2004 ?>"></td>
	<td width="45%">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="imcmsAdmText" align="right"><? install/htdocs/sv/jsp/ImageBrowse.html/7 ?></td>
	</tr>
	<tr>
		<td>
		<select name="imglist" size="15" onDblClick="document.forms[0].<%= StringEscapeUtils.escapeJavaScript( ImageBrowse.PARAMETER_BUTTON__PREVIEW ) %>.click();" style="width:270">
		<%=imageBrowseFormData.getOptions()%>
		</select></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="3" align="right">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="3" class="imcmsAdmText">
        <? install/htdocs/sv/jsp/ImageBrowse.html/9 ?> <%=imageBrowseFormData.getMaxNumber()%>&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
	</tr>
	<tr>
		<td align="right"><%=imageBrowseFormData.getPreviousButton()%></td>
		<td>&nbsp;</td>
		<td><%=imageBrowseFormData.getNextButton()%></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="3">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="3" align="right">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="submit" class="imcmsFormBtn" name="<%= ImageBrowse.PARAMETER_BUTTON__OK %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2005 ?>"></td>
		<td>&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtn" name="<%= ImageBrowse.PARAMETER_BUTTON__PREVIEW %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2006 ?>"></td>
		<td>&nbsp;</td>
    	<td><input type="Submit" class="imcmsFormBtn" name="<%= ImageBrowse.PARAMETER_BUTTON__CANCEL %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2007 ?>"></td>
	</tr>
	</table></td>
</tr>
</table>
</form>
#gui_bottom()
#gui_outer_end()
<div align="center" id="previewDiv"><%=imageBrowseFormData.getImagePreview()%></div>

<script language="JavaScript">
<!--
if (hasGetElementById) {
	if (document.getElementById("previewDiv")) {
		if (!/\./.test(document.getElementById("previewDiv").innerHTML.toString())) {
			document.getElementById("previewDiv").style.display = "none" ;
		}
	}
}
//-->
</script>

</body>
</html>
</vel:velocity>
