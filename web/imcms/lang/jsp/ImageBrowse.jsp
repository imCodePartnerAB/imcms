<%@ page
	
	import="com.imcode.imcms.servlet.admin.ImageBrowse,
	        org.apache.commons.lang.StringEscapeUtils,
	        com.imcode.imcms.servlet.admin.ImageBrowser,
	        imcode.util.HttpSessionUtils,
	        imcode.server.Imcms,
	        com.imcode.imcms.util.l10n.LocalizedMessage,
	        imcode.util.Utility,
            com.imcode.util.ImageSize,
	        java.io.File,
	        imcode.util.io.FileUtility,
	        java.util.List, java.awt.image.BufferedImage, javax.imageio.ImageIO, imcode.server.document.textdocument.ImageDomainObject, imcode.server.document.textdocument.ImagesPathRelativePathImageSource, imcode.util.ImcmsImageUtils"
	
	contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"
	
%><%@taglib prefix="vel" uri="imcmsvelocity"
%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%!

private final static int THUMB_BOUNDARIES   = 130 ;

private final static int MIN_CONTENT_WIDTH  = 760 ; // Entire table
private final static int MIN_CONTENT_HEIGHT = 350 ; // Only div with images

%><%

boolean fromEditor = (request.getParameter("editor_image") != null && request.getParameter("editor_image").equals("true")) ;

ImageBrowse.ImageBrowserPage imageBrowsePage = ImageBrowse.ImageBrowserPage.fromRequest(request) ;

File currentDirectory = imageBrowsePage.getCurrentDirectory() ;

File imagesRoot = Imcms.getServices().getConfig().getImagePath() ;
List<File> images = imageBrowsePage.getImagesList() ;

String cp = request.getContextPath() ;


%><vel:velocity><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>

<title><fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/1" /></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp" />
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body style="margin-bottom:0 !important;<%= fromEditor ? " overflow:auto;" : "" %>"<%= fromEditor ? " scroll=\"auto\"" : "" %>>


#gui_outer_start()
#gui_head("<fmt:message key="global/imcms_administration" />")
<form action="ImageBrowse" method="POST" enctype="multipart/form-data">
<input type="hidden" id="dir" name="dir" value="<%= ("\\" + FileUtility.relativizeFile( Imcms.getServices().getConfig().getImagePath().getParentFile(), currentDirectory ).toString()).replace("\\", "/") %>" />
<input type="hidden" name="editor_image" value="<%= request.getParameter("editor_image") %>" />
<input type="hidden" name="<%= ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER %>" value="<%=HttpSessionUtils.getSessionAttributeNameFromRequest(request, ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER)%>" /><%
if (null != imageBrowsePage.getLabel() ) { %>
<input type="hidden" name="<%= ImageBrowse.REQUEST_PARAMETER__LABEL %>" value="<%=imageBrowsePage.getLabel()%>" /><%
} %>
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><input type="Submit" class="imcmsFormBtn imcmsFormBtnCancel" name="<%= ImageBrowse.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/2001" />" /></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/2002" />" title="<fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/2003" />" class="imcmsFormBtn" onClick="openHelpW('ImageArchive')" /></td>
</tr>
</table>
#gui_mid()
<table id="mainTable" border="0" cellspacing="0" cellpadding="2" align="center" style="width:<%= MIN_CONTENT_WIDTH %>px;">
<tr>
	<td colspan="2">#gui_heading( "<fmt:message key="templates/sv/change_img.html/4/1" />" )</td>
</tr>
<tr>
	<td style="width:260px;">
	<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
	<tr>
		<td><b><fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/5" /></b></td>
		<td align="right">
		<input type="submit" class="imcmsFormBtnSmall" id="changeDir" name="<%= ImageBrowse.REQUEST_PARAMETER__CHANGE_DIRECTORY_BUTTON %>" value="<fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/2004" />" /></td>
	</tr>
	</table></td>
	
	<td style="padding-left:25px;">
	<b><fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/7" /></b>
	<span class="imcmsAdmDim" style="padding-left:20px;">
		Double click image to use. Single click to select.
	</span></td>
</tr>
<tr valign="top">
	<td>
	<select id="directorySelect" name="<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_DIRECTORY %>" size="30" ondblclick="jQ('#changeDir').click();" style="width:260px; height:<%= MIN_CONTENT_HEIGHT + 10 %>px;">
		<%=imageBrowsePage.getDirectoriesOptionList()%>
	</select></td>
	
	<td style="padding-left:25px;">
	<%--
	<select name="<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_URL %>" size="15"
		ondblclick="document.forms[0].elements['<%= StringEscapeUtils.escapeJavaScript( ImageBrowse.REQUEST_PARAMETER__OK_BUTTON ) %>'].click();" style="width:270px;"><%
	String imageOptionsList = imageBrowsePage.getImagesOptionList() ;
	if (!"".equals(imageOptionsList)) { %>
		<%= imageOptionsList %><%
	} else { %>
		<optgroup label="<fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/No_files" />" style="font-weight:normal !important; font-style:italic !important;"></optgroup><%
	} %>
	</select>
	--%>
	
	<div id="thumbsDiv" style="margin: 10px 0; height:<%= MIN_CONTENT_HEIGHT %>px; overflow:auto;"><%
		if (null != images) {
			int iCount = 0 ;
			for (File file : images) {
				//String pathAndFileName = "/images" + file.getPath().split("images")[1].replace("\\", "/");
				String filePath = file.getPath() ;
				String srcWithoutCp = "/images" + filePath.split("images")[1].replace("\\", "/");
				String imgStyle = "width:" + THUMB_BOUNDARIES + "px; height:" + THUMB_BOUNDARIES + "px;" ;
				String imgDimensions = "?" ;
				ImageDomainObject iDO = new ImageDomainObject() ;
				String path = filePath.replace(new File(imagesRoot, "/").toString(), "") ;
				ImagesPathRelativePathImageSource imageSource = new ImagesPathRelativePathImageSource(path) ;
				iDO.setSourceAndClearSize(imageSource) ;
				try {
                    ImageSize realSize = iDO.getRealImageSize();
                    int orgWidth = realSize.getWidth();
                    int orgHeight = realSize.getHeight();
					if (orgWidth > 0 && orgHeight > 0) {
						if (orgWidth < THUMB_BOUNDARIES && orgHeight < THUMB_BOUNDARIES) {
                            iDO.setWidth(orgWidth);
                            iDO.setHeight(orgHeight);
							imgStyle = "width:" + orgWidth + "px; height:" + orgHeight + "px;" ;
						} else if (orgWidth > orgHeight) {
                            iDO.setWidth(THUMB_BOUNDARIES);
							imgStyle = "width:" + THUMB_BOUNDARIES + "px;" ;
						} else {
                            iDO.setHeight(THUMB_BOUNDARIES);
							imgStyle = "height:" + THUMB_BOUNDARIES + "px;" ;
						}
						imgDimensions = orgWidth + " x " + orgHeight + ", " + ImageBrowse.getSimpleFileSize(file.length()) ;
					}
				} catch (Exception ignore) {}
                String previewSrc = ImcmsImageUtils.getImagePreviewUrl(iDO, cp) ;
				String value = FileUtility.relativeFileToString(FileUtility.relativizeFile(imagesRoot, file)) ;
				String src = cp + srcWithoutCp ;
				String fileName = file.getName(); %>
		<div id="imageDiv<%= iCount %>" style="float:left; width:<%= THUMB_BOUNDARIES + 10 %>px; border: 1px solid #f5f5f7; border-width: 0 10px 10px 0;">
			<table border="0" cellspacing="0" cellpadding="0" style="width:<%= THUMB_BOUNDARIES %>px; border: 1px solid #ccc; background-color:#fff;">
			<tr>
				<td style="height:<%= THUMB_BOUNDARIES %>px; text-align:center; vertical-align:middle;">
				<a href="javascript://choose()" title="<%= StringEscapeUtils.escapeHtml(fileName) %>"
				   onclick="jQ('#imageCB<%= iCount %>').attr('checked', 'checked'); return false"
				   ondblclick="jQ('#imageCB<%= iCount %>').attr('checked', 'checked'); jQ('#useBtn').click(); return false"><%
					%><img src="<%= previewSrc %>" alt="" class="previewImage" style="<%= imgStyle %>margin:0; border:0;" longdesc="<%= iCount %>" /><%
				%></a></td>
			</tr>
			</table><%--
			<%= "<hr/>" + imageSource.getUrlPathRelativeToContextPath() %>
			<%= "<hr/>" + previewSrc + "<hr/>" %>
			<img src="<%= previewSrc %>" alt="" class="previewImage" style="<%= imgStyle %>margin:0; border:0;" />--%>
			<div style="padding:2px; text-align:center; color:#999; font-size:9px;">
				<%= imgDimensions %>
			</div>
			<label for="imageCB<%= iCount %>" title="<%= StringEscapeUtils.escapeHtml(fileName) %>" style="cursor:pointer;">
				<input type="radio"
				       id="imageCB<%= iCount %>"
				       name="<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_URL %>"
				       value="<%= StringEscapeUtils.escapeHtml(value) %>"
				       style="margin-right:5px; vertical-align:-2px;" />
				<%= ImageBrowse.truncateString(fileName, 15) %>
			</label>
		</div><%
				iCount++ ;
			}
		} %>
		<div style="clear:both;"></div>
	</div>
	
	
	
	
		
	</td>
</tr>
<tr>
	<td colspan="2">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="2" align="right">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="submit" class="imcmsFormBtn imcmsFormBtnDefault" id="useBtn" name="<%= ImageBrowse.REQUEST_PARAMETER__OK_BUTTON %>" value="<fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/2005" />" /></td>
		<td>&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtn" name="<%= ImageBrowse.REQUEST_PARAMETER__PREVIEW_BUTTON %>" value="<fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/2006" />"
		onclick="return previewFile();"></td>
		<td>&nbsp;</td>
		<td><input type="Submit" class="imcmsFormBtn imcmsFormBtnCancel" name="<%= ImageBrowse.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/2007" />"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="2" style="padding-top:35px;">#gui_heading( "<fmt:message key="templates/sv/change_img.html/4/1" />" )</td>
</tr><%
LocalizedMessage errorMessage = imageBrowsePage.getErrorMessage() ;
if (null != errorMessage) { %>
<tr>
	<td colspan="2"><span style="color: red"><%= errorMessage.toLocalizedString(request) %></span></td>
</tr><%
} %>
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td><input type="file" name="<%= ImageBrowse.REQUEST_PARAMETER__FILE %>" id="theFile" size="54" /></td>
		<td align="right">
		<input type="submit" class="imcmsFormBtn" name="<%= ImageBrowse.REQUEST_PARAMETER__UPLOAD_BUTTON %>" value="<fmt:message key="templates/sv/change_img.html/2005" />" /></td>
	</tr>
	</table></td>
</tr>
</table>
</form>
#gui_bottom()
#gui_outer_end()

<%

if (null != imageBrowsePage.getImageUrl()) {

	ImageDomainObject iDO = new ImageDomainObject() ;
	ImagesPathRelativePathImageSource imageSource = new ImagesPathRelativePathImageSource(imageBrowsePage.getImageUrl()) ;
	iDO.setSourceAndClearSize(imageSource) ;
	String previewSrc = ImcmsImageUtils.getImagePreviewUrl(iDO, cp) ;%>
<div align="center" id="previewDiv">
	<img src="<%= previewSrc %>" alt="" />
</div>
<script type="text/javascript">
jQ(document).ready(function($) {
	var $actImg = null ;
	$('img.previewImage').each(function() {
		if ($(this).attr('src') == '<%= previewSrc %>') {
			$actImg = $(this) ;
		}
	}) ;<%--
	console.log('$actImg: ' + $actImg + ', ' + $actImg.length) ;--%>
	if (1 == $actImg.length) {
		var id = $actImg.attr('longdesc') ;
		$('#imageCB' + id).attr('checked', 'checked') ;<%--
		console.log('CB: ' + $('#imageCB' + id)) ;--%>
	}
}) ;
</script><%
}

%>

</vel:velocity>
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

function getFile() {<%--
	var theVal = "";
	try {
		var f = document.forms[0];
		var oSel = f.<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_URL %>;
		if (oSel.selectedIndex > -1) theVal = oSel.options[oSel.selectedIndex].value;
	} catch (e) {}
	return theVal;--%>
	var $selRadio = jQ(':radio[id^="imageCB"]:checked') ;
	return ($selRadio.length > 0) ? $selRadio.val() : '' ;
}

<vel:velocity>
function previewFile() {
	try {
		var actFile = getFile() ;
		if ('' != actFile) {
			var file = "<%= Imcms.getServices().getConfig().getImageUrl() %>" + actFile ;
			if (isImageFile(file)) {
				popWinOpen(800,570,"$contextPath/imcms/$language/jsp/FileAdmin_preview.jsp?file=" + encodeURIComponent(file),"",1,0);
				return false;
			}
		}
	} catch (e) {}
	return true ;
}
</vel:velocity>
jQ(document).ready(function($) {
	resizeContent($) ;
	$(window).resize(function() {
		resizeContent($) ;
	}) ;
}) ;

function resizeContent($) {
	var $display = $(window) ;
	var displayW = $display.width() ;
	var displayH = $display.height() ;<%--
	imLog('display: ' + displayW + ' x ' + displayH) ;--%>
	var minW   = <%= MIN_CONTENT_WIDTH %> ;
	var minH   = <%= MIN_CONTENT_HEIGHT %> ;
	var offsetW = 140 ;
	var offsetH = 420 ;
	var newW, newH ;
	if (displayW > (minW + offsetW)) {
		newW = displayW - offsetW ;
	} else {
		newW = minW ;
	}
	if (displayH > (minH + offsetH)) {
		newH = displayH - offsetH ;<%----%>
		imLog('1, newH: ' + newH) ;
	} else {
		newH = minH ;<%----%>
		imLog('2, newH: ' + newH) ;
	}
	$('#mainTable').css('width', newW + 'px') ;
	$('#directorySelect').css('height', (newH + 10) + 'px') ;
	$('#thumbsDiv').css('height', newH + 'px') ;
}
//-->
</script>

</body>
</html>
