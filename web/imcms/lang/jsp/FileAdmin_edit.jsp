<%@ page language="java"
	import="org.apache.oro.util.*, org.apache.oro.text.*, org.apache.oro.text.regex.*, org.apache.oro.text.perl.*, java.io.*, java.util.*, java.text.*, java.net.*, javax.servlet.*, javax.servlet.http.*, imcode.external.diverse.*, imcode.util.*, imcode.server.*"
%><%
/* *******************************************************************
 *           SETTINGS                                                *
 ******************************************************************* */

String acceptedExtPattern         = "/\\.(HTML?|CSS|JS|VBS|TXT|INC|JSP|ASP|FRAG)+$/i" ;
String acceptedExtPatternReadonly = "/" +
	"(\\.(HTML?|CSS|JS|VBS|TXT|INC|JSP|ASP|FRAG)+$)" +
	"|(\\.LOG+)" +
	"/i" ;

String IMG_PATH   = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ; // path to buttons (with trailing /)

/* *******************************************************************
 *           INIT                                                    *
 ******************************************************************* */

String file        = request.getParameter("file") ;
String thisPage = request.getContextPath() + request.getServletPath();
String sTemp;

String hdPath      = request.getParameter("hdPath") ;
String fileSrc     = request.getParameter("txtField") ;
boolean doSave     = (fileSrc != null) ? true : false ;
boolean isReadonly = (request.getParameter("readonly") != null) ? true : false ;

String theSearchString = (request.getParameter("searchString") != null) ? request.getParameter("searchString") : "" ;

/* Is editable file? */
Perl5Util re = new Perl5Util() ;

if (isReadonly) acceptedExtPattern = acceptedExtPatternReadonly ;

boolean isEditable = re.match(acceptedExtPattern, file) ;

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

/* get full path */

File fn = new File(webRoot, file) ;

if (hdPath == null) {
	hdPath = fn.getCanonicalPath() ;
}

/* Check browser */

String uAgent = request.getHeader("USER-AGENT") ;
boolean hasDocumentAll  = re.match("/(MSIE 4|MSIE 5|MSIE 5\\.5|MSIE 6|MSIE 7)/i", uAgent) ;
boolean hasDocumentLayers  = (re.match("/Mozilla/i", uAgent) && !re.match("/Gecko/i", uAgent) && !re.match("/MSIE/i", uAgent)) ? true : false ;
boolean hasGetElementById = re.match("/Gecko/i", uAgent) ;
boolean isMac = re.match("/Mac/i", uAgent) ;

/* Special Character replacers */

 /* replace entities so they will render correctly.
    ie: &Aring; in code would have been ล in the editfield.
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
			sError = "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1001/1 ?>" ;
		}
		session.setAttribute("fileSaved", fileSrc) ;
		/*for (int i = 0; i < fuckedUpUmlingBack.length; i = i + 2) {
			fileSrc = fileSrc.replaceAll(fuckedUpUmlingBack[i],fuckedUpUmlingBack[i+1]) ;
		}*/
		fileOut = new FileWriter(fn) ;
		fileOut.write(fileSrc) ;
		isSaved = true ;
	} catch (FileNotFoundException fnEx) {
		sError = "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1001/2 ?>" ;
	} catch (IOException ioEx) {
		sError = "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1001/3 ?>" ;
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

	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fn))) ;

	fileSrc = "" ;
	while ((fileLine = br.readLine()) != null) {
		tempStr = fileLine + "\n" ;
		if (tempStr.length() > 0) {
			fileSrc += tempStr ;
		}
	}
	br.close() ;


} else if (!isEditable) {
	
	out.print("The file is not editable!") ;
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
	sError  = "<b>Recovered!</b>" + sTemp + sError ;
}

String sReadonly = "" ;

if (isReadonly) {
	sReadonly = " readonly onFocus=\"blur()\"" ;
}



/* *******************************************************************************************
 *         Help window                                                                       *
 ******************************************************************************************* */

boolean isHelpWin = (request.getParameter("show") != null && request.getParameter("show").equals("help")) ? true : false ;

if (isHelpWin) { %>
<html>
<head>
<title>:: imCMS ::</title>



<STYLE TYPE="text/css">
<!--
.imHeading { font: bold 17px Verdana, Geneva, sans-serif; color:#000066; }
.imFilename { font: 10px Verdana, Geneva, sans-serif; color:#006600; }
.norm { font: 11px Verdana, Geneva, sans-serif; color:#333333; }
TT, .edit { font: 11px "Courier New", Courier, monospace, color:#000000; }
SELECT, INPUT, .small { font: 10px Verdana, Geneva, sans-serif; color:#333333; }
A:link, A:visited, A:active { color:#000099; text-decoration:none; }
B { font-weight:bold; }
-->
</STYLE>

</head>
<body>

<table border="0" cellspacing="0" cellpadding="0" width="90%" align="center">
<tr>
	<td class="imHeading">Tips!</td>
</tr>
<tr>
	<td colspan="2" align="center"><img src="<%= IMG_PATH %>line_hr2.gif" width="<%
	if (hasGetElementById || hasDocumentAll) {
		%>100%<%
	} else {
		%>380<%
	} %>" height="6" vspace="5"></td>
</tr>
<tr>
	<td class="norm"><%
	if (!hasDocumentLayers && !isMac) { %>
	<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/6 ?> <i><%
		if (isReadonly) {
			%><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/7 ?> <%
		} else {
			%><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/8 ?> <%
		}
		if (isTempl) {
			%><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/9 ?><%
		} else {
			%><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/10 ?><%
		} %></i> <? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/11 ?><%
		if (hasDocumentAll) {
		%> <? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/12 ?><%
		} %><br><br><%
	}
	if (!isReadonly) { %>
	<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/13 ?><%
		if (hasDocumentAll && !isMac) { %>
	<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/14 ?><%
		}
	} %></td>
</tr>
</table>


</body>
</html><%
	return ;
}
%>
<html>
<head>
<title>:: imCMS ::</title>



<STYLE TYPE="text/css">
<!--
.imHeading { font: bold 17px Verdana, Geneva, sans-serif; color:#000066; }
.imFilename { font: 10px Verdana, Geneva, sans-serif; color:#006600; }
.norm { font: 11px Verdana, Geneva, sans-serif; color:#333333; }
TT, .edit { font: 11px "Courier New", Courier, monospace, color:#000000; }
SELECT, INPUT, .small { font: 10px Verdana, Geneva, sans-serif; color:#333333; }
A:link, A:visited, A:active { color:#000099; text-decoration:none; }
B { font-weight:bold; }
-->
</STYLE>

<script language="JavaScript">
<!--
function closeIt() {
	window.close();
	if (parent.opener) parent.opener.focus();
}

var hasGetElementById = (document.getElementById);
var hasDocumentAll  = (document.all);
var hasDocumentLayers  = (document.layers);

var win = window;
var n   = 0;

function findIt(str) {
	var txt, i, found;
	if (hasGetElementById && str != "") {
		txt = win.document.getElementById("txtField").createTextRange();
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
		document.forms.editForm.searchString.value  = str;
		document.forms.resetForm.searchString.value = str;
	}
	if (hasDocumentAll && hasGetElementById) document.getElementById("btnSearch").setActive(); // focus btn - for [Enter] support
}

function doSave() {
	if (confirm("<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/1 ?>")) {
		return true;
	}
	return false;
}

function doReset() {
	var f = document.forms.resetForm;
	var theSel = f.resetFile.options[f.resetFile.selectedIndex].value;
	if (theSel != "") {
		var theText = (theSel == "org") ? "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/2 ?>" : "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/3 ?>";
		if (confirm("<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/4 ?>" + theText + "\n\n<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2/5 ?>")) {
			f.submit();
		} else {
			f.resetFile.selectedIndex = 0;
		}
	}
	return false;
}

/* onLoad-function */

function checkSaved(ch) {
	if (hasGetElementById) {<%
		if (!isReadonly) { %>
		var isSaved = <%= isSaved %>;
		var el = document.getElementById("btnSave");
		if (isSaved && !ch) {
			el.style.cursor = "default";
			el.style.filter = "progid:DXImageTransform.Microsoft.BasicImage( Rotation=0,Mirror=0,Invert=1,XRay=0,Grayscale=1,Opacity=0.60)";
			el.disabled = 1;
		} else {
			el.style.cursor = (hasDocumentAll) ? "hand" : "pointer";
			el.style.filter = "";
			el.disabled = 0;
		}<%
		} %>
		resizeEditField();
	}
}

function resizeEditField() {
	if (hasGetElementById) {
		var elEdit = document.getElementById("txtField");
		var availW = 0;
		var availH = 0;
		if (hasDocumentAll) {
			availW = parseInt(document.body.offsetWidth);
			availH = parseInt(document.body.offsetHeight);
		} else {
			availW = parseInt(innerWidth);
			availH = parseInt(innerHeight);
		}
		if (availW > 0) elEdit.style.width = availW - 13;
		if (availH > 0) elEdit.style.height = availH - 62;
	}
}

var errMess = <% if (!sError.equals("")) { %>1<% } else { %>2<% } %>;

function toggleFontSize() {
	if (hasGetElementById) {
		var el = document.getElementById("txtField");
		if (window.event) {
			if (window.event.shiftKey) {
				el.style.fontFamily = (el.style.fontFamily.indexOf("Courier") != -1) ? "Verdana, Geneva, sans-serif" : "'Courier New', Courier, monospace";
				return;
			}
		}
		var theSize = (el.style.fontSize) ? parseInt(el.style.fontSize) : 11;
		if (theSize == 11) {
			el.style.fontSize = "13px";
		} else if (theSize == 13) {
			el.style.fontSize = "15px";
		} else if (theSize == 15) {
			el.style.fontSize = "17px";
		} else if (theSize == 17) {
			el.style.fontSize = "10px";
		} else if (theSize == 10) {
			el.style.fontSize = "11px";
		}
	}
}

function popWinOpen(winW,winH,sUrl,sName,iResize,iScroll) {
	if (screen) {
		if ((screen.height - winH) < 150) {
			var winX = (screen.width - winW) / 2;
			var winY = 0;
		} else {
			var winX = (screen.width - winW) / 2;
			var winY = (screen.height - winH) / 2;
		}
		var popWindow = window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH + ",top=" + winY + ",left=" + winX + "");
		if (popWindow) popWindow.focus();
	} else {
		window.open(sUrl,sName,"resizable=yes,menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH);
	}
}
//-->
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
				%><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1002/1 ?> <%
			} else {
				%><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1002/2 ?> <%
			}
			if (isTempl) {
				%><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1002/3 ?><%
			} else {
				%><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1002/4 ?><%
			} %> &nbsp; </span></td>

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
			if (hasDocumentAll && !isMac) { %>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
			<form name="searchForm" onSubmit="findIt(document.forms[0].searchString.value); return false">
			<tr>
				<td><input type="text" name="searchString" size="8" value="<%= theSearchString %>" style="width:50"></td>
				<td><a id="btnSearch" href="javascript://find()" onClick="findIt(document.forms[0].searchString.value);"><img src="<%= IMG_PATH %>btn_find.gif" border="0" hspace="5" alt="<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2001 ?>"></a></td>
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
				<td class="norm"><a href="javascript://help" onClick="alert('Vไlj att ๅterstไlla filen som den sๅg ut nไr:\n\n - Du senast sparade den.\n - Nไr du ๖ppnade den i detta f๖nster.')"><span style="color:black; text-decoration:none; cursor:help;"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/4 ?></span></a>&nbsp;</td>
				<td class="small">
				<select name="resetFile" onChange="doReset(); return false">
					<option value=""><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/5 ?>
					<option value="saved"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/6 ?>
					<option value="org"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/7 ?>
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
				<td><input name="btnSave" id="btnSave" type="image" src="<%= IMG_PATH %>btn_save.gif" border="0" alt="<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2002 ?>"></td>
				<td class="norm">&nbsp;&nbsp;</td>
				<td><%
				if (hasDocumentLayers) {
					%><a href="javascript: closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" alt="<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2003 ?>"></a><%
				} else {
					%><input name="btnClose" id="btnClose" type="image" src="<%= IMG_PATH %>btn_close.gif" border="0" alt="<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2004 ?>" onClick="closeIt(); return false"><%
				} %></td>
				<td class="norm"><a href="javascript: popWinOpen(400,200,'<%= thisPage %>?show=help<%
			if (isTempl) {
				%>&template=1<%
			}
			if (isReadonly) {
				%>&readonly=1<%
			} %>&file=<%= file %>&templName=<%= templName %>','FileAdminEditHelp',0,0)"><img src="../htmleditor/images/btn_help_subject.gif" width="16" height="16" alt="Tips!" border="0" hspace="10"></a></td>
			</tr>
			</table></td>
		</tr>
		</table><%
		} else { // readonly %>
		<table border="0" cellspacing="0" cellpadding="0">
		<form name="editForm" action="<%= thisPage %>" method="post" onSubmit="if (doSave()) return true; return false">
		<tr>
			<td><%
			if (hasDocumentLayers) {
				%><a href="javascript: closeIt();"><img src="<%= IMG_PATH %>btn_close.gif" border="0" alt="<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2005 ?>"></a><%
			} else {
				%><input name="btnClose" id="btnClose" type="image" src="<%= IMG_PATH %>btn_close.gif" border="0" alt="<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/2006 ?>" onClick="closeIt(); return false"><%
			} %></td>
			<td class="norm"><a href="javascript: popWinOpen(400,200,'<%= thisPage %>?show=help<%
			if (isTempl) {
				%>&template=1<%
			}
			if (isReadonly) {
				%>&readonly=1<%
			} %>&file=<%= file %>&templName=<%= templName %>','FileAdminEditHelp',0,0)"><img src="../htmleditor/images/btn_help_subject.gif" width="16" height="16" alt="Tips!" border="0" hspace="10"></a></td>
		</tr>
		</table><%
		} %></td>
	</tr>
	<tr>
		<td colspan="2" align="center"><img src="<%= IMG_PATH %>line_hr2.gif" width="<%
			if (hasGetElementById || hasDocumentAll) {
				%>100%<%
			} else {
				%>795<%
			} %>" height="6"></td>
	</tr>
	<tr>
		<td colspan="2" height="18" class="small"><span style="font: <% if (hasDocumentLayers) { %>10<% } else { %>9<% } %>px Verdana"><%
			if (isTempl && !(isMac && (hasDocumentLayers || hasDocumentAll))) { %>
		&nbsp;&nbsp;<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1004/1 ?>&nbsp;
		<a href="javascript: imScriptCount('text');"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1004/2 ?></a> |
		<a href="javascript: imScriptCount('image');"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1004/3 ?></a> |
		<a href="javascript: imScriptCount('menu');"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1004/4 ?></a> |
		<a href="javascript: imScriptCount('include');"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1004/5 ?></a> |
		<a href="javascript: imScriptCount('date');"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1004/6 ?></a> |
		<a href="javascript: imScriptCount('bradgard');"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1004/7 ?></a> |
		<a href="javascript: imScriptCount('other');"><? install/htdocs/sv/jsp/FileAdmin_edit.jsp/1004/8 ?></a><%
			} else {
				out.print("&nbsp;") ;
			}
			if (!sError.equals("")) {
				%><div style="color:#cc0000"><%= sError %></div><%
			} %></span></td>
	</tr><%
			String taRows = (isTempl && !(isMac && (hasDocumentLayers || hasDocumentAll))) ? "39" : "40" ;
			if (hasDocumentAll || (isMac && hasGetElementById)) { %>
	<tr>
		<td colspan="2" align="center">
		<textarea name="txtField" id="txtField" cols="90" rows="<%= taRows %>" class="edit" style="width:790; height:<% if (isTempl || (isMac && hasDocumentAll)) { %>505<% } else { %>515<% } %>; overflow:auto" onKeyUp="checkSaved(1);"<%= sReadonly %>><%
			} else if (hasGetElementById) { %>
	<tr>
		<td colspan="2" align="center" valign="top">
		<textarea name="txtField" id="txtField" cols="90" rows="<%= taRows %>" wrap="soft" class="edit" style="width:98%; height:<% if (isTempl) { %>500<% } else { %>510<% } %>" onKeyUp="checkSaved(1);"<%= sReadonly %>><%
			} else if (isMac && hasDocumentLayers) { %>
	<tr>
		<td colspan="2" align="center" class="norm">
		<textarea name="txtField" id="txtField" cols="125" rows="<%= taRows %>" wrap="soft" class="edit" onKeyUp="checkSaved(1);"<%= sReadonly %>><%
			} else { %>
	<tr>
		<td colspan="2" align="center" class="norm">
		<textarea name="txtField" id="txtField" cols="82" rows="<%= taRows %>" wrap="soft" class="edit" onKeyUp="checkSaved(1);"<%= sReadonly %>><%
			} %>
<%= fileSrc %></textarea></td>
	</tr>
	</form>
	</table></td>
</tr>
</table>
<%
if (isTempl && !(isMac && (hasDocumentLayers || hasDocumentAll))) { %>
<script language="JavaScript">
<!--
function imScriptCount(imType) {
	var hits,arr1,arr2;
	var retStr = "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/8 ?>\nจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจ\n";
	if (hasDocumentLayers) retStr += "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/9 ?>\nจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจ\n";
	var head_1_a = ":: ";
	var head_1_b = " ::";
	var head_2_a = "        - ";
	var head_2_b = " -";
	var re1 = /<\?imcms\:text[^\?]*?\?>/gi;
	var re2 = /<\?imcms\:image[^\?]*?\?>/gi;
	var re3 = /<\?imcms\:menu\s+[^\?]*?\?>/gi;
	var re4 = /<\?imcms\:include[^\?]*?\?>/gi;
	var re5 = /#[A-Z0-9_-]+?#/gi;
	var re6 = /<\?imcms\:datetime[^\?]*?\?>/gi;
	var re7 = /(<\?imcms\:[^\?]*?\?>)|(<\!--\/?IMSCRIPT-->)/gi;
	var re72 = /imcms\:(text|image|menu|include|datetime)/gi; // not used - inline

	var cont = document.forms.editForm.txtField.value;
	switch (imType) {
		case 'text':
			if (re1.test(cont)) {
				hits = cont.match(re1);
				//hits = hits.sort();
				retStr += head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/10 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
					hits    = fixImCmsTags(hits, "codeOrder");
					retStr += hits.join("\n");
					retStr += "\n\n" + head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/12 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "numOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/1 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'image':
			if (re2.test(cont)) {
				hits = cont.match(re2);
				//hits = hits.sort();
				retStr += head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/13 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
					hits    = fixImCmsTags(hits, "codeOrder");
					retStr += hits.join("\n");
					retStr += "\n\n" + head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/12 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "numOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/2 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'menu':
			if (re3.test(cont)) {
				hits = cont.match(re3);
				//hits = hits.sort();
				retStr += head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/14 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/11 ?>:\n\n";
					hits    = fixImCmsTags(hits, "codeOrder");
					retStr += hits.join("\n");
					retStr += "\n\n" + head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/12 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "numOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/3 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'include':
			if (re4.test(cont)) {
				hits = cont.match(re4);
				//hits = hits.sort();
				retStr += head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/15 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "codeOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/4 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'bradgard':
			if (re5.test(cont)) {
				hits = cont.match(re5);
				retStr += head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/16 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "codeOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/5 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'date':
			if (re6.test(cont)) {
				hits = cont.match(re6);
				retStr += head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/17 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "codeOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/6 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'other':
			if (re7.test(cont)) {
				hits = cont.match(re7);
				retStr += head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/18 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
				}
				var arrTemp = new Array();
				var iCount = 0;
				for (var i = 0; i < hits.length; i++) {
					hits[i] = hits[i].replace(/\s+/g, " ");
					re      = new RegExp(":(text|image|menu|include|datetime)[\\s\\?]", "g");
					if (!re.test(hits[i])) {
						arrTemp[iCount] = hits[i];
						iCount++;
					}
				}
				arrTemp = fixImCmsTags(arrTemp, "codeOrder");
				retStr += arrTemp.join("\n");
			} else {
				retStr = head_1_a + "<? install/htdocs/sv/jsp/FileAdmin_edit.jsp/10/7 ?>" + head_1_b;
			}
			alert(retStr);
		break;
	}
}

function fixImCmsTags(theArray, theType) {
	var theArr = theArray;
	var sCount = "";
	var re1_pa = /\s+/g;
	var re1_to = " ";

	var sTemp;

	/* In original order */
	if (theType == "codeOrder") {
		for (var i = 0; i < theArr.length; i++) {
			/* replace linebreaks */
			theArr[i] = theArr[i].replace(re1_pa, re1_to);
			/* replace long parameters in tag */
			theArr[i] = replaceLongParams(theArr[i]);
			/* add "counter" to the left */
			sCount = (i < 9) ? "0" + (i+1) : i+1 ;
			theArr[i] = sCount + " : " + theArr[i];
		}
	}

	/* In numerical order */

	if (theType == "numOrder") {
		theArr = theArray;
		/* get highest number */
		var lenMax  = 0;
		var lenTemp = 0;
		var re4_pa1 = new RegExp(".+\\s+no=([\\\"'])([^\\2]*?)\\1.+", "gi");
		for (var i = 0; i < theArr.length; i++) {
			sTemp = theArr[i].replace(re4_pa1, "$2");
			lenTemp = sTemp.length;
			if (lenTemp > lenMax) lenMax = lenTemp;
		}

		var re4_pa1 = new RegExp(".+\\s+no=([\\\"'])([^\\2]*?)\\1.+", "gi");
		for (var i = 0; i < theArr.length; i++) {
			/* get the number */
			sTemp = theArr[i].replace(re4_pa1, "$2");
			//sTemp = (parseInt(sTemp) < 10) ? "0" + sTemp : sTemp ;
			theArr[i] = getZeros(sTemp, lenMax) + theArr[i];
		}
		theArr = theArr.sort();
		for (var i = 0; i < theArr.length; i++) {
			theArr[i] = theArr[i].replace(/^[^<]+/g, "");
		}

	}

	/* return it */

	return theArr;
}

function getZeros(theString, len) {
	var zeros = "";
	var lenStr = theString.length;
	for (var i = 0; i < (len-lenStr); i++) {
		zeros += "0";
	}
	theString = zeros + theString;
	return theString;
}

function replaceLongParams(theString) {
	var sTemp;
	var re2_pa1 = new RegExp(".+\\s+pre=([\\\"'])([^\\2]*?)\\1.+", "gi");
	var re2_pa2 = new RegExp("\\s+(pre=)([\"'])([^\\2]*?)(\\2)", "gi");
	var re2_to = " $1$2[TOO_LONG]$2";
	var re3_pa1 = new RegExp(".+\\s+post=([\\\"'])([^\\2]*?)\\1.+", "gi");
	var re3_pa2 = new RegExp("\\s+(post=)([\\\"'])([^\\2]*?)(\\2)", "gi");
	var re3_to = " $1$2[TOO_LONG]$2";
	var re4_pa1 = new RegExp(".+\\s+label=([\\\"'])([^\\2]*?)\\1.+", "gi");
	var re4_pa2 = new RegExp("\\s+(label=)([\\\"'])([^\\2]*?)(\\2)", "gi");
	var re4_to = " $1$2[TOO_LONG]$2";

	/* read PRE */
	sTemp = theString.replace(re2_pa1, "$2");
	/* replace long PRE's */
	if (sTemp != null) {
		if (sTemp.length > 20) theString = theString.replace(re2_pa2, re2_to);
	}
	/* read POST */
	sTemp = theString.replace(re3_pa1, "$2");
	/* replace long POST's */
	if (sTemp != null) {
		if (sTemp.length > 20) theString = theString.replace(re3_pa2, re3_to);
	}
	/* read LABEL */
	sTemp = theString.replace(re4_pa1, "$2");
	/* replace long LABEL's */
	if (sTemp != null) {
		if (sTemp.length > 20) theString = theString.replace(re4_pa2, re4_to);
	}
	return theString;
}
//-->
</script><%
} %>

</body>
</html>