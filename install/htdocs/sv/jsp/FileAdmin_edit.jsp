<%@ page language="java"
	import="org.apache.oro.util.*, org.apache.oro.text.*, org.apache.oro.text.regex.*, org.apache.oro.text.perl.*, java.io.*, java.util.*, java.text.*, java.net.*, javax.servlet.*, javax.servlet.http.*, imcode.external.diverse.*, imcode.util.*, imcode.server.*"
%><%
/* *******************************************************************
 *           SETTINGS                                                *
 ******************************************************************* */

String IMG_PATH    = "@imcmsimageurllang@/admin/" ; // path to buttons (with trailing /)
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
			sError = "<? sv/jsp/FileAdmin_edit.jsp/1001/1 ?>" ;
		}
		session.setAttribute("fileSaved", fileSrc) ;
		/*for (int i = 0; i < fuckedUpUmlingBack.length; i = i + 2) {
			fileSrc = fileSrc.replaceAll(fuckedUpUmlingBack[i],fuckedUpUmlingBack[i+1]) ;
		}*/
		fileOut = new FileWriter(fn) ;
		fileOut.write(fileSrc) ;
		isSaved = true ;
	} catch (FileNotFoundException fnEx) {
		sError = "<? sv/jsp/FileAdmin_edit.jsp/1001/2 ?>" ;
	} catch (IOException ioEx) {
		sError = "<? sv/jsp/FileAdmin_edit.jsp/1001/3 ?>" ;
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
	sError  = "<b>Recovered!</b>" + sTemp + sError ;
}

String sReadonly = "" ;

if (isReadonly) {
	sReadonly = " readonly onFocus=\"blur()\"" ;
}
%>
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
<!--
function closeIt() {
	window.close();
	if (parent.opener) parent.opener.focus();
}

var isMoz = (document.getElementById);
var isIE  = (document.all);
var isNS  = (document.layers);

var win = window;
var n   = 0;

function findIt(str) {
	var txt, i, found;
	if (isMoz && str != "") {
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
	if (isIE && isMoz) document.getElementById("btnSearch").setActive(); // focus btn - for [Enter] support
}

function doSave() {
	if (confirm("<? sv/jsp/FileAdmin_edit.jsp/2/1 ?>")) {
		return true;
	}
	return false;
}

function doReset() {
	var f = document.forms.resetForm;
	var theSel = f.resetFile.options[f.resetFile.selectedIndex].value;
	if (theSel != "") {
		var theText = (theSel == "org") ? "<? sv/jsp/FileAdmin_edit.jsp/2/2 ?>" : "<? sv/jsp/FileAdmin_edit.jsp/2/3 ?>";
		if (confirm("<? sv/jsp/FileAdmin_edit.jsp/2/4 ?>" + theText + "\n\n<? sv/jsp/FileAdmin_edit.jsp/2/5 ?>")) {
			f.submit();
		} else {
			f.resetFile.selectedIndex = 0;
		}
	}
	return false;
}

/* onLoad-function */

function checkSaved(ch) {
	if (isMoz) {<%
		if (!isReadonly) { %>
		var isSaved = <%= isSaved %>;
		var el = document.getElementById("btnSave");
		if (isSaved && !ch) {
			el.style.cursor = "default";
			el.style.filter = "progid:DXImageTransform.Microsoft.BasicImage( Rotation=0,Mirror=0,Invert=1,XRay=0,Grayscale=1,Opacity=0.60)";
			el.disabled = 1;
		} else {
			el.style.cursor = (isIE) ? "hand" : "pointer";
			el.style.filter = "";
			el.disabled = 0;
		}<%
		} %>
		resize<? sv/jsp/FileAdmin_edit.jsp/2/8 ?>Field();
		loopMess(0);
	}
}

function resize<? sv/jsp/FileAdmin_edit.jsp/2/8 ?>Field() {
	if (isMoz) {
		var el<? sv/jsp/FileAdmin_edit.jsp/2/8 ?> = document.getElementById("txtField");
		var availW = 0;
		var availH = 0;
		if (isIE) {
			availW = parseInt(document.body.offsetWidth);
			availH = parseInt(document.body.offsetHeight);
		} else {
			availW = parseInt(innerWidth);
			availH = parseInt(innerHeight);
		}
		if (availW > 0) el<? sv/jsp/FileAdmin_edit.jsp/2/8 ?>.style.width = availW - 13;
		if (availH > 0) el<? sv/jsp/FileAdmin_edit.jsp/2/8 ?>.style.height = availH - 72;
	}
}

var errMess = <% if (!sError.equals("")) { %>1<% } else { %>2<% } %>;

function loopMess(stopit) {
	if (isMoz) {
		if (stopit) {
			window.clearTimeout(oTimer);
			if (errMess) {
				errMess = 0;
				return;
			} else if (isIE) {
				errMess = 2;
			}
		}
		var el = document.getElementById("messId");
		var colorRed   = "#cc0000";
		var colorGreen = "#009900";
		var oTimer;
		if (errMess == 2) {
			el.style.color = colorGreen;
			el.innerHTML = "<? sv/jsp/FileAdmin_edit.jsp/2/6 ?><i><% if (isReadonly) { %><? sv/jsp/FileAdmin_edit.jsp/2/7 ?><% } else { %><? sv/jsp/FileAdmin_edit.jsp/2/8 ?><% } %> <% if (isTempl) { %><? sv/jsp/FileAdmin_edit.jsp/2/9 ?><% } else { %><? sv/jsp/FileAdmin_edit.jsp/2/10 ?><% } %></i> <? sv/jsp/FileAdmin_edit.jsp/2/11 ?><% if (isIE) { %> <? sv/jsp/FileAdmin_edit.jsp/2/12 ?><% } %>";
			errMess = 3;<%
			if (!isReadonly) { %>
			oTimer = window.setTimeout("loopMess()", 5000);<%
			} %>
		} else if (errMess == 3) {
			el.style.color = colorGreen;
			el.innerHTML = "<? sv/jsp/FileAdmin_edit.jsp/2/13 ?>";
			errMess = 4;
			oTimer = window.setTimeout("loopMess()", 5000);
		} else if (errMess == 4) {
			el.style.color = colorGreen;
			el.innerHTML = "<? sv/jsp/FileAdmin_edit.jsp/2/14 ?>";
			errMess = <% if (!sError.equals("")) { %>1<% } else { %>2<% } %>;
			oTimer = window.setTimeout("loopMess()", 5000);
		} else if (errMess == 1) {
			el.style.color = colorRed;
			el.innerHTML = "<%= sError %>";
			errMess = 2;
			oTimer = window.setTimeout("loopMess()", 10000);
		}
	}
}

function toggleFontSize() {
	if (isMoz) {
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
				<td class="norm"><a href="javascript://help" onClick="alert('Vไlj att ๅterstไlla filen som den sๅg ut nไr:\n\n - Du senast sparade den.\n - Nไr du ๖ppnade den i detta f๖nster.')"><span style="color:black; text-decoration:none; cursor:help;"><? sv/jsp/FileAdmin_edit.jsp/4 ?></span></a>&nbsp;</td>
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
<!--
function imScriptCount(imType) {
	var hits,arr1,arr2;
	var retStr = "<? sv/jsp/FileAdmin_edit.jsp/10/8 ?>\nจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจ\n";
	if (isNS) retStr += "<? sv/jsp/FileAdmin_edit.jsp/10/9 ?>\nจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจจ\n";
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
				retStr += head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/10 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
					hits    = fixImCmsTags(hits, "codeOrder");
					retStr += hits.join("\n");
					retStr += "\n\n" + head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/12 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "numOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/1 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'image':
			if (re2.test(cont)) {
				hits = cont.match(re2);
				//hits = hits.sort();
				retStr += head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/13 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
					hits    = fixImCmsTags(hits, "codeOrder");
					retStr += hits.join("\n");
					retStr += "\n\n" + head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/12 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "numOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/2 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'menu':
			if (re3.test(cont)) {
				hits = cont.match(re3);
				//hits = hits.sort();
				retStr += head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/14 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/11 ?>:\n\n";
					hits    = fixImCmsTags(hits, "codeOrder");
					retStr += hits.join("\n");
					retStr += "\n\n" + head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/12 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "numOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/3 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'include':
			if (re4.test(cont)) {
				hits = cont.match(re4);
				//hits = hits.sort();
				retStr += head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/15 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "codeOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/4 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'bradgard':
			if (re5.test(cont)) {
				hits = cont.match(re5);
				retStr += head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/16 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "codeOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/5 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'date':
			if (re6.test(cont)) {
				hits = cont.match(re6);
				retStr += head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/17 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
				}
				hits    = fixImCmsTags(hits, "codeOrder");
				retStr += hits.join("\n");
			} else {
				retStr = head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/6 ?>" + head_1_b;
			}
			alert(retStr);
		break;
		case 'other':
			if (re7.test(cont)) {
				hits = cont.match(re7);
				retStr += head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/18 ?>" + head_1_b + "\n\n";
				if (hits.length > 1) {
					retStr += head_2_a + "<? sv/jsp/FileAdmin_edit.jsp/10/11 ?>" + head_2_b + "\n\n";
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
				retStr = head_1_a + "<? sv/jsp/FileAdmin_edit.jsp/10/7 ?>" + head_1_b;
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