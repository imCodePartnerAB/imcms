<%@ page language="java"
	import="org.apache.oro.util.*, org.apache.oro.text.*, org.apache.oro.text.regex.*, org.apache.oro.text.perl.*, java.io.*, java.util.*, java.text.*, java.net.*, javax.servlet.*, javax.servlet.http.*, imcode.external.diverse.*, imcode.util.*, imcode.server.*"
%><%
/* *******************************************************************
 *           SETTINGS                                                *
 ******************************************************************* */

String IMG_PATH    = "@imcmsimageurllang@/" ; // path to buttons (with trailing /)
String imC_PATH    = "@rooturl@" ;              // "/imcms" if used - "" if not

String acceptedExt         = "HTM|HTML|VBS|JS|CSS|TXT|INC" ;
String acceptedExtReadonly = "HTM|HTML|VBS|JS|CSS|TXT|INC|JSP|ASP" ;

/* *******************************************************************
 *           INIT                                                    *
 ******************************************************************* */

String file        = request.getParameter("file") ;
String thisPage    = request.getServletPath() ;
thisPage           = imC_PATH + thisPage ;
String sTemp;

String hdPath      = request.getParameter("hdPath") ;
String fileSrc     = request.getParameter("txtField") ;
boolean doSave     = (fileSrc != null) ? true : false ;
boolean isReadonly = (request.getParameter("readonly") != null) ? true : false ;

String theSearchString = (request.getParameter("searchString") != null) ? request.getParameter("searchString") : "" ;

/* Is editable file? */
Perl5Util re                = new Perl5Util() ;
if (isReadonly) acceptedExt = acceptedExtReadonly ;
boolean isEditable          = re.match("/\\.(" + acceptedExt + ")+$/i", file) ;

/* reset file ? */

boolean resetOrg   = false ;
boolean resetSaved = false ;

if (request.getParameter("resetFile") != null) {
	if (request.getParameter("resetFile").equals("org")) {
		resetOrg   = true ;
	} else if (request.getParameter("resetFile").equals("saved")) {
		resetSaved = true ;
	}
}

/* edit template ? */

boolean isTempl  = (request.getParameter("template") != null) ? true : false ;
String templName = request.getParameter("templName") ;

/* split to "/path" and "filename.ext" */

File webRoot    = imcode.server.WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath() ;
String filePath = file.substring(0, file.lastIndexOf("/")) ;
String fileName = file.substring(file.lastIndexOf("/") + 1, file.length()) ;

/* get full path */

File fn = new File(fileName) ;
fn = new File (new File(webRoot + filePath),fn.getName()) ;

if (hdPath == null) {
	hdPath = fn.getCanonicalPath() ;
}

/* Check browser */

String uAgent = request.getHeader("USER-AGENT") ;
boolean isIE  = re.match("/(MSIE 4|MSIE 5|MSIE 5\\.5|MSIE 6|MSIE 7)/i", uAgent) ;
boolean isNS  = (re.match("/Mozilla/i", uAgent) && !re.match("/Gecko/i", uAgent) && !re.match("/MSIE/i", uAgent)) ? true : false ;
boolean isMoz = re.match("/Gecko/i", uAgent) ;
boolean isMac = re.match("/Mac/i", uAgent) ;

/* Special Character replacers */

 /* replace entities so they will render correctly.
    ie: &Aring; in code would have been Å in the editfield.
    Now &amp;Aring; in code and &Aring; in editfield. Thereby saved correctly. */
String[] fuckedUpUmling = new String[] {
 "&", "&amp;"
} ;

 /* replace back. If any of them where in a script-block, they would have been rendered and saved wrongly.
    ie: &amp;nbsp; will be replaced with &nbsp; 
String[] fuckedUpUmlingBack = new String[] {
 "&amp;", "&#$1;"
} ;
*/
/* SAVE IT */

Writer fileOut  = null ;
boolean isSaved = false ;
String sError   = "" ;

if (doSave) {
	try {
		if (!fn.exists()) {
			sError = "<b>OBS!</b> Filen fanns inte längre så den blev skapad." ;
		}
		session.setAttribute("fileSaved", fileSrc) ;
		/*for (int i = 0; i < fuckedUpUmlingBack.length; i = i + 2) {
			fileSrc = fileSrc.replaceAll(fuckedUpUmlingBack[i],fuckedUpUmlingBack[i+1]) ;
		}*/
		fileOut = new FileWriter(fn) ;
		fileOut.write(fileSrc) ;
		isSaved = true ;
	} catch (FileNotFoundException fnEx) {
		sError = "<b>FEL!</b> Filen är skrivskyddad eller går inte att spara!" ;
	} catch (IOException ioEx) {
		sError = "<b>FEL!</b> Ett fel har uppstått!" ;
	} finally {
		if (fileOut != null) {
			fileOut.close() ;
		}
	}
}

/* if Is editable file - Read file and show it */

String fileLine = "" ;
String tempStr  = "";

if (isEditable && !doSave) {

	File sf = new File(fileName) ;
	sf = new File (new File(webRoot + filePath),sf.getName()) ;

	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sf))) ;

	fileSrc = "" ;
	while ((fileLine = br.readLine()) != null) {
		tempStr = fileLine + "\n" ;
		if (tempStr.length() > 0) {
			fileSrc += tempStr ;
		}
	}
	br.close() ;


} else if (!isEditable) {

	return ;

}

/*
  :: replace all "Special Characters", or they will be saved wrongly the second time they're saved.
     or they will mess up the page...
     Replace all < & > with &lt; / &gt;
*/

for (int i = 0; i < fuckedUpUmling.length; i = i + 2) {
	fileSrc = fileSrc.replaceAll(fuckedUpUmling[i],fuckedUpUmling[i+1]) ;
}

fileSrc = fileSrc.replaceAll("<","&lt;") ;
fileSrc = fileSrc.replaceAll(">","&gt;") ;


if (!resetOrg && !resetSaved && !doSave) {
	session.setAttribute("fileOrg", fileSrc) ;
	session.setAttribute("fileSaved", fileSrc) ;
}

if (resetOrg) {
	fileSrc = (String) session.getAttribute("fileOrg") ;
} else if (resetSaved) {
	fileSrc = (String) session.getAttribute("fileSaved") ;
}

if (resetOrg || resetSaved) {
	sTemp   = (sError.equals("")) ? "" : " - " ;
	sError  = "<b>Återställd!</b>" + sTemp + sError ;
}

String sReadonly = "" ;

if (isReadonly) {
	sReadonly = " readonly onFocus=\"blur()\"" ;
}
%>
<html>
<head>
<title><? sv/jsp/FileAdmin_edit.jsp/1 ?></title>

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
			&nbsp;<%
			if (isReadonly) {
				%>Visa <%
			} else {
				%>Redigera <%
			}
			if (isTempl) {
				%>formatmall<%
			} else {
				%>textfil<%
			} %> &nbsp; </span></td>

			<td nowrap><span class="imFilename">
<? sv/jsp/FileAdmin_edit.jsp/3 ?></span></td>
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
				<td><a id="btnSearch" href="javascript://find()" onClick="findIt(document.forms[0].searchString.value);"><img src="<%= IMG_PATH %>btn_find.gif" border="0" hspace="5" alt="Sök!"></a></td>
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
				<td><input name="btnSave" id="btnSave" type="image" src="<%= IMG_PATH %>btn_save.gif" border="0" alt="Spara!"></td>
				<td class="norm">&nbsp;&nbsp;</td>
				<td><%
				if (isNS) {
					%><a href="javascript: closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" alt="Stäng och återgå!"></a><%
				} else {
					%><input name="btnClose" id="btnClose" type="image" src="<%= IMG_PATH %>btn_close.gif" border="0" alt="Stäng och återgå!" onClick="closeIt(); return false"><%
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
				%><a href="javascript: closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" alt="Stäng och återgå!"></a><%
			} else {
				%><input name="btnClose" id="btnClose" type="image" src="<%= IMG_PATH %>btn_close.gif" border="0" alt="Stäng och återgå!" onClick="closeIt(); return false"><%
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
		<textarea name="txtField" id="txtField" cols="90" rows="<%= taRows %>" class="edit" style="width:790; height:<% if (isTempl || (isMac && isIE)) { %>505<% } else { %>515<% } %>; overflow:auto" onKeyUp="checkSaved(1);"<%= sReadonly %><? sv/jsp/FileAdmin_edit.jsp/9 ?></textarea><%
			if (isTempl && !(isMac && (isNS || isIE))) { %>
		<div align="center"><span style="font: <% if (isNS) { %>10<% } else { %>9<% } %>px Verdana">Visa imCMS-taggar:&nbsp;
			<a href="javascript: imScriptCount('text');">text</a> |
			<a href="javascript: imScriptCount('image');">image</a> |
			<a href="javascript: imScriptCount('menu');">menu</a> |
			<a href="javascript: imScriptCount('include');">include</a> |
			<a href="javascript: imScriptCount('date');">date</a> |
			<a href="javascript: imScriptCount('bradgard');">#-taggar</a> |
			<a href="javascript: imScriptCount('other');">övriga</a></div><%
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