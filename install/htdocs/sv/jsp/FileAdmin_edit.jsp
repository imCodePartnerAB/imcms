<? sv/jsp/FileAdmin_edit.jsp/1001 ?>
<html>
<head>
<title>:: imCMS ::</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<STYLE TYPE="text/css">
<!--
.imHeading { font: bold 17px Verdana, Geneva, sans-serif; color:#000066; }
.imFilename { font: 10px Verdana, Geneva, sans-serif; color:#006600; }
.norm { font: 11px Verdana, Geneva, sans-serif; color:#333333; }
TT, .edit { font: 11px "Courier New", Courier, monospace, color:#000000; }
SELECT, INPUT, .small { font: 10px Verdana, Geneva, sans-serif; color:#333333; }
A:link, A:visited, A:active { color:#000099; text-decoration:none; }
-->
</STYLE>

<script language="JavaScript">
<? sv/jsp/FileAdmin_edit.jsp/2 ?>
</script>

</head>
<body bgcolor="#d6d3ce" style="border:0; margin:0" onLoad="checkSaved(0);" onResize="resizeEditField()">

<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
<tr>
	<td align="center" valign="top">
	<table border="0" cellspacing="0" cellpadding="0" width="800">
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="30" nowrap><span class="imHeading" onDblClick="toggleFontSize(this)">
			&nbsp;<? sv/jsp/FileAdmin_edit.jsp/1002 ?> &nbsp; </span></td>

			<td nowrap><span class="imFilename">
&quot;<%
			if (isTempl) {
				out.print(templName) ;
			} else {
				String fileNameToShow = file ;
				sTemp = fileNameToShow ;
				if (fileNameToShow.length() > 80) {

					fileNameToShow = sTemp.substring(0,40) + "<br>\n" ;
					fileNameToShow += sTemp.substring(40,80) + "<br>\n" ;
					fileNameToShow += sTemp.substring(80,sTemp.length()) ;

				} else if (fileNameToShow.length() > 40) {

					fileNameToShow = sTemp.substring(0,40) + "<br>\n" ;
					fileNameToShow += sTemp.substring(40,sTemp.length()) ;

				}
				out.print(fileNameToShow) ;
			} %>&quot;</span></td>
		</tr>
		</table></td>

		<td align="right"><%
		if (!isReadonly) {
			%>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr><%
			if (isIE && !isMac) { %>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
			<form name="searchForm" onSubmit="findIt(document.forms[0].searchString.value); return false">
			<tr>
				<td><input type="text" name="searchString" size="8" value="<%= theSearchString %>" style="width:50"></td>
				<td><a id="btnSearch" href="javascript://find()" onClick="findIt(document.forms[0].searchString.value);"><img src="<%= IMG_PATH %>btn_find.gif" border="0" hspace="5" alt="<? sv/jsp/FileAdmin_edit.jsp/2001 ?>"></a></td>
			</tr>
			</form>
			</table></td><%
			} %>
			<td class="norm">&nbsp;&nbsp;</td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
			<form name="resetForm">
			<input type="hidden" name="file" value="<%= file %>">
			<input type="hidden" name="hdPath" value="<%= hdPath %>">
			<input type="hidden" name="searchString" value="<%= theSearchString %>"><%
			if (isTempl) { %>
			<input type="hidden" name="template" value="1">
			<input type="hidden" name="templName" value="<%= templName %>"><%
			} %>
			<tr>
				<td class="norm"><a href="javascript://help" onClick="alert('Välj att återställa filen som den såg ut när:\n\n - Du senast sparade den.\n - När du öppnade den i detta fönster.')"><span style="color:black; text-decoration:none; cursor:help;"><? sv/jsp/FileAdmin_edit.jsp/4 ?></span></a>&nbsp;</td>
				<td class="small">
				<select name="resetFile" onChange="doReset(); return false">
					<option value=""><? sv/jsp/FileAdmin_edit.jsp/5 ?>
					<option value="saved"><? sv/jsp/FileAdmin_edit.jsp/6 ?>
					<option value="org"><? sv/jsp/FileAdmin_edit.jsp/7 ?>
				</select></td>
				<td></td>
			</tr>
			</form>
			</table></td>
			<td class="norm">&nbsp;&nbsp;</td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
			<form name="editForm" action="<%= thisPage %>" method="post" onSubmit="if (doSave()) return true; return false">
			<input type="hidden" name="file" value="<%= file %>">
			<input type="hidden" name="hdPath" value="<%= hdPath %>">
			<input type="hidden" name="searchString" value="<%= theSearchString %>"><%
			if (isTempl) { %>
			<input type="hidden" name="template" value="1">
			<input type="hidden" name="templName" value="<%= templName %>"><%
			} %>
			<tr>
				<td><input name="btnSave" id="btnSave" type="image" src="<%= IMG_PATH %>btn_save.gif" border="0" alt="<? sv/jsp/FileAdmin_edit.jsp/2002 ?>"></td>
				<td class="norm">&nbsp;&nbsp;</td>
				<td><%
				if (isNS) {
					%><a href="javascript: closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" alt="<? sv/jsp/FileAdmin_edit.jsp/2003 ?>"></a><%
				} else {
					%><input name="btnClose" id="btnClose" type="image" src="<%= IMG_PATH %>btn_close.gif" border="0" alt="<? sv/jsp/FileAdmin_edit.jsp/2004 ?>" onClick="closeIt(); return false"><%
				} %></td>
				<td class="norm">&nbsp;&nbsp;</td>
			</tr>
			</table></td>
		</tr>
		</table><%
		} else { // readonly %>
		<table border="0" cellspacing="0" cellpadding="0">
		<form name="editForm" action="<%= thisPage %>" method="post" onSubmit="if (doSave()) return true; return false">
		<tr>
			<td><%
			if (isNS) {
				%><a href="javascript: closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" alt="<? sv/jsp/FileAdmin_edit.jsp/2005 ?>"></a><%
			} else {
				%><input name="btnClose" id="btnClose" type="image" src="<%= IMG_PATH %>btn_close.gif" border="0" alt="<? sv/jsp/FileAdmin_edit.jsp/2006 ?>" onClick="closeIt(); return false"><%
			} %></td>
		</tr>
		</table><%
		} %></td>
	</tr>
	<tr>
		<td colspan="2" align="center"><img src="<%= IMG_PATH %>line_hr2.gif" width="<%
			if (isMoz || isIE) {
				%>100%<%
			} else {
				%>795<%
			} %>" height="6"></td>
	</tr>
	<tr>
		<td colspan="2" height="18" class="small">&nbsp;&nbsp;&nbsp;<span id="messId" style="color:#cc0000" onClick="loopMess(1)"><%
				if (!sError.equals("")) {
					%><%= sError %><%
				} %></span></td>
	</tr><%
			String taRows = (isTempl && !(isMac && (isNS || isIE))) ? "39" : "40" ;
			if (isIE || (isMac && isMoz)) { %>
	<tr>
		<td colspan="2"<% if (isMac) { %> <? sv/jsp/FileAdmin_edit.jsp/8 ?>
		<? sv/jsp/FileAdmin_edit.jsp/1003 ?>><%
			} else if (isMoz) { %>
	<tr>
		<td colspan="2" valign="top">
		<textarea name="txtField" id="txtField" cols="90" rows="<%= taRows %>" wrap="soft" class="edit" style="width:98%; height:<% if (isTempl) { %>500<% } else { %>510<% } %>" onKeyUp="checkSaved(1);"<%= sReadonly %>><%
			} else if (isMac && isNS) { %>
	<tr>
		<td colspan="2" align="center" class="norm">
		<textarea name="txtField" id="txtField" cols="125" rows="<%= taRows %>" wrap="soft" class="edit" onKeyUp="checkSaved(1);"<%= sReadonly %>><%
			} else { %>
	<tr>
		<td colspan="2" align="center" class="norm">
		<textarea name="txtField" id="txtField" cols="82" rows="<%= taRows %>" wrap="soft" class="edit" onKeyUp="checkSaved(1);"<%= sReadonly %>><%
			} %>
<%= fileSrc %></textarea><%
			if (isTempl && !(isMac && (isNS || isIE))) { %>
		<div align="center"><span style="font: <% if (isNS) { %>10<% } else { %>9<% } %>px Verdana"><? sv/jsp/FileAdmin_edit.jsp/1004 ?></div><%
			} %></td>
	</tr>
	</form>
	</table></td>
</tr>
</table>
<%
if (isTempl && !(isMac && (isNS || isIE))) { %>
<script language="JavaScript">
<? sv/jsp/FileAdmin_edit.jsp/10 ?>
</script><%
} %>

</body>
</html>