<%@ page import="com.imcode.imcms.servlet.admin.ImageBrowse,
                 com.imcode.imcms.servlet.admin.ImageBrowse"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<title><? sv/jsp/ImageBrowse.html/1 ?></title>

<link rel="stylesheet" href="@imcmscssurl@/imcms_admin_ns.css" type="text/css">
<script src="@imcmsscripturl@/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(0,'text')">

<script>
imcmsGui("outer_start", null);
imcmsGui("head", null);
</script>

<%
    ImageBrowse.ImageBrowseBean imageBrowseFormBean;
    imageBrowseFormBean = (ImageBrowse.ImageBrowseBean)request.getAttribute("imagebrowsebean");
%>
<table border="0" cellspacing="0" cellpadding="0">
<form name="backForm" method="POST" action="<%=imageBrowseFormBean.getCaller()%>">
<input type="HIDDEN" name="caller" value="<%=imageBrowseFormBean.getCaller()%>">
<input type="HIDDEN" name="meta_id" value="<%=imageBrowseFormBean.getMetaId()%>">
<input type="HIDDEN" name="img_no" value="<%=imageBrowseFormBean.getImageNumber()%>">
<tr>
	<td><input type="Submit" class="imcmsFormBtn" name="avbryt" value="<? sv/jsp/ImageBrowse.html/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? sv/jsp/ImageBrowse.html/2002 ?>" title="<? sv/jsp/ImageBrowse.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(44)"></td>
</tr>
</form>
</table>
<script>
imcmsGui("mid", null);
</script>
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form name="folder" action="ImageBrowse">
<input type="HIDDEN" name="caller" value="<%=imageBrowseFormBean.getCaller()%>">
<input type="HIDDEN" name="meta_id" value="<%=imageBrowseFormBean.getMetaId()%>">
<input type="HIDDEN" name="img_no" value="<%=imageBrowseFormBean.getImageNumber()%>">
<INPUT TYPE="HIDDEN" NAME="label" VALUE="<%=imageBrowseFormBean.getLabel()%>">
<input type="HIDDEN" name="imglist" value="<%=imageBrowseFormBean.getImageList()%>">
<input type="HIDDEN" name="dirlist_preset" value="<%=imageBrowseFormBean.getDirListPreset()%>">
<tr>
	<td colspan="3"><script>imcHeading("<? sv/jsp/ImageBrowse.html/4/1 ?> <%=imageBrowseFormBean.getImageNumber()%> <? sv/jsp/ImageBrowse.html/4/2 ?> <%=imageBrowseFormBean.getMetaId()%>",656);</script></td>
</tr>
<tr valign="top">
	<td width="45%" align="right">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/ImageBrowse.html/5 ?></td>
	</tr>
	<tr>
		<td>
		<select name="dirlist" size="15" onDblClick="document.forms.folder.change.click();" style="width:270">
			<%=imageBrowseFormBean.getFolders()%>
		</select></td>
	</tr>
	</table></td>
	<td width="10%" align="center">
	&nbsp;<br><input type="submit" class="imcmsFormBtnSmall" name="change" value="<? sv/jsp/ImageBrowse.html/2004 ?>"></td>
	<td width="45%">
	<table border="0" cellspacing="0" cellpadding="0">
	</form>
	<form name="imageForm" method="POST" action="<%=imageBrowseFormBean.getCaller()%>">
	<input type="HIDDEN" name="caller" value="<%=imageBrowseFormBean.getCaller()%>">
	<input type="HIDDEN" name="meta_id" value="<%=imageBrowseFormBean.getMetaId()%>">
	<input type="HIDDEN" name="img_no" value="<%=imageBrowseFormBean.getImageNumber()%>">
    <input type="HIDDEN" name="dirlist_preset" value="<%=imageBrowseFormBean.getDirListPreset()%>">
    <input type="HIDDEN" name="label" value="<%=imageBrowseFormBean.getLabel()%>">
	<tr>
		<td class="imcmsAdmText" align="right"><? sv/jsp/ImageBrowse.html/7 ?></td>
	</tr>
	<tr>
		<td>
		<select name="imglist" size="15" onDblClick="document.forms.imageForm.preview.click();" style="width:270">
		<%=imageBrowseFormBean.getOptions()%>
		</select></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="3" align="right">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="3" class="imcmsAdmText">
        <? sv/jsp/ImageBrowse.html/9 ?> <%=imageBrowseFormBean.getMaxNumber()%>&nbsp;&nbsp;&nbsp;&nbsp;  
        </td>
	</tr>
	<tr>
		<td align="right"><%=imageBrowseFormBean.getPreviousButton()%></td>
		<td>&nbsp;</td>
		<td><%=imageBrowseFormBean.getNextButton()%></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="3"><script>hr("100%",656,"blue");</script></td>
</tr>
<tr>
	<td colspan="3" align="right">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="submit" class="imcmsFormBtn" name="OK" value="<? sv/jsp/ImageBrowse.html/2005 ?>"></td>
		<td>&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtn" name="preview" value="<? sv/jsp/ImageBrowse.html/2006 ?>"></td>
		<td>&nbsp;</td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		</form>
		<form name="abortForm" method="POST" action="<%=imageBrowseFormBean.getCaller()%>">
		<input type="HIDDEN" name="caller" value="<%=imageBrowseFormBean.getCaller()%>">
		<input type="HIDDEN" name="meta_id" value="<%=imageBrowseFormBean.getMetaId()%>">
		<input type="HIDDEN" name="img_no" value="<%=imageBrowseFormBean.getImageNumber()%>">
		<input type="HIDDEN" name="label" value="<%=imageBrowseFormBean.getLabel()%>">
		<tr>
			<td><input type="Submit" class="imcmsFormBtn" name="avbryt" value="<? sv/jsp/ImageBrowse.html/2007 ?>"></td>
		</tr>
		</form>
		</table></td>
	</tr>
	</table></td>
</tr>
</table>
<script>
imcmsGui("bottom", null);
imcmsGui("outer_end", null);
</script>

<div align="center" id="previewDiv"><%=imageBrowseFormBean.getImagePreview()%></div>

<script language="JavaScript">
<!--
if (isMoz) {
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
