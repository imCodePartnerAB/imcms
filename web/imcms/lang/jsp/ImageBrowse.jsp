<%@ page
	
	import="com.imcode.imcms.addon.ui.DocumentPaging,
	        com.imcode.imcms.servlet.admin.ImageBrowse,
	        com.imcode.imcms.servlet.admin.ImageBrowser,
	        com.imcode.imcms.util.l10n.LocalizedMessage,
	        com.imcode.util.ImageSize,
	        imcode.server.Imcms,
	        imcode.server.document.textdocument.ImageDomainObject,
	        imcode.server.document.textdocument.ImagesPathRelativePathImageSource,
	        imcode.util.Html,
	        imcode.util.HttpSessionUtils,
	        imcode.util.ImcmsImageUtils,
	        imcode.util.ToStringPairTransformer,
	        imcode.util.Utility,
	        imcode.util.image.Format,
	        imcode.util.image.Resize,
	        imcode.util.io.FileUtility,
	        org.apache.commons.io.comparator.LastModifiedFileComparator,
	        org.apache.commons.io.comparator.NameFileComparator,
	        org.apache.commons.io.comparator.PathFileComparator"
	
	contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"
	
%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.oro.text.perl.Perl5Util" %>
<%@ page import="java.io.File" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@taglib prefix="vel" uri="imcmsvelocity"
%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%!

private final static int LAST_MOD_COUNT_ON_TOP                    = 5 ;
private final static int LAST_MOD_COUNT_ON_TOP_ACTIVATED_ON_COUNT = 10 ;

private final static int PAGING_FILE_COUNT_LIMIT = 100 ;
private final static int HITS_PER_PAGE           = 100 ;

private final static int THUMB_BOUNDARIES   = 130 ;

private final static int MIN_CONTENT_WIDTH  = 760 ; // Entire table
private final static int MIN_CONTENT_HEIGHT = 350 ; // Only div with images

public static int getIntRequestParam(String paramName, int defaultValue, HttpServletRequest request) {
	try {
		return Integer.parseInt(request.getParameter(paramName)) ;
	} catch(Exception e) {
		return defaultValue ;
	}
}

public String getDirectoriesOptionList(File currentDirectory) throws java.io.IOException {
	final File imagesRoot = Imcms.getServices().getConfig().getImagePath() ;
	Collection imageDirectories = Utility.collectImageDirectories() ;
	List<File> imageDirectoriesList = new ArrayList<File>(imageDirectories);
	Collections.sort(imageDirectoriesList, PathFileComparator.PATH_INSENSITIVE_COMPARATOR) ;
	File currentDirectoryRelativeToImageRootParent = FileUtility.relativizeFile( imagesRoot.getParentFile(), currentDirectory ) ;
	return Html.createOptionList(imageDirectoriesList, currentDirectoryRelativeToImageRootParent, new ToStringPairTransformer() {
		public String[] transformToStringPair(Object input) {
			File file = (File) input ;
			return new String[] { FileUtility.relativeFileToString(file), FileUtility.relativeFileToString(file) } ;
		}
	}) ;
}

%><%

Perl5Util re = new Perl5Util() ;

boolean fromEditor = (request.getParameter("editor_image") != null && request.getParameter("editor_image").equals("true")) ;

ImageBrowse.ImageBrowserPage imageBrowsePage = ImageBrowse.ImageBrowserPage.fromRequest(request) ;

File currentDirectory = imageBrowsePage.getCurrentDirectory() ;

File imagesRoot = Imcms.getServices().getConfig().getImagePath() ;
List<File> images = new ArrayList<File>(imageBrowsePage.getImagesList()) ; // Can't modify an AbstractList
Integer uploadedImageIndex = (
       request.getMethod().equalsIgnoreCase("post")
    && request.getParameter(ImageBrowse.REQUEST_PARAMETER__UPLOAD_BUTTON) != null
    && imageBrowsePage.getErrorMessage() == null
    && imageBrowsePage.getCurrentImage() != null
    ) ? images.indexOf(imageBrowsePage.getCurrentImage())
      : null;
List<File> lastModImages = null ;
if (images.size() > LAST_MOD_COUNT_ON_TOP_ACTIVATED_ON_COUNT) {
	Collections.sort(images, LastModifiedFileComparator.LASTMODIFIED_REVERSE) ;
	lastModImages = new ArrayList<File>(images.subList(0, LAST_MOD_COUNT_ON_TOP)) ;
}
Collections.sort(images, NameFileComparator.NAME_INSENSITIVE_COMPARATOR) ;
boolean hasNewImagesFirst = false ;
if (null != lastModImages) {
	images.addAll(0, lastModImages) ;
	hasNewImagesFirst = true ;
}

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;

String cp = request.getContextPath() ;


%><vel:velocity><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>

<title><fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/1" /></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp" />
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

<style type="text/css">
/* *******************************************************************************************
 *         Paging                                                                            *
 ******************************************************************************************* */
<%
String uAgent   = StringUtils.defaultString(request.getHeader("USER-AGENT")).toLowerCase() ;
boolean isGecko = re.match("/gecko/i", uAgent) ;
%>
.paging {
	clear: both;
	margin: 10px auto;
	padding: 10px;
	text-align: center;
	font: 10px Verdana;
	color: #616d98 !important;
}
.pagingTop {
	margin-top: 0;
	border-bottom: 1px solid #eaeaea;
}
.paging A,
.paging A:link,
.paging A:visited,
.paging SPAN {
	<%= "display: -moz-inline-box;" %>
	display: inline-block;<%-- IE 6 support only on non-block-elements!?! - And these are that... --%>
	background-color: #b8c6d5;
	color: #fff;
	font: 11px Tahoma, Arial, sans-serif;
	text-shadow: #000 1px 1px 1px;
	border: <%= isGecko ? 1 : 2 %>px solid #999;
	border-color: #dae4ef #999 #999 #dae4ef;
	cursor:pointer;
	padding: <%= isGecko ? 2 : 1 %>px 8px;
	text-decoration: none !important;
}
.paging SPAN.paging_dots {
	color: #666;
	margin: 2px !important;
	background: transparent !important;
	border: 0 !important;
}
.paging .oneDigit {
	padding: <%= isGecko ? 2 : 1 %>px 10px;
}
.paging .prevBtn {
	margin-right: 5px !important;
	padding: <%= isGecko ? 2 : 1 %>px 10px;
}
.paging .nextBtn {
	margin-left: 5px !important;
	padding: <%= isGecko ? 2 : 1 %>px 10px;
}
.paging .dim {
	color: #999 !important;
}
.paging A:active,
.paging A:hover,
.paging .active {
	background-color: #20568d;
	border-color: #668db6 #000 #000 #668db6;
}
</style>
<%--
/* *******************************************************************************************
 *         LAZY LOAD FIX                                                                     *
 ******************************************************************************************* */
--%>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="$contextPath/imcms/scripts/jquery.lazyload.min.js"></script>
<script type="text/javascript">
jQuery(document).ready(function($) {
	$('img.lazy').lazyload({
		container : $('#thumbsDiv'),
		threshold : 200,
		effect    : 'fadeIn'
	}) ;
    window.setTimeout(function() {
        $('img.lazy').trigger('scroll') ;
    }, 1000) ;
}) ;
</script>
<%--
/* *******************************************************************************************
 *         END LAZY LOAD FIX                                                                 *
 ******************************************************************************************* */
--%>

<%--
    Selects uploaded image thumbnail.
    If uploaded image thumbnail is not shown in 'recently added images' section then thumbnail is scrolled into view.
--%>
<%
    if (uploadedImageIndex != null) {
        if (hasNewImagesFirst) {
            %>
            <script>
                // -vs- window.onload ???
                jQ(function() {
                    var imageCB = document.getElementById("imageCB0");
                    if (imageCB) {
                        imageCB.checked = true;
                    }
                });
            </script>
            <%
        } else {
            %>
            <script>
                // -vs- window.onload ???
                jQ(function() {
                    var imageCB = document.getElementById("imageCB"+<%=uploadedImageIndex%>);
                    var imageDiv = document.getElementById("imageDiv"+<%=uploadedImageIndex%>);
                    if (imageCB && imageDiv) {
                        imageCB.checked = true;
                        imageDiv.scrollIntoView(false);
                    }
                });
            </script>
            <%
        }
    }
%>

</head>
<body style="margin-bottom:0 !important;<%= fromEditor ? " overflow:auto;" : "" %>"<%= fromEditor ? " scroll=\"auto\"" : "" %>>


#gui_outer_start()
#gui_head("<fmt:message key="global/imcms_administration" />")
<form action="ImageBrowse" method="POST" enctype="multipart/form-data" accept-charset="UTF-8">
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
	<b><%= images.size() + " " %><fmt:message key="install/htdocs/sv/jsp/ImageBrowse.html/7" /></b>
	<span class="imcmsAdmDim" style="padding-left:20px;">
		<fmt:message key="image_browser.usage"/><%
		if (hasNewImagesFirst) { %>
		<fmt:message key="image_browser.recent" /><%
		} %>
	</span></td>
</tr>
<tr valign="top">
	<td>
	<select id="directorySelect" name="<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_DIRECTORY %>" size="30" ondblclick="jQ('#changeDir').click();" style="width:260px; height:<%= MIN_CONTENT_HEIGHT + 10 %>px;">
		<%= getDirectoriesOptionList(currentDirectory) %>
		<%//=imageBrowsePage.getDirectoriesOptionList()%>
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
	<div id="pagingTopDiv"></div>
	<div id="thumbsDiv" style="margin: 10px 0; height:<%= MIN_CONTENT_HEIGHT %>px; overflow:auto;"><%
		if (null != images) {
			int pageIdx = getIntRequestParam("idx", 0, request) - 1 ;
			int idx_0 = 0 ;
			int idx_1 = images.size() ;
			if (images.size() > PAGING_FILE_COUNT_LIMIT) {
				idx_0 = (pageIdx > -1) ? pageIdx * HITS_PER_PAGE : 0 ;
				idx_1 = (pageIdx > -1) ? (pageIdx + 1) * HITS_PER_PAGE : HITS_PER_PAGE ;
				idx_1 = (images.size() < idx_1) ? images.size() : idx_1 ;
			}
			for (int i = idx_0; i < idx_1; i++) {
				File file = images.get(i) ;
				String filePath = file.getPath() ;
				String fileName = file.getName();
				String imgDimensions = "?" ;
				ImageDomainObject iDO = new ImageDomainObject() ;
				String path = filePath.replace(new File(imagesRoot, "/").toString(), "") ;
				ImagesPathRelativePathImageSource imageSource = new ImagesPathRelativePathImageSource(path) ;
				iDO.setFormat(fileName.toLowerCase().endsWith(".png") ? Format.PNG : fileName.toLowerCase().endsWith(".gif") ? Format.GIF : Format.JPEG);
				iDO.setSourceAndClearSize(imageSource) ;
				iDO.setWidth(THUMB_BOUNDARIES);
				iDO.setHeight(THUMB_BOUNDARIES);
				iDO.setResize(Resize.GREATER_THAN);
				boolean canPreview = false;
				try {
					ImageSize realSize = ImcmsImageUtils.getCachedRealSize(iDO);
					int orgWidth = realSize.getWidth();
					int orgHeight = realSize.getHeight();
					canPreview = (orgWidth > 0 && orgHeight > 0);
					imgDimensions = orgWidth + " x " + orgHeight + ", " + ImageBrowse.getSimpleFileSize(file.length()) ;
					if (0 == orgWidth || 0 == orgHeight || 0 == file.length()) {
						continue ;
					}
				} catch (Exception ignore) {}
				String previewSrc = ImcmsImageUtils.getImageUrl(null, iDO, cp);
				String value = FileUtility.relativeFileToString(FileUtility.relativizeFile(imagesRoot, file)) ; %>
		<div id="imageDiv<%= i %>" style="float:left; width:<%= THUMB_BOUNDARIES + 2 %>px; border: 1px solid #f5f5f7; border-width: 0 10px 10px 0; padding-bottom:5px;<%
				if (hasNewImagesFirst && i < LAST_MOD_COUNT_ON_TOP) {
					%> background-color:#ff9 !important;<%
				} %>">
			<table border="0" cellspacing="0" cellpadding="0" style="width:<%= THUMB_BOUNDARIES %>px; border: 1px solid #ccc; background-color:#fff;">
			<tr>
				<td style="height:<%= THUMB_BOUNDARIES %>px; text-align:center; vertical-align:middle;">
				<a href="javascript://choose()" title="<%= StringEscapeUtils.escapeHtml(fileName) %>"
				   onclick="jQ('#imageCB<%= i %>').attr('checked', 'checked'); return false"
				   ondblclick="jQ('#imageCB<%= i %>').attr('checked', 'checked'); jQ('#useBtn').click(); return false"><%
					%><img class="lazy previewImage" src="$contextPath/imcms/images/img_loading.gif" data-original="<%= previewSrc %>" style="margin:0; border:0;" longdesc="<%= i %>" alt="" /><%
				%></a></td>
			</tr>
			</table>
			<div style="padding:2px; text-align:center; color:#999; font-size:9px;">
				<%= imgDimensions + "<br/>" + df.format(file.lastModified()) %>
			</div>
			<label for="imageCB<%= i %>" title="<%= StringEscapeUtils.escapeHtml(fileName) %>" style="cursor:pointer;">
				<input type="radio"
				       id="imageCB<%= i %>"
				       name="<%= ImageBrowse.REQUEST_PARAMETER__IMAGE_URL %>"
				       value="<%= StringEscapeUtils.escapeHtml(value) %>"
                       data-canpreview="<%= canPreview %>"
				       style="margin-right:5px; vertical-align:-2px;" />
				<%= ImageBrowse.truncateString(fileName, 15) %>
			</label>
		</div><%
			}
			if (images.size() > PAGING_FILE_COUNT_LIMIT && !images.isEmpty()) {
				DocumentPaging paging = new DocumentPaging("PAGE", pageIdx + 1, images.size(), HITS_PER_PAGE) ;
				paging.setDivId("pagingBottomDiv") ;
				paging.setDivClass("paging") ;
				paging.setSpaceBetween("") ;
				paging.setShortVisPagesCount(4) ;
				paging.setMaxVisPagesCount(20) ;
				String pagingHtml = paging.getPagingLinksAsHtml() ;
				pagingHtml = re.substitute("s/href=\"[^\"]*?([\\d]+)[^\"]*?\"/href=\"#goToPage($1)\" onclick=\"goToPage($1); return false\"/gi", pagingHtml) ; %>
			<%= pagingHtml %><%
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

<form id="pagingForm" action="ImageBrowse" method="POST" enctype="multipart/form-data">
	<input type="hidden" id="dir" name="dir" value="<%= ("\\" + FileUtility.relativizeFile( Imcms.getServices().getConfig().getImagePath().getParentFile(), currentDirectory ).toString()).replace("\\", "/") %>" />
	<input type="hidden" name="editor_image" value="<%= request.getParameter("editor_image") %>" />
	<input type="hidden" name="<%= ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER %>" value="<%=HttpSessionUtils.getSessionAttributeNameFromRequest(request, ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER)%>" /><%
	if (null != imageBrowsePage.getLabel() ) { %>
	<input type="hidden" name="<%= ImageBrowse.REQUEST_PARAMETER__LABEL %>" value="<%=imageBrowsePage.getLabel()%>" /><%
	} %>
	<input type="hidden" id="idx" name="idx" value="1" />
</form>
<script type="text/javascript">
function goToPage(idx) {
	jQ('#idx').val(idx) ;
	jQ('#pagingForm').submit() ;
}
try {
	if (jQ('#pagingBottomDiv')) jQ('#pagingTopDiv').addClass("paging").addClass("pagingTop").html(jQ('#pagingBottomDiv').html()) ;
} catch(e) {}
</script>
#gui_bottom()
#gui_outer_end()

</vel:velocity>
<script type="text/javascript">
<!--
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
    
    if ($selRadio.length > 0) {
        return {
            path: $selRadio.val(), 
            canPreview: ($selRadio.attr("data-canpreview") == "true")
        };
    }
    
    return null;
}

<vel:velocity>
function previewFile() {
	try {
		var actFile = getFile() ;
        if (actFile != null) {
			var file = "<%= Imcms.getServices().getConfig().getImageUrl() %>" + actFile.path ;
			if (actFile.canPreview) {
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
		newH = displayH - offsetH ;<%--
		imLog('1, newH: ' + newH) ;--%>
	} else {
		newH = minH ;<%--
		imLog('2, newH: ' + newH) ;--%>
	}
	$('#mainTable').css('width', newW + 'px') ;
	$('#directorySelect').css('height', (newH + 10) + 'px') ;
	$('#thumbsDiv').css('height', newH + 'px') ;
}
//-->
</script>

</body>
</html>
