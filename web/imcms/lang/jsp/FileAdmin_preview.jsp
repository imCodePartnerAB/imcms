<%@ page language="java"
	import="org.apache.oro.util.*, org.apache.oro.text.*, org.apache.oro.text.regex.*, org.apache.oro.text.perl.*, java.io.*, java.util.*, java.text.*, java.net.*, javax.servlet.*, javax.servlet.http.*, imcode.external.diverse.*, imcode.util.*, imcode.server.*"
%><%
/* *******************************************************************
 *           SETTINGS                                                *
 ******************************************************************* */

String acceptedExtPattern = "/" +
	"(\\.(GIF|JPE?G|PNG|BMP|AVI|MPE?G|HTML?|CSS|JS|VBS|TXT|JSP|ASP|FRAG)+$)" +
	"|(\\.LOG+)" +
	"/i" ;

String IMG_PATH   = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/" ; // path to buttons (with trailing /)

/* *******************************************************************
 *           INIT                                                    *
 ******************************************************************* */

String file       = request.getParameter("file") ;

String frame      = (request.getParameter("frame") != null) ? request.getParameter("frame") : "FRAME" ;
String thisPage = request.getContextPath() + request.getServletPath();

String zoom       = "" ;
String defZoom    = "1.0" ;

boolean isStat    = (request.getParameter("isStat") != null) ? true : false ;

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
	hasBorder       = (request.getParameter("border").equals("1")) ? true : false ;
	session.setAttribute("border", request.getParameter("border")) ;
} else if (session.getAttribute("border") != null) {
	border          = (session.getAttribute("border").equals("1")) ? " border=\"1\"" : "" ;
	hasBorder       = (session.getAttribute("border").equals("1")) ? true : false ;
}

File webRoot    = imcode.server.WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath() ;
String filePath = file.substring(0, file.lastIndexOf("/")) ;
String fileName = file.substring(file.lastIndexOf("/") + 1, file.length()) ;

/* Is image? */
Perl5Util re       = new Perl5Util() ;
boolean isImage    = re.match(acceptedExtPattern, file) ;

/* Check browser */

String uAgent = request.getHeader("USER-AGENT") ;
boolean hasDocumentAll  = re.match("/(MSIE 5\\.5|MSIE 6|MSIE 7)/i", uAgent) ;
boolean hasDocumentLayers  = (re.match("/Mozilla/i", uAgent) && !re.match("/Gecko/i", uAgent)) ? true : false ;
boolean hasGetElementById = re.match("/Gecko/i", uAgent) ;
boolean isMac = re.match("/Mac/i", uAgent) ;

/* if Stat-Report - Read file and show it */

if (isStat && frame.equalsIgnoreCase("MAIN")) {
    File sf = new File(fileName) ;
    sf = new File (new File(webRoot + filePath),sf.getName()) ;
    //String statSrc = ReadTextFile.getFile(sf);

    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sf))) ;

    String statSrc = "" ;
    String fileLine = "" ;
    StringBuffer strbuf = new StringBuffer();
    while ((fileLine = br.readLine())!= null) {
        strbuf.append(fileLine).append( "\n") ;
    }
    statSrc = strbuf.toString() ;
    br.close() ;

    statSrc = statSrc.replaceAll("<head>","<head>\n\n<base target=\"_blank\">");	/* add some buttons in some browsers */
	
	String theButtons = "" ;
	boolean hasInlineButtons = false ;
	
	theButtons = "<table border=0 bgcolor=\"#d6d3ce\" align=\"right\">\n<tr>" ;
	if (hasGetElementById && !hasDocumentAll && !isMac) {
		hasInlineButtons = true ;
		theButtons += "\n	<td><a href=\"javascript: find(); return false\"><img align=\"absmiddle\" src=\"" + IMG_PATH + "btn_find.gif\" border=\"0\" alt=\"S�k!\"></a></td>" ;
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

/* Get size */

File fn = new File(fileName) ;
fn = new File (new File(webRoot + filePath),fn.getName()) ;

String image_ref = fn.getCanonicalPath() ;

ImageFileMetaData imagefile = new ImageFileMetaData(new File(image_ref)) ;
int width = imagefile.getWidth() ;
int height = imagefile.getHeight() ;

String size  = "" ;
double iSize = 0 ;

try {
	iSize = (double) fn.length() ;
	if (iSize >= 1024) {
		iSize = iSize / 1024 ;
		DecimalFormat df = new DecimalFormat("#.#") ;
		size  = (String) df.format(iSize) ;
		size  = ", " + size.replaceAll(",", ".") + "kB" ;
	} else {
		size  = ", " + fn.length() + " bytes" ;
	}
} catch ( NumberFormatException ex ) {
	// ignore
}

//out.print("fn.length(): " + fn.length() + "<br><br>iSize: " + iSize + "<br><br>size: " + size) ;

/* *******************************************************************************************
 *         FRAME MAIN                                                                        *
 ******************************************************************************************* */

if (frame.equalsIgnoreCase("MAIN")) { %>
<html>
<head>
<title></title>



</head>
<body marginwidth="10" marginheight="10" leftmargin="10" topmargin="10" bgcolor="#ffffff">

<div align="center"><%
if (isImage) {
	%><div style="padding: 5 0 <%
	if (hasBorder) {
		%>5<% 
	} else {
		%>6<% 
	} %> 0; font: 10px Verdana, Geneva, sans-serif; color:#999999;">&quot;<%= request.getContextPath() + file %>&quot;<%
	if (width > 0 && height > 0 && !size.equals("")) {
		%> (<%
		if (width > 0 && height > 0) {
			%><%= width + "x" + height %><%
		}
		if (!size.equals("")) {
			%><%= size %><%
		} %>)<%
	} %></div><img name="theImg" id="theImg" src="<%= request.getContextPath() + file %>"<%= border + zoom %>><%
} else {
	%><%
} %></div>

</body>
</html><%

/* *******************************************************************************************
 *         FRAME TOP                                                                         *
 ******************************************************************************************* */

} else if (frame.equalsIgnoreCase("TOP")) { %>
<html>
<head>
<title></title>



<STYLE TYPE="text/css">
<!-- 
.imHeading { font: bold 17px Verdana, Geneva, sans-serif; color:#000066; }
.imFilename { font: bold italic 10px Verdana, Geneva, sans-serif; color:#009900; }
.norm { font: 11px Verdana, Geneva, sans-serif; color:#333333; }
SELECT, INPUT { font: 10px Verdana, Geneva, sans-serif; color:#333333; }
A:link, A:visited, A:active { color:#000099; text-decoration:none; }
-->
</STYLE>

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
		for (i = 0; i <= n && (found = txt.findText(str)) != false; i++) {
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
<body bgcolor="#d6d3ce" style="border:0">

<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%" style="border-bottom: 1px solid #828482">
<tr>
	<td nowrap><span class="imHeading">
	&nbsp;<%
	if (isStat) {
		%><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1002/1 ?><%
	} else {
		%><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1002/2 ?><% 
	} %> &nbsp;</span></td>
	
	<td align="right"><%
	if (isStat) {
		%>
	<table border="0" cellspacing="0" cellpadding="0">
	<form onSubmit="findIt(document.forms[0].searchString.value); return false">
	<tr><%
		if (hasDocumentAll || hasDocumentLayers) { %>
		<td class="norm"><input type="text" name="searchString" size="15" value="" class="norm" style="width:100"></td>
		<td><a id="btnSearch" href="javascript://find()" onClick="findIt(document.forms[0].searchString.value);"><img src="<%= IMG_PATH %>btn_find.gif" border="0" hspace="5" alt="<? install/htdocs/sv/jsp/FileAdmin_preview.jsp/2001 ?>"></a></td><%
		} %>
		<td class="norm">&nbsp;&nbsp;</td><%
		if (!isMac) { %>
		<td><a href="javascript://print()" onClick="top.frames.main.focus(); top.frames.main.print();"><img src="<%= IMG_PATH %>btn_print.gif" border="0" hspace="5" alt="<? install/htdocs/sv/jsp/FileAdmin_preview.jsp/2002 ?>"></a></td><%
		} %>
		<td><a href="javascript://close()" onClick="closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" hspace="5" alt="<? install/htdocs/sv/jsp/FileAdmin_preview.jsp/2003 ?>"></a></td>
		<td class="norm">&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	</form>
	</table><%
	} else { /* image */ %>
	<table border="0" cellspacing="0" cellpadding="0">
	<form action="<%= thisPage %>" target="main">
	<input type="hidden" name="frame" value="main">
	<input type="hidden" name="file" value="<%= file %>">
	<tr>
		<td class="norm">| &nbsp; <%
		if (hasDocumentAll) { %><span onDblClick="document.forms[0].zoom.selectedIndex = 3; document.forms[0].submit();"><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/16 ?></span>&nbsp;</td>
		<td class="norm">
		<select name="zoom" onChange="this.form.submit();">
			<option value="0.25"<% if (defZoom.equals("0.25")) { %> selected<% } %>>25%
			<option value="0.5"<%  if (defZoom.equals("0.5")) { %> selected<% } %>>50%
			<option value="0.75"<% if (defZoom.equals("0.75")) { %> selected<% } %>>75%
			<option value="1.0"<%  if (defZoom.equals("1.0")) { %> selected<% } %>>100%
			<option value="1.5"<%  if (defZoom.equals("1.5")) { %> selected<% } %>>150%
			<option value="2.0"<%  if (defZoom.equals("2.0")) { %> selected<% } %>>200%
			<option value="4.0"<%  if (defZoom.equals("4.0")) { %> selected<% } %>>400%
			<option value="8.0"<%  if (defZoom.equals("8.0")) { %> selected<% } %>>800%
			<option value="16.0"<% if (defZoom.equals("16.0")) { %> selected<% } %>>1600%
		</select></td>
		<td class="norm"> &nbsp; | &nbsp; <%
		} %><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1004/1 ?>
		<a href="<%= thisPage %>?frame=main&file=<%= file %>&border=1" target="main"><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1004/2 ?></a> /
		<a href="<%= thisPage %>?frame=main&file=<%= file %>&border=0" target="main"><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1004/3 ?></a> &nbsp; | &nbsp;
		<a href="javascript: closeIt();"><b><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/1004/4 ?></b></a> &nbsp; | &nbsp;</td>
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
<title><? install/htdocs/sv/jsp/FileAdmin_preview.jsp/26 ?></title>

</head>
	<%
	if (isStat) { /* Statistics Report (HTML page) */ %>
<frameset rows="30,*" border="0" framespacing="0" frameborder="NO" style="border:0">
	<frame name="topframe" src="<%= thisPage %>?frame=top&isStat=1&file=<%= file %>" marginwidth="0" marginheight="0" frameborder="NO" scrolling="NO" noresize>
	<frame name="main" src="<%= thisPage %>?frame=main&isStat=1&file=<%= file %>" marginwidth="10" marginheight="10" frameborder="NO" scrolling="AUTO" noresize>
	<noframes>
	<body bgcolor="#FFFFFF" text="#000000">
	</body>
	</noframes>
</frameset><%
		
	} else { /* Image File */
		
		%>
<frameset rows="30,*" border="0" framespacing="0" frameborder="NO" style="border:0">
	<frame name="topframe" src="<%= thisPage %>?frame=top&file=<%= file %>" marginwidth="0" marginheight="0" frameborder="NO" scrolling="NO" noresize>
	<frame name="main" src="<%= thisPage %>?frame=main&file=<%= file %>" marginwidth="0" marginheight="0" frameborder="NO" scrolling="AUTO" noresize>
	<noframes>
	<body bgcolor="#FFFFFF" text="#000000">
	</body>
	</noframes>
</frameset><%
	} %>

</html>
<%
} %>