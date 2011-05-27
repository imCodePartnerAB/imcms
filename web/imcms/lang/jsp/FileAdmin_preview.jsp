<%@ page language="java"
	
	import="imcode.server.Imcms,
	        imcode.util.Utility,
	        org.apache.oro.text.perl.Perl5Util,
	        javax.imageio.ImageIO,
	        java.awt.image.BufferedImage,
	        java.io.BufferedReader,
	        java.io.File,
	        java.io.FileInputStream,
	        java.io.InputStreamReader, java.text.DecimalFormat, org.apache.commons.lang.StringEscapeUtils, java.io.IOException, org.apache.commons.lang.StringUtils, java.net.URLEncoder, imcode.server.document.textdocument.ImageDomainObject, imcode.server.document.textdocument.ImagesPathRelativePathImageSource, imcode.util.ImcmsImageUtils"
	
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	
%><%

String cp = request.getContextPath() ;

/* *******************************************************************
 *           SETTINGS                                                *
 ******************************************************************* */

String ren = "(\\.[\\d]+)?" ;

String acceptedExtPattern = "/" +
	"(\\.(GIF|JPE?G|PNG|BMP|AVI|MPE?G|HTML?|CSS|JS|VBS|TXT|JSP|ASP|FRAG|PROPERTIES)+" + ren + "$)" +
	"|(\\.LOG+)" +
	"/i" ;

String IMG_PATH   = cp+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ; // path to buttons (with trailing /)

/* *******************************************************************
 *           INIT                                                    *
 ******************************************************************* */

String file       = request.getParameter("file") ;
String orgFileParam = StringUtils.defaultString(request.getParameter("file")) ;

String frame      = (request.getParameter("frame") != null) ? request.getParameter("frame") : "FRAME" ;
String thisPage = cp + request.getServletPath();

String zoom       = "" ;
String defZoom    = "1.0" ;

if (request.getParameter("zoom") != null) {
	zoom            = " style=\"zoom:" + request.getParameter("zoom") + "\"" ;
	session.setAttribute("zoom", request.getParameter("zoom")) ;
	defZoom         = request.getParameter("zoom") ;
} else if (session.getAttribute("zoom") != null) {
	zoom            = " style=\"zoom:" + session.getAttribute("zoom") + "\"" ;
	defZoom         = (String) session.getAttribute("zoom") ;
}

String border     = "" ;
boolean hasBorder = false ;
if (request.getParameter("border") != null) {
	border          = (request.getParameter("border").equals("1")) ? " border=\"1\"" : "" ;
	hasBorder       = (request.getParameter("border").equals("1")) ;
	session.setAttribute("border", request.getParameter("border")) ;
} else if (session.getAttribute("border") != null) {
	border          = (session.getAttribute("border").equals("1")) ? " border=\"1\"" : "" ;
	hasBorder       = (session.getAttribute("border").equals("1")) ;
}

File webRoot    = Imcms.getPath() ;
File fn = new File(webRoot, file) ;

/* Is image? */
Perl5Util re       = new Perl5Util() ;
boolean isImage    = re.match(acceptedExtPattern, file) ;

/* Check browser */

String uAgent = StringUtils.defaultString(request.getHeader("USER-AGENT")) ;
boolean hasDocumentAll    = re.match("/(MSIE \\d)/i", uAgent) ;
boolean hasGetElementById = re.match("/Gecko/i", uAgent) || hasDocumentAll ;
boolean hasDocumentLayers = (re.match("/Mozilla/i", uAgent) && !hasDocumentAll && !hasGetElementById) ;
boolean isMac = re.match("/Mac/i", uAgent) ;

/* if Stat-Report - Read file and show it */
boolean isStat    = (request.getParameter("isStat") != null) ;

if (isStat && frame.equalsIgnoreCase("MAIN")) {

    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fn))) ;

    String statSrc  ;
    String fileLine ;
    StringBuffer strbuf = new StringBuffer();
    while ((fileLine = br.readLine())!= null) {
        strbuf.append(fileLine).append( "\n") ;
    }
    statSrc = strbuf.toString() ;
    br.close() ;

    statSrc = statSrc.replaceAll("<head>","<head>\n\n<base target=\"_blank\">");	/* add some buttons in some browsers */

	String theButtons ;
	boolean hasInlineButtons = false ;

	theButtons = "<table border=\"0\" bgcolor=\"#d6d3ce\" align=\"right\">\n<tr>" ;
	if (hasGetElementById && !hasDocumentAll && !isMac) {
		hasInlineButtons = true ;
         theButtons += "\n   <td><a href=\"#\" onClick=\"find(); return false\"><img align=\"absmiddle\" src=\"" + IMG_PATH + "btn_find.gif\" border=\"0\" alt=\"Sök!\"></a></td>" ;
	}
	if (isMac) {
		hasInlineButtons = true ;
		theButtons += "\n	<td><a href=\"javascript: print(); return false\"><img src=\"" + IMG_PATH + "btn_print.gif\" border=\"0\" alt=\"Skriv ut!!\"></a></td>" ;
	}
	theButtons += "\n</tr>\n</table>\n" ;

	if (hasInlineButtons) {
		statSrc = statSrc.replaceAll("<body>", "<body>\n\n" + theButtons + "\n");
	}

	/* print it */

	out.print(statSrc) ;
	return ;
}

/* Get formated file size */

String size  = "" ;
double iSize ;

try {
	iSize = (double) fn.length() ;
	if (iSize >= (1024*1024)) {
		iSize = iSize / 1024 / 1024 ;
		DecimalFormat df = new DecimalFormat("#.#") ;
		size  = df.format(iSize) ;
		size  = size.replaceAll(",", ".") + " MB" ;
	} else if (iSize >= 1024) {
		iSize = iSize / 1024 ;
		DecimalFormat df = new DecimalFormat("#.#") ;
		size  = df.format(iSize) ;
		size  = size.replaceAll(",", ".") + " kB" ;
	} else {
		size  = fn.length() + " bytes" ;
	}
} catch ( NumberFormatException ex ) {
	// ignore
}

//out.print("fn.length(): " + fn.length() + "<br><br>iSize: " + iSize + "<br><br>size: " + size) ;

%><%@taglib prefix="vel" uri="imcmsvelocity"%><vel:velocity><%

/* *******************************************************************************************
 *         FRAME MAIN                                                                        *
 ******************************************************************************************* */

if (frame.equalsIgnoreCase("MAIN")) { %>
<html>
<head>
<title><%= StringEscapeUtils.escapeHtml(file) %></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body class="imcmsAdmBgCont" style="margin:10px">

<div align="center"><%

if (isImage) {
    try {
        BufferedImage image = ImageIO.read( fn ); %>
	<div class="imcmsAdmText" style="padding: 5px 0 <%= hasBorder ? 7 : 8 %>px 0; color:#666;">
		&quot;<%= cp %><%= file.replaceAll("\\\\","/") %>&quot;<%
        if (image.getWidth() > 0 && image.getHeight() > 0 && !size.equals("")) {
            %> (<%
            if (image.getWidth() > 0 && image.getHeight() > 0) {
                %><%= image.getWidth() + "x" + image.getHeight() %> px, <%
	            if (hasGetElementById && !hasDocumentAll && defZoom.matches("^\\d+\\.\\d+$")) {// Gecko
		            double dZoom = Double.parseDouble(defZoom) ;
		            double newWidth = (dZoom * image.getWidth()) ;
		            //out.print(defZoom + " - " + dZoom + " - " + newWidth + " - " + Math.round(newWidth) + " - ");
		            zoom = " style=\"width:" + Math.round(newWidth) + "px;\"" ;
	            }
            }
            if (!size.equals("")) {
                %><%= size %><%
            } %>)<%
        } %>
	</div><%
    } catch( IOException ignored ) {
	    //out.print(ignored);
	    if (hasGetElementById && !hasDocumentAll) zoom = "" ;
    } %><%--
	<img name="theImg" id="theImg" src="$contextPath<%= Utility.escapeUrl(file).replaceAll("(%5C|%255C)","/") %>"<%= border + zoom %> alt="">--%><%
	
		File imagesRoot = Imcms.getServices().getConfig().getImagePath() ;
		ImageDomainObject iDO = new ImageDomainObject() ;
		String path = file.replace(new File(imagesRoot, "/").toString(), "") ;
		ImagesPathRelativePathImageSource imageSource = new ImagesPathRelativePathImageSource(path) ;
		iDO.setSourceAndClearSize(imageSource) ;
		String previewSrc = ImcmsImageUtils.getImagePreviewUrl(iDO, cp) ;
		
		%>
	<img name="theImg" id="theImg" src="<%= previewSrc %>"<%= border + zoom %> alt="" /><%
} %>
</div>

</body>
</html><%

/* *******************************************************************************************
 *         FRAME TOP                                                                         *
 ******************************************************************************************* */

} else if (frame.equalsIgnoreCase("TOP")) { %>
<html>
<head>
<title><%= StringEscapeUtils.escapeHtml(file) %></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

<% /*<STYLE TYPE="text/css">
<!--
.imHeading { font: bold 17px Verdana, Geneva, sans-serif; color:#000066; }
.imFilename { font: bold italic 10px Verdana, Geneva, sans-serif; color:#009900; }
.norm { font: 11px Verdana, Geneva, sans-serif; color:#333333; }
SELECT, INPUT { font: 10px Verdana, Geneva, sans-serif; color:#333333; }
A:link, A:visited, A:active { color:#000099; text-decoration:none; }
-->
</STYLE>*/ %>

<script language="JavaScript">
<!--
function closeIt() {
	top.window.close();
	if (top.parent.opener) top.parent.opener.focus();
}

var hasGetElementById = (document.getElementById);
var hasDocumentAll  = (document.all);
var hasDocumentLayers  = (document.layers);

var win = parent.main;
var n   = 0;

function findIt(str) {
	var txt, i, found;
	if (hasDocumentLayers && str != "") {
		if (!win.find(str)) {
			while(win.find(str, false, true)) {
				n++;
			}
		} else {
			n++;
			if (n == 0) alert("Not found.");
		}
	} else if ((hasDocumentAll || hasGetElementById) && str != "") {
		txt = win.document.body.createTextRange();
		for (i = 0; i <= n && (found = txt.findText(str)); i++) {
			txt.moveStart("character", 1);
			txt.moveEnd("textedit");
		}
		if (found) {
			txt.moveStart("character", -1);
			txt.findText(str);
			txt.select();
			txt.scrollIntoView();
			n++;
		} else {
			if (n > 0) {
				n = 0;
				findIt(str);
			} else {
				alert("Not found.");
			}
		}
	}
	if (hasDocumentAll && hasGetElementById) document.getElementById("btnSearch").setActive();
}
//-->
</script>

</head>
<body class="imcmsAdmBgHead" style="margin:0; border:0;">

<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%" style="border-bottom: 1px solid #000000">
<tr>
	<td height="35" nowrap><span class="imcmsAdmHeadingTop">
	&nbsp;<%
	if (isStat) {
		%><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1002/1 ?><%
	} else {
		%><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1002/2 ?><%
	} %> &nbsp;</span></td>

	<td align="right"><%
	if (isStat) { %>
	<form action="" onSubmit="findIt(document.forms[0].searchString.value); return false">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr><%
		if (hasDocumentAll || hasDocumentLayers) { %>
		<td class="imcmsAdmText"><input type="text" name="searchString" size="15" value="" class="imcmsAdmText" style="width:100px;"></td>
		<td><a id="btnSearch" href="javascript://find()" onClick="findIt(document.forms[0].searchString.value);"><img src="<%= IMG_PATH %>btn_find.gif" border="0" hspace="5" alt="<? install/htdocs/sv/jsp/FileAdmin_preview.jsp/2001 ?>"></a></td><%
		} %>
		<td class="imcmsAdmText">&nbsp;&nbsp;</td><%
		if (!isMac) { %>
		<td><a href="javascript://print()" onClick="top.frames.main.focus(); top.frames.main.print();"><img src="<%= IMG_PATH %>btn_print.gif" border="0" hspace="5" alt="<? install/htdocs/sv/jsp/FileAdmin_preview.jsp/2002 ?>"></a></td><%
		} %>
		<td><a href="javascript://close()" onClick="closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" hspace="5" alt="<? install/htdocs/sv/jsp/FileAdmin_preview.jsp/2003 ?>"></a></td>
		<td class="imcmsAdmText">&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	</table>
	</form><%
	} else { /* image */ %>
	<table border="0" cellspacing="0" cellpadding="0">
	<form action="<%= thisPage %>" target="main">
	<input type="hidden" name="frame" value="main">
	<input type="hidden" name="file" value="<%= StringEscapeUtils.escapeHtml(orgFileParam) %>">
	<tr>
		<td class="imcmsAdmText"><span class="imcmsAdmText" style="color:#ffffff;">| &nbsp; <%
		if (hasGetElementById) { %><span onDblClick="document.forms[0].zoom.selectedIndex = 3; document.forms[0].submit();"><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/16 ?></span>&nbsp;</span></td>
		<td class="imcmsAdmText">
		<select name="zoom" onChange="this.form.submit();">
			<option value="0.25"<% if (defZoom.equals("0.25")) { %> selected<% } %>>25%</option>
			<option value="0.5"<%  if (defZoom.equals("0.5")) { %> selected<% } %>>50%</option>
			<option value="0.75"<% if (defZoom.equals("0.75")) { %> selected<% } %>>75%</option>
			<option value="1.0"<%  if (defZoom.equals("1.0")) { %> selected<% } %>>100%</option>
			<option value="1.5"<%  if (defZoom.equals("1.5")) { %> selected<% } %>>150%</option>
			<option value="2.0"<%  if (defZoom.equals("2.0")) { %> selected<% } %>>200%</option>
			<option value="4.0"<%  if (defZoom.equals("4.0")) { %> selected<% } %>>400%</option>
			<option value="8.0"<%  if (defZoom.equals("8.0")) { %> selected<% } %>>800%</option>
			<option value="16.0"<% if (defZoom.equals("16.0")) { %> selected<% } %>>1600%</option>
		</select></td>
		<td><span class="imcmsAdmText" style="color:#ffffff;"> &nbsp; | &nbsp; <%
		} %><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1004/1 ?>
		<a href="<%= thisPage %>?frame=main&file=<%= Utility.escapeUrl(file) %>&border=1" target="main" style="color:#ffffff;"><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1004/2 ?></a> /
		<a href="<%= thisPage %>?frame=main&file=<%= Utility.escapeUrl(file) %>&border=0" target="main" style="color:#ffffff;"><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1004/3 ?></a> &nbsp; | &nbsp;
		<a href="javascript: closeIt();" style="color:#ffffff;"><b><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1004/4 ?></b></a> &nbsp; | &nbsp;</span></td>
	</tr>
	</form>
	</table><%
	} %></td>
</tr>
</table>


</body>
</html>

<%

/* *******************************************************************************************
 *         FRAMESET                                                                          *
 ******************************************************************************************* */

} else if (frame.equalsIgnoreCase("FRAME")) {

	if (session.getAttribute("zoom") != null) session.setAttribute("zoom", "1.0") ; // Reset to 100%

	%>
<html>
<head>
<title><%= StringEscapeUtils.escapeHtml(file) %></title>

</head>
	<%
	if (isStat) { /* Statistics Report (HTML page) */ %>
<frameset rows="35,*" border="0" framespacing="0" frameborder="NO" style="border:0">
	<frame name="topframe" src="<%= thisPage %>?frame=top&isStat=1&file=<%= Utility.escapeUrl(file) %>" marginwidth="0" marginheight="0" frameborder="NO" scrolling="NO" noresize>
	<frame name="main" src="<%= thisPage %>?frame=main&isStat=1&file=<%= Utility.escapeUrl(file) %>" marginwidth="10" marginheight="10" frameborder="NO" scrolling="AUTO" noresize>
	<noframes>
	<body bgcolor="#FFFFFF" text="#000000">
	</body>
	</noframes>
</frameset><%

	} else { /* Image File */

		%>
<frameset rows="35,*" border="0" framespacing="0" frameborder="NO" style="border:0">
	<frame name="topframe" src="<%= thisPage %>?frame=top&file=<%= Utility.escapeUrl(file) %>" marginwidth="0" marginheight="0" frameborder="NO" scrolling="NO" noresize>
	<frame name="main" src="<%= thisPage %>?frame=main&file=<%= Utility.escapeUrl(file) %>" marginwidth="0" marginheight="0" frameborder="NO" scrolling="AUTO" noresize>
	<noframes>
	<body bgcolor="#FFFFFF" text="#000000">
	</body>
	</noframes>
</frameset><%
	} %>

</html>
<%
} %>
</vel:velocity>