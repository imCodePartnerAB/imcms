<? sv/jsp/FileAdmin_preview.jsp/1 ?><head><? sv/jsp/FileAdmin_preview.jsp/2 ?><head><? sv/jsp/FileAdmin_preview.jsp/3 ?><base target=\"_blank\"><? sv/jsp/FileAdmin_preview.jsp/4 ?><table border=0 bgcolor=\"#d6d3ce\" align=\"right\"><? sv/jsp/FileAdmin_preview.jsp/5 ?><tr><? sv/jsp/FileAdmin_preview.jsp/6 ?>	<td><a href=\"javascript: find(); return false\"><img align=\"absmiddle\" src=\"" + IMG_PATH + "btn_find.gif\" border=\"0\" alt=\"Sök!\"></a></td><? sv/jsp/FileAdmin_preview.jsp/7 ?>	<td><a href=\"javascript: print(); return false\"><img src=\"" + IMG_PATH + "btn_print.gif\" border=\"0\" alt=\"Skriv ut!!\"></a></td><? sv/jsp/FileAdmin_preview.jsp/8 ?></tr><? sv/jsp/FileAdmin_preview.jsp/9 ?></table><? sv/jsp/FileAdmin_preview.jsp/10 ?><body><? sv/jsp/FileAdmin_preview.jsp/11 ?><body><? sv/jsp/FileAdmin_preview.jsp/12 ?>
<html>
<head>
<title></title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

</head>
<body marginwidth="10" marginheight="10" leftmargin="10" topmargin="10" bgcolor="#ffffff">

<div align="center"><%
if (isImage) {
	%><div style="padding: 5 0 <%
	if (hasBorder) {
		%>5<% 
	} else {
		%>6<% 
	} %> 0; font: 10px Verdana, Geneva, sans-serif; color:#999999;"><? sv/jsp/FileAdmin_preview.jsp/13 ?></div><? sv/jsp/FileAdmin_preview.jsp/1001 ?><? sv/jsp/FileAdmin_preview.jsp/14 ?></div>

</body>
</html><%

/* *******************************************************************************************
 *         FRAME TOP                                                                         *
 ******************************************************************************************* */

} else if (frame.equalsIgnoreCase("TOP")) { %>
<html>
<head>
<title></title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

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
<? sv/jsp/FileAdmin_preview.jsp/15 ?>
</script>

</head>
<body bgcolor="#d6d3ce" style="border:0">

<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%" style="border-bottom: 1px solid #828482">
<tr>
	<td nowrap><span class="imHeading">
	&nbsp;<? sv/jsp/FileAdmin_preview.jsp/1002 ?> &nbsp;</span></td>
	
	<td align="right"><%
	if (isStat) {
		%>
	<table border="0" cellspacing="0" cellpadding="0">
	<form onSubmit="findIt(document.forms[0].searchString.value); return false">
	<tr><%
		if (isIE || isNS) { %>
		<td class="norm"><input type="text" name="searchString" size="15" value="" class="norm" style="width:100"></td>
		<td><a id="btnSearch" href="javascript://find()" onClick="findIt(document.forms[0].searchString.value);"><img src="<%= IMG_PATH %>btn_find.gif" border="0" hspace="5" alt="Sök!"></a></td><%
		} %>
		<td class="norm">&nbsp;&nbsp;</td><%
		if (!isMac) { %>
		<td><a href="javascript://print()" onClick="top.frames.main.focus(); top.frames.main.print();"><img src="<%= IMG_PATH %>btn_print.gif" border="0" hspace="5" alt="Skriv ut!"></a></td><%
		} %>
		<td><a href="javascript://close()" onClick="closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" hspace="5" alt="Stäng och återgå!"></a></td>
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
		<td class="norm"><? sv/jsp/FileAdmin_preview.jsp/1003 ?><span onDblClick="document.forms[0].zoom.selectedIndex = 3; document.forms[0].submit();"><? sv/jsp/FileAdmin_preview.jsp/16 ?></span>&nbsp;</td>
		<td class="norm">
		<select name="zoom" onChange="this.form.submit();">
			<option value="0.25"<% if (defZoom.equals("0.25")) { %> <? sv/jsp/FileAdmin_preview.jsp/17 ?>
			<option value="0.5"<%  if (defZoom.equals("0.5")) { %> <? sv/jsp/FileAdmin_preview.jsp/18 ?>
			<option value="0.75"<% if (defZoom.equals("0.75")) { %> <? sv/jsp/FileAdmin_preview.jsp/19 ?>
			<option value="1.0"<%  if (defZoom.equals("1.0")) { %> <? sv/jsp/FileAdmin_preview.jsp/20 ?>
			<option value="1.5"<%  if (defZoom.equals("1.5")) { %> <? sv/jsp/FileAdmin_preview.jsp/21 ?>
			<option value="2.0"<%  if (defZoom.equals("2.0")) { %> <? sv/jsp/FileAdmin_preview.jsp/22 ?>
			<option value="4.0"<%  if (defZoom.equals("4.0")) { %> <? sv/jsp/FileAdmin_preview.jsp/23 ?>
			<option value="8.0"<%  if (defZoom.equals("8.0")) { %> <? sv/jsp/FileAdmin_preview.jsp/24 ?>
			<option value="16.0"<% if (defZoom.equals("16.0")) { %> <? sv/jsp/FileAdmin_preview.jsp/25 ?>
		</select></td>
		<td class="norm"> &nbsp; <? sv/jsp/FileAdmin_preview.jsp/1004 ?> &nbsp;</td>
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
<title><? sv/jsp/FileAdmin_preview.jsp/26 ?></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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