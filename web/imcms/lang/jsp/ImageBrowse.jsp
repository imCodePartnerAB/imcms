<%@ page import="com.imcode.imcms.servlet.admin.ImageBrowse,
                 com.imcode.imcms.servlet.admin.ImageBrowse,
                 org.apache.commons.lang.StringEscapeUtils,
                 com.imcode.imcms.servlet.admin.ImageBrowser,
                 imcode.util.HttpSessionUtils,
                 imcode.server.Imcms,
                 com.imcode.imcms.util.l10n.LocalizedMessage,
                 imcode.util.Utility, java.io.File, imcode.util.io.FileUtility"
        contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%><%

boolean fromEditor = (request.getParameter("editor_image") != null && request.getParameter("editor_image").equals("true")) ;

ImageBrowse.ImageBrowserPage imageBrowsePage = ImageBrowse.ImageBrowserPage.fromRequest(request) ;

File currentDirectory = imageBrowsePage.getCurrentDirectory() ;

%>
<vel:velocity>
<html>
<head>


<title><? install/htdocs/sv/jsp/ImageBrowse.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(0,'text')"<%
	if (fromEditor) {
		%> style="overflow:auto;" scroll="auto"<%
	} %>>


#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")
<form action="ImageBrowse" method="POST" enctype="multipart/form-data">
<input type="hidden" id="dir" name="dir" value="<%= ("\\" + FileUtility.relativizeFile( Imcms.getServices().getConfig().getImagePath().getParentFile(), currentDirectory ).toString()).replace("\\", "/") %>">
<input type="hidden" name="editor_image" value="<%= request.getParameter("editor_image") %>">
<input type="hidden" name="<%= ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER %>" value="<%=HttpSessionUtils.getSessionAttributeNameFromRequest(request, ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER)%>"><%
if (null != imageBrowsePage.getLabel() ) { %>
<input type="hidden" name="<%= ImageBrowse.REQUEST_PARAMETER__LABEL %>" value="<%=imageBrowsePage.getLabel()%>"><%
} %>
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><input type="Submit" class="imcmsFormBtn" name="<%= ImageBrowse.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2002 ?>" title="<? install/htdocs/sv/jsp/ImageBrowse.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('ImageArchive')"></td>
</tr>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<tr valign="top">
	<td width="45%" align="right">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td class="imcmsAdmText"><? install/htdocs/sv/jsp/ImageBrowse.html/5 ?></td>
        </tr>
        <tr>
            <td>
            <select name="<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_DIRECTORY %>" size="15"
                    ondblclick="document.forms[0].elements['<%= StringEscapeUtils.escapeJavaScript( ImageBrowse.REQUEST_PARAMETER__CHANGE_DIRECTORY_BUTTON ) %>'].click();" style="width:270px;">
                <%=imageBrowsePage.getDirectoriesOptionList()%>
            </select></td>
        </tr>
        </table>
    </td>
	<td width="10%" align="center">
    	<input type="submit" class="imcmsFormBtnSmall" name="<%= ImageBrowse.REQUEST_PARAMETER__CHANGE_DIRECTORY_BUTTON %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2004 ?>">
    </td>
	<td width="45%">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td class="imcmsAdmText" align="right"><? install/htdocs/sv/jsp/ImageBrowse.html/7 ?></td>
            </tr>
            <tr>
                <td>
								<select name="<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_URL %>" size="15"
								        ondblclick="document.forms[0].elements['<%= StringEscapeUtils.escapeJavaScript( ImageBrowse.REQUEST_PARAMETER__PREVIEW_BUTTON ) %>'].click();" style="width:270px;"><%
								String imageOptionsList = imageBrowsePage.getImagesOptionList() ;
								if (!"".equals(imageOptionsList)) { %>
									<%= imageOptionsList %><%
								} else { %>
									<optgroup label="<? install/htdocs/sv/jsp/ImageBrowse.html/No_files ?>" style="font-weight:normal !important; font-style:italic !important;"></optgroup><%
								} %>
								</select></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
	<td colspan="3">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="3" align="right">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><input type="submit" class="imcmsFormBtn" name="<%= ImageBrowse.REQUEST_PARAMETER__OK_BUTTON %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2005 ?>"></td>
                <td>&nbsp;</td>
                <td><input type="submit" class="imcmsFormBtn" name="<%= ImageBrowse.REQUEST_PARAMETER__PREVIEW_BUTTON %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2006 ?>"
                           onclick="return previewFile();"></td>
                <td>&nbsp;</td>
                <td><input type="Submit" class="imcmsFormBtn" name="<%= ImageBrowse.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? install/htdocs/sv/jsp/ImageBrowse.html/2007 ?>"></td>
            </tr>
        </table></td>
</tr>
<tr>
    <td colspan="3">#gui_heading( "<? templates/sv/change_img.html/4/1 ?>" )</td>
</tr>
<%
    LocalizedMessage errorMessage = imageBrowsePage.getErrorMessage() ;
    if (null != errorMessage) {
        %><tr>
            <td colspan="3"><span style="color: red"><%= errorMessage.toLocalizedString(request) %></span></td>
        </tr><%
    }
%>
<tr>
    <td colspan="3">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <td>
                    <input type="file" name="<%= ImageBrowse.REQUEST_PARAMETER__FILE %>" id="theFile" size="54">
                </td>
                <td align="right">
                    <input type="submit" class="imcmsFormBtn" name="<%= ImageBrowse.REQUEST_PARAMETER__UPLOAD_BUTTON %>" value="<? templates/sv/change_img.html/2005 ?>">
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr>
	<td colspan="3">#gui_hr( "blue" )</td>
</tr>
</table>
</form>
#gui_bottom()
#gui_outer_end()
<% if (null != imageBrowsePage.getImageUrl()) { %>
<div align="center" id="previewDiv"><img src="<%= StringEscapeUtils.escapeHtml( Utility.escapeUrl(request.getContextPath() + Imcms.getServices().getConfig().getImageUrl() + imageBrowsePage.getImageUrl() )) %>"></div>
<% } %>

<script type="text/javascript">
<!--
if (document.getElementById) {
	if (document.getElementById("previewDiv")) {
		if (!/\./.test(document.getElementById("previewDiv").innerHTML.toString())) {
			document.getElementById("previewDiv").style.display = "none" ;
		}
	}
}

var pai = "\\.(GIF|JPE?G|PNG)$";

function isImageFile(file) {
	try {
		var re = new RegExp(pai, 'gi');
		return re.test(file);
	} catch (e) {
		return false ;
	}
}

function getFile() {
	var theVal = "";
	try {
		var f = document.forms[0];
		var oSel = f.<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_URL %>;
		if (oSel.selectedIndex > -1) theVal = oSel.options[oSel.selectedIndex].value;
	} catch (e) {}
	return theVal;
}

function previewFile() {
	try {
		var file = document.getElementById("dir").value + "/" + getFile();
		if (isImageFile(file)) {
			popWinOpen(800,570,"$contextPath/imcms/$language/jsp/FileAdmin_preview.jsp?file=" + escape(file),"",1,0);
			return false;
		}
	} catch (e) {}
	return true ;
}
//-->
</script>

</body>
</html>
</vel:velocity>
