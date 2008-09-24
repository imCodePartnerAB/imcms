<%@ page
  import="com.imcode.imcms.api.DocumentService,
          com.imcode.imcms.api.ContentManagementSystem,
          com.imcode.imcms.api.TextDocument,imcode.server.Imcms"
  contentType="text/html; charset=UTF-8"

%><%!
/* *******************************************************************************************
 *         COOKIE FUNCTIONS                                                                  *
 ******************************************************************************************* */

public String getCookie ( String theName, HttpServletRequest req ) {
	String retVal = "" ;
	Cookie cookie = null;
	Cookie[] cookies = req.getCookies();
	for (int i = 0; i < cookies.length; i++) {
		cookie = cookies[i];
		if (theName.equals(cookie.getName())) {
			retVal = cookie.getValue() ;
			break;
		}
	}
	return retVal ;
}

public void setCookie ( String theName, String theValue, HttpServletResponse res ) {
	Cookie cookie = null;
	cookie = new Cookie(theName, theValue);
	cookie.setMaxAge(60*60*24*365);
	cookie.setPath("/");
	res.addCookie(cookie);
}
//setCookie("DUMMY", "YEPP", response) ;
//if (debug) out.print(getCookie("DUMMY", request)) ;

%><%
response.setContentType( "text/html; charset=" + Imcms.DEFAULT_ENCODING );

ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
DocumentService documentService     = imcmsSystem.getDocumentService() ;

String action  = (request.getParameter("action") != null) ? request.getParameter("action") : "" ;
String meta_id = (request.getParameter("meta_id") != null) ? request.getParameter("meta_id") : "" ;
String txt_no  = (request.getParameter("txt") != null) ? request.getParameter("txt") : "" ;
String label   = (request.getParameter("label") != null) ? request.getParameter("label") : "" ;

int iMetaId = 0 ;
int iTxtNo  = 0 ;

try {
	iMetaId = Integer.parseInt(meta_id) ;
	iTxtNo  = Integer.parseInt(txt_no) ;
} catch(NumberFormatException ex) {
	out.print("No meta_id and/or txt_no!") ;
	return ;
}

String orgContent = "" ;
String txtContent = "" ;

TextDocument txtDoc = documentService.getTextDocument(iMetaId) ;

if (action.equals("ExecSave")) {

	orgContent = request.getParameter("orgContent") ;
	txtContent = request.getParameter("txtContent") ;

	txtDoc.setHtmlTextField(iTxtNo, txtContent) ;
	documentService.saveChanges(txtDoc) ;

} else {

	TextDocument.TextField txtField = txtDoc.getTextField(iTxtNo) ;
	orgContent = txtField.getHtmlFormattedText() ;
	txtContent = orgContent ;

}
%><html>
<head>
<title>&nbsp; <? install/htdocs/sv/htmleditor/editor/editor.jsp/1001 ?> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</title>

<META HTTP-EQUIV="pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-Control" CONTENT="must-revalidate">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META NAME="robots" CONTENT="noindex, nofollow, noimageindex">

<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript" SRC="scripts/editor_config.js.jsp"></SCRIPT>
<SCRIPT LANGUAGE="VBScript"   TYPE="text/vbscript"   SRC="scripts/editor_config.vbs.jsp"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript" SRC="scripts/editor_functions.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript" SRC="scripts/editor_help.js"></SCRIPT>
<SCRIPT LANGUAGE="VBScript"   TYPE="text/vbscript"   SRC="scripts/editor_functions.vbs"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript" SRC="scripts/editor_config_buttons.js"></SCRIPT>

<link rel="STYLESHEET" type="text/css" href="css/editor.css">

<STYLE TYPE="text/css">
<!--
#fontColorDiv { position:relative; width:18; height:18; border: 1px inset #FFFFFF; background-color:#000000 }
#backgroundColorDiv { position:relative; width:18; height:18; border: 1px inset #FFFFFF; background-color:#FFFFFF }
-->
</STYLE>

</head>
<body unselectable="off" bgcolor="#D6D3CE" style="background-color:buttonface; border: 1px solid buttonface;" onLoad="init();" onUnLoad="if (parent.opener) parent.opener.location.reload();">

<script language="JavaScript">
<!--
if (directEditEnabled) {
	//document.write('<iframe id="changeTextFrame" src="' + servletPath + 'ChangeText?meta_id=' + metaId + '&txt=' + txtNr + '&label=' + sLabel + '" style="position:absolute; top:1000; left:-1000; width:1; height:1; z-index:2000; visibility:visible"></iframe>') ;
}
//-->
</script>



<form name="editorForm" onSubmit="return false">
<input type="hidden" name="execState" value="1">
<DIV style="position:absolute; left:0; top:0">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr bgcolor="#333366">
	<td height="20" style="font: 11px Verdana,Geneva,Arial,Helvetica,sans-serif; color:#FFFFFF">
	<img src="images/1x1.gif" width="10" height="1"><? install/htdocs/sv/htmleditor/editor/editor.jsp/2/1 ?>
	<b style="font-size:12px"><%= txt_no %></b><%
	if (!label.equals("")) {
		label = label.replaceAll("<[^>]+>", " ") ;
		%> [<%= label.substring(0,(label.length() > 60 ? 60 : label.length())) + ((label.length() > 60) ? "..." : "") %>]<%
	} %>
	<? install/htdocs/sv/htmleditor/editor/editor.jsp/2/2 ?>
	<b style="font-size:12px"><%= meta_id %></b></td>
	<td align="right">
	<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td height="16">
<SCRIPT LANGUAGE="JavaScript">
<!--
if (showHelpFullBtn) {
	document.writeln('<a href="javascript:void(0)" onClick="showHelp(\'all\');"><SPAN STYLE="font: bold 10px; color:white; text-decoration:none"><? install/htdocs/sv/htmleditor/editor/editor.jsp/3/1 ?></SPAN></a>');
}
if (showHelpFullBtn && showHelpSubjectBtn) {
	document.writeln('</td>\n<td><img src="images/1x1.gif" width="10" height="1"></td>\n<td>');
}
if (showHelpSubjectBtn) {
	document.writeln('<a href="javascript:void(0)" onClick="showHelpSubjects();"><img src="images/btn_help_subject.gif" width="16" height="16" alt="" border="0"></a>');
}
//-->
</SCRIPT></td>
		<td><img src="images/1x1.gif" width="10" height="1"></td>
	</tr>
	</table></td>
</tr>
</table>


<div id="javascriptDisabled" style="display:; text-align:center; font-size:16pt"></div>


<!-- ***** TOP NAVIGATION ***** -->

<div align="center" style="position:absolute; left:-1; top:20; width:665; height:60; z-index:2; text-align:left" unselectable="on">

<img src="images/1x1.gif" width="1" height="4"><br>


<table border="0" cellspacing="0" cellpadding="0" width="681" unselectable="on">
<tr>
	<td unselectable="on">
	<table border="0" cellspacing="0" cellpadding="0" align="left">
	<tr>
		<td><img src="images/1x1.gif" width="3" height="1"></td>
<script language="JavaScript">
<!-- /* ***** FIRST ROW BUTTONS ***** */
for (var i = 0; i < arrButtonsRowOne.length; i++) {
	document.writeln('	<td>' + arrBtns[arrButtonsRowOne[i]] + '</td>');
}
//-->
</script>
	</tr>
	</table></td>
	<td align="right" unselectable="on">

<SCRIPT LANGUAGE="JavaScript">
<!--
if (showHtmlBtn) {
	document.write('<button unselectable="on" id="btn12" onClick="if(document.forms[0].execState.value == \'2\')\{ showHelpLayer(\'EditCode\'); \} else \{ previewCode()\; \}" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/5/1 ?>" style="position:relative; width:55; height:21">');
	document.write('		<img name="previewImg" src="images/btn_preview_html.gif"></button>');
}

if (showAdv && showSimple) {
	document.write('&nbsp\;<button unselectable="on" id="advBtn" style="width:70" onClick="show<? install/htdocs/sv/htmleditor/editor/editor.jsp/5/3 ?>()\;" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/5/2 ?>" style="height:21"><b id="advBtnText" style="position:relative\; top:0"><? install/htdocs/sv/htmleditor/editor/editor.jsp/5/3 ?></b></button>');
} else {
	document.write('<button unselectable="on" id="advBtn" style="width:70" onClick="show<? install/htdocs/sv/htmleditor/editor/editor.jsp/5/3 ?>()\;" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/5/2 ?>" style="position:relative\; top:-1000\; height:21"><b id="advBtnText" style="position:relative\; top:-1000"> <? install/htdocs/sv/htmleditor/editor/editor.jsp/5/3 ?> </b></button>');
	if (document.getElementById("btn12")) document.getElementById("btn12").style.left = 70;
}
//-->
</SCRIPT></td>
</tr>
</table>


<script language="JavaScript">
<!--
if (arrButtonsRowTwo.length > 0) {
	document.writeln('<img src="images/1x1.gif" width="1" height="3"><br>');
	document.writeln('<img src="images/1x1_848284.gif" width="681" height="1"><br>');
	document.writeln('<img src="images/1x1_white.gif" width="681" height="1"><br>');
	document.writeln('<img src="images/1x1.gif" width="1" height="3"><br>');

	document.writeln('<table border="0" cellspacing="0" cellpadding="0" align="left">');
	document.writeln('<tr>');
	document.writeln('	<td><img src="images/1x1.gif" width="3" height="1"></td>');
}
for (var i = 0; i < arrButtonsRowTwo.length; i++) {
	document.writeln('	<td>' + arrBtns[arrButtonsRowTwo[i]] + '</td>');
}
if (arrButtonsRowTwo.length > 0) {
	document.writeln('</tr>');
	document.writeln('</table>');
}
//-->
</script></div>


<!-- ***** END TOP NAVIGATION ***** -->


<!-- ******************************************************************************** -->
<!-- ***********************************  EDITOR  *********************************** -->
<!-- ******************************************************************************** -->







<div id="editorOuterDiv" unselectable="off" style="position:absolute; left:5; top:80; width:525; height:380; z-index:3; background-color:white; text-align: left; padding:0; border: 2px inset #D6D3CE">
</div>
<div id="editorDiv" contenteditable unselectable="off" style="position:absolute; left:5; top:80; width:525; height:380; z-index:4; background-color:white; text-align: left; padding:3; border: 2px inset #D6D3CE; scrollbar-base-color:#D6D3CE; overflow:auto" onKeyUp="if (isWordEnabled && preview) checkWordCode(this.innerHTML)" onMouseUp="if (isWordEnabled && preview) checkWordCode(this.innerHTML)" onChange="if (isWordEnabled && preview) checkWordCode(this.innerHTML)"></div>










<!-- ***** Right panel - SIMPLE ***** -->

<div id="modeSimpleDiv" style="position:absolute; left:535; top:80; z-index:5; visibility:hidden" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0" width="141">
<tr>
	<td align="center" bgcolor="#333366" height="22">
	    <b style="color:#ffffff" id="simpleHeadingText"><? install/htdocs/sv/htmleditor/editor/editor.jsp/7 ?></b></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"></td>
</tr>
<tr>
	<td>
	<SCRIPT LANGUAGE="JavaScript">
<!--
var onCountSimple = 0;
if(showSimple){
	if (showSimpleLinkDiv) onCountSimple++;
	if (showSimpleListDiv) onCountSimple++;
	if (showSimplePixelDiv) onCountSimple++;

	if (onCountSimple > 1) {
		document.write('	<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/1 ?></b></td>\n');
		document.write('	</tr>\n');
		document.write('	<tr>\n');
		document.write('		<td>\n');
		document.write('	<sel'+'ect name="simpleSelector" unselectable="On" onChange="showSimpleFunction(this.options(this.selectedIndex).value);" style="width:140">\n');
		if(showSimpleLinkDiv) {
			document.write('		<option value="modeSimpleLinkDiv"><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/2 ?></option>\n');
		}
		if(showSimpleListDiv) {
			document.write('		<option value="modeSimpleListDiv"><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/3 ?></option>\n');
		}
		if(showSimplePixelDiv) {
			document.write('		<option value="modeSimplePixelDiv"><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/4 ?></option>\n');
		}
		document.write('	</sel'+'ect>');
	} else {
		document.write('	<sel'+'ect name="simpleSelector" style="display:none">\n');
		document.write('		<option value="modeSimpleLinkDiv"><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/2 ?></option>\n');
		document.write('		<option value="modeSimpleListDiv"><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/3 ?></option>\n');
		document.write('		<option value="modeSimplePixelDiv"><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/4 ?></option>\n');
		document.write('	</sel'+'ect>');
		if(showSimpleLinkDiv) {
			document.write('<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/2 ?></b>');
			document.getElementById("simpleHeadingText").innerText = '<? install/htdocs/sv/htmleditor/editor/editor.jsp/8/2 ?>';
		} else if(showSimpleListDiv) {
			document.write('<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/3 ?></b>');
			document.getElementById("simpleHeadingText").innerText = '<? install/htdocs/sv/htmleditor/editor/editor.jsp/8/3 ?>';
		} else if(showSimplePixelDiv) {
			document.write('<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/8/4 ?></b>');
			document.getElementById("simpleHeadingText").innerText = '<? install/htdocs/sv/htmleditor/editor/editor.jsp/8/4 ?>';
		}
	}
}
//-->
</SCRIPT></td>
</tr>
<tr>
	<td>
	<img src="images/1x1.gif" width="1" height="4"><br>
	<img src="images/1x1_848284.gif" width="141" height="1"><br>
	<img src="images/1x1_white.gif" width="141" height="1"><br>
	<img src="images/1x1.gif" width="1" height="2"></td>
</tr>
</table></div>











<div id="modeSimpleLinkDiv" style="position:absolute; left:535; top:155; width:141; z-index:6; visibility:hidden;" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/9 ?></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"><? install/htdocs/sv/htmleditor/editor/editor.jsp/10 ?></td>
</tr>
<tr>
	<td>
	<select name="createLinkType" unselectable="On" onChange="changeLinkType(this.value)" style="width:140">
		<option value="GetDoc"><? install/htdocs/sv/htmleditor/editor/editor.jsp/11 ?></option>
		<option value="http"><? install/htdocs/sv/htmleditor/editor/editor.jsp/12 ?></option>
		<option value="mailto"><? install/htdocs/sv/htmleditor/editor/editor.jsp/13 ?></option>
		<option value="ftp"><? install/htdocs/sv/htmleditor/editor/editor.jsp/14 ?></option>
	</select></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"><? install/htdocs/sv/htmleditor/editor/editor.jsp/15 ?></td>
</tr>
<tr>
	<td>
	<select name="createLinkTargetTemp" unselectable="On" onChange="document.forms[0].createLinkTarget.value = this.value; document.forms[0].createLinkTarget.focus(); document.forms[0].createLinkTarget.select()" style="width:140">
		<option value="" selected><? install/htdocs/sv/htmleditor/editor/editor.jsp/16 ?></option>
		<option value="_top"><? install/htdocs/sv/htmleditor/editor/editor.jsp/17 ?></option>
		<option value="_blank"><? install/htdocs/sv/htmleditor/editor/editor.jsp/18 ?></option>
		<option value="_self"><? install/htdocs/sv/htmleditor/editor/editor.jsp/19 ?></option>
		<option value="_parent"><? install/htdocs/sv/htmleditor/editor/editor.jsp/20 ?></option>
		<option value="Skriv namnet!"><? install/htdocs/sv/htmleditor/editor/editor.jsp/21 ?></option>
	</select></td>
</tr>
<tr>
	<td><input type="text" name="createLinkTarget" value="" size="12" maxlength="50" style="width:140"><br>
	<img src="images/1x1.gif" width="1" height="5"></td>
</tr>
<tr>
	<td id="createLinkFieldText"><? install/htdocs/sv/htmleditor/editor/editor.jsp/22 ?></td>
</tr>
<tr>
	<td id="createLinkField"><input type="text" name="createLinkValue" value="" size="5" maxlength="6" style="width:42; text-align:right"></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"><? install/htdocs/sv/htmleditor/editor/editor.jsp/23 ?></td>
</tr>
<tr>
	<td>
<SCRIPT LANGUAGE="JavaScript1.2">
<!--
var iCount = 0;
var sChecked='';
document.write('<select name="createLinkCssTemp" unselectable="On" style="width:140" onChange="document.forms[0].createLinkCss.value = this.options[this.selectedIndex].value; this.selectedIndex = 0">');
document.write('	<option value=""><? install/htdocs/sv/htmleditor/editor/editor.jsp/24/1 ?></option>');
for (var i=0; i<arrAllClass.length; i++){
	document.write('	<option value="' + arrAllClass[i] + '">' + arrAllClass[i] + '</option>');
}
document.write('</select>');
//-->
</SCRIPT></td>
</tr>
<tr>
	<td><input type="text" name="createLinkCss" value="" size="12" maxlength="50" style="width:140"></td>
</tr>
<tr>
	<td align="center"><img src="images/1x1.gif" width="1" height="5"><br>
	<button unselectable="on" name="CreateLink" id="CreateLink" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/2001 ?>" class="fButtonSmall"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1002 ?></button></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="15"><br>
	<span class="dim"><? install/htdocs/sv/htmleditor/editor/editor.jsp/25 ?></span></td>
</tr>
</table>
</div>














<div id="modeSimpleListDiv" style="position:absolute; left:535; top:155; width:141; z-index:6; visibility:hidden;" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/26 ?></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"><? install/htdocs/sv/htmleditor/editor/editor.jsp/27 ?></td>
</tr>
<tr>
	<td>
	<select name="createListType" unselectable="On" onChange="changeListType(this.value)" style="width:140">
		<option value="UL"><? install/htdocs/sv/htmleditor/editor/editor.jsp/28 ?></option>
		<option value="OL"><? install/htdocs/sv/htmleditor/editor/editor.jsp/29 ?></option>
		<option value="DL"><? install/htdocs/sv/htmleditor/editor/editor.jsp/30 ?></option>
	</select></td>
</tr>
</table>
<DIV id="listCountTypeDiv" style="position:relative; visibility:hidden" unselectable="On">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"><? install/htdocs/sv/htmleditor/editor/editor.jsp/31 ?></td>
</tr>
<tr>
	<td>
	<select name="createListOLType" unselectable="On" style="width:140">
		<option value="1" selected><? install/htdocs/sv/htmleditor/editor/editor.jsp/32 ?></option>
		<option value="A"><? install/htdocs/sv/htmleditor/editor/editor.jsp/33 ?></option>
		<option value="a"><? install/htdocs/sv/htmleditor/editor/editor.jsp/34 ?></option>
		<option value="I"><? install/htdocs/sv/htmleditor/editor/editor.jsp/35 ?></option>
		<option value="i"><? install/htdocs/sv/htmleditor/editor/editor.jsp/36 ?></option>
	</select></td>
</tr>
</table></DIV>
<DIV id="listCountDiv" style="position:relative; top:-25" unselectable="On">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td>
	<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/37 ?></td>
		<td><img src="images/1x1.gif" width="10" height="1"></td>
		<td>
		<SCRIPT LANGUAGE="JavaScript">
<!--
document.write('<select name="createListCount" unselectable="On" style="width:47">');
for(i=1;i<=20;i++){
	document.write('<option value="' + i + '">' + i + '</option>');
}
document.write('</select>');
//-->
</SCRIPT></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"><? install/htdocs/sv/htmleditor/editor/editor.jsp/39 ?></td>
</tr>
<tr>
	<td>
<SCRIPT LANGUAGE="JavaScript1.2">
<!--
var iCount = 0;
var sChecked='';
document.write('<select name="createListCssTemp" unselectable="On" style="width:140" onChange="document.forms[0].createListCss.value = this.options[this.selectedIndex].value; this.selectedIndex = 0">');
document.write('	<option value=""><? install/htdocs/sv/htmleditor/editor/editor.jsp/40/1 ?></option>');
for (var i=0; i<arrAllClass.length; i++){
	document.write('	<option value="' + arrAllClass[i] + '">' + arrAllClass[i] + '</option>');
}
document.write('</select>');
//-->
</SCRIPT></td>
</tr>
<tr>
	<td><input type="text" name="createListCss" value="" size="12" maxlength="50" style="width:140"></td>
</tr>
<tr>
	<td align="center"><img src="images/1x1.gif" width="1" height="5"><br>
	<button unselectable="on" name="CreateList" id="CreateList" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/2002 ?>" class="fButtonSmall"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1003 ?></button></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="15"><br>
	<span class="dim"><? install/htdocs/sv/htmleditor/editor/editor.jsp/41 ?></span></td>
</tr>
</table></DIV>
</div>








<div id="modeSimplePixelDiv" style="position:absolute; left:535; top:155; width:141; z-index:6; visibility:hidden;" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/42 ?>
	<img src="images/1x1.gif" width="1" height="5"></td>
</tr>
<tr>
	<td>
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/43 ?></td>
		<td align="right"><input type="text" name="pixelWidth" value="1" size="4" maxlength="4" style="text-align:right; width:35"></td>
	</tr>
	<tr>
		<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/44 ?></td>
		<td align="right"><input type="text" name="pixelHeight" value="8" size="4" maxlength="4" style="text-align:right; width:35"></td>
	</tr>
	<tr>
		<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/45 ?></td>
		<td align="right"><input type="text" name="pixelBorder" value="0" size="4" maxlength="4" style="text-align:right; width:35"></td>
	</tr>
	<tr>
		<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/46 ?></td>
		<td align="right">
		<select name="pixelAlign" unselectable="On" style="width:85">
			<option value="" selected><? install/htdocs/sv/htmleditor/editor/editor.jsp/47 ?></option>
			<option value="left"><? install/htdocs/sv/htmleditor/editor/editor.jsp/48 ?></option>
			<option value="right"><? install/htdocs/sv/htmleditor/editor/editor.jsp/49 ?></option>
			<option value="top"><? install/htdocs/sv/htmleditor/editor/editor.jsp/50 ?></option>
			<option value="middle"><? install/htdocs/sv/htmleditor/editor/editor.jsp/51 ?></option>
			<option value="bottom"><? install/htdocs/sv/htmleditor/editor/editor.jsp/52 ?></option>
			<option value="texttop"><? install/htdocs/sv/htmleditor/editor/editor.jsp/53 ?></option>
			<option value="absmiddle"><? install/htdocs/sv/htmleditor/editor/editor.jsp/54 ?></option>
			<option value="baseline"><? install/htdocs/sv/htmleditor/editor/editor.jsp/55 ?></option>
			<option value="absbottom"><? install/htdocs/sv/htmleditor/editor/editor.jsp/56 ?></option>
		</select></td>
	</tr>
	<tr>
		<td nowrap><img src="images/1x1.gif" width="1" height="5"><br>
		<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/57/1 ?></b></td>
	</tr>
	<tr>
		<td align="center"><? install/htdocs/sv/htmleditor/editor/editor.jsp/58 ?></td>
		<td align="center"><? install/htdocs/sv/htmleditor/editor/editor.jsp/59 ?></td>
	</tr>
	<tr>
		<td align="center"><input type="checkbox" unselectable="On" name="pixelBreakStart" value="1"></td>
		<td align="center"><input type="checkbox" unselectable="On" name="pixelBreakEnd" value="1"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td align="center"><img src="images/1x1.gif" width="1" height="5"><br>
	<button unselectable="on" name="CreatePixel" id="CreatePixel" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/2003 ?>" class="fButtonSmall"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1004 ?></button></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="15"><br>
	<span class="dim"><? install/htdocs/sv/htmleditor/editor/editor.jsp/60 ?>
	<img src="images/1x1.gif" width="1" height="5"><br>
	&nbsp;&nbsp;&#149; <? install/htdocs/sv/htmleditor/editor/editor.jsp/1005/1 ?><br>
	&nbsp;&nbsp;&#149; <? install/htdocs/sv/htmleditor/editor/editor.jsp/1005/2 ?><br>
	&nbsp;&nbsp;&#149; <? install/htdocs/sv/htmleditor/editor/editor.jsp/1005/3 ?><br>
	&nbsp;&nbsp;&#149; <? install/htdocs/sv/htmleditor/editor/editor.jsp/1005/4 ?><br>
	<img src="images/1x1.gif" width="1" height="5"><br>
	<? install/htdocs/sv/htmleditor/editor/editor.jsp/1006/1 ?></span></td>
</tr>
</table>
</div>





<!-- ***** Right panel - ADVANCED ***** -->

<div id="modeAdvancedDiv" style="position:absolute; left:535; top:80; width:141; z-index:5; visibility:hidden" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0" width="141">
<tr>
	<td align="center" bgcolor="#333366" height="22"><b style="color:#ffffff" id="advHeadingText"><? install/htdocs/sv/htmleditor/editor/editor.jsp/61/1 ?></b></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"></td>
</tr>
<tr>
	<td>
	<SCRIPT LANGUAGE="JavaScript">
<!--
var onCountAdv = 0;
if(showAdv){
	if (showAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?>Div) onCountAdv++;
	if (showAdvFontClassDiv) onCountAdv++;
	if (showAdvFontStyleDiv) onCountAdv++;
	if (showAdvCodeStringDiv) onCountAdv++;
	if (showAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/3 ?>Div) onCountAdv++;

	if (onCountAdv > 1) {
		document.write('	<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/62/1 ?></b></td>\n');
		document.write('	</tr>\n');
		document.write('	<tr>\n');
		document.write('		<td>\n');
		document.write('	<sel'+'ect name="advSelector" unselectable="On" onChange="showAdvFunction(this.options(this.selectedIndex).value);" style="width:140">\n');
		if(showAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?>Div){
			document.write('		<option value="modeAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?>Div"><? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?></option>\n');
		}
		if(showAdvFontClassDiv){
			document.write('		<option value="modeAdvFontClassDiv">Font Cl'+'ass=&quot\; X &quot\;</option>\n');
		}
		if(showAdvFontStyleDiv){
			document.write('		<option value="modeAdvFontStyleDiv">Font Style=&quot\; X &quot\;</option>\n');
		}
		if(showAdvCodeStringDiv){
			document.write('		<option value="modeAdvCodeStringDiv"><? install/htdocs/sv/htmleditor/editor/editor.jsp/62/2 ?></option>\n');
		}
		if(showAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/3 ?>Div){
			document.write('		<option value="modeAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/3 ?>Div"><? install/htdocs/sv/htmleditor/editor/editor.jsp/62/3 ?></option>\n');
		}
		document.write('	</sel'+'ect>');
	} else {
		document.write('	<sel'+'ect name="advSelector" style="display:none">\n');
		document.write('		<option value="modeAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?>Div"><? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?></option>\n');
		document.write('		<option value="modeAdvFontClassDiv">Font Cl'+'ass=&quot\; X &quot\;</option>\n');
		document.write('		<option value="modeAdvFontStyleDiv">Font Style=&quot\; X &quot\;</option>\n');
		document.write('		<option value="modeAdvCodeStringDiv"><? install/htdocs/sv/htmleditor/editor/editor.jsp/62/2 ?></option>\n');
		document.write('		<option value="modeAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/3 ?>Div"><? install/htdocs/sv/htmleditor/editor/editor.jsp/62/3 ?></option>\n');
		document.write('	</sel'+'ect>');
		if(showAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?>Div) {
			document.write('<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?></b>');
			document.getElementById("advHeadingText").innerText = '<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/4 ?>';
		} else if(showAdvFontClassDiv) {
			document.write('<b>Font Cl'+'ass=&quot\; X &quot\;</b>');
			document.getElementById("advHeadingText").innerText = 'Font Cl'+'ass=&quot\; X &quot\;';
		} else if(showAdvFontStyleDiv) {
			document.write('<b>Font Style=&quot\; X &quot\;</b>');
			document.getElementById("advHeadingText").innerText = 'Font Style=&quot\; X &quot\;';
		} else if(showAdvCodeStringDiv) {
			document.write('<b>Valfri kod</b>');
			document.getElementById("advHeadingText").innerText = '<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/2 ?>';
		} else if(showAdv<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/3 ?>Div) {
			document.write('<b>Inställningar</b>');
			document.getElementById("advHeadingText").innerText = '<? install/htdocs/sv/htmleditor/editor/editor.jsp/62/3 ?>';
		}
	}
}
//-->
</SCRIPT></td>
</tr>
<tr>
	<td>
	<img src="images/1x1.gif" width="1" height="4"><br>
	<img src="images/1x1_848284.gif" width="141" height="1"><br>
	<img src="images/1x1_white.gif" width="141" height="1"><br>
	<img src="images/1x1.gif" width="1" height="2"></td>
</tr>
</table></div>







<div id="modeAdvStandardDiv" style="position:absolute; left:535; top:150; width:141; z-index:5; visibility:hidden" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0" width="140">
<tr>
	<td nowrap><? install/htdocs/sv/htmleditor/editor/editor.jsp/63 ?></td>
	<td align="right"><button id="btn26" class="button" unselectable="on" onClick="collectTableInfo()" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/2004 ?>">
	<img src="images/btn_table.gif"></button></td>
</tr>
<tr>
	<td colspan="2" height="10">
	<img src="images/1x1_848284.gif" width="141" height="1"><br>
	<img src="images/1x1_white.gif" width="141" height="1"></td>
</tr>
<tr>
	<td colspan="2" nowrap>
	<? install/htdocs/sv/htmleditor/editor/editor.jsp/64 ?>
	<select name="FormatBlockCode" unselectable="On" style="font-size:9px; width:140">
		<option value="" SELECTED><? install/htdocs/sv/htmleditor/editor/editor.jsp/65 ?></option>
		<option value="P"><? install/htdocs/sv/htmleditor/editor/editor.jsp/66 ?></option>
		<option value="DIV"><? install/htdocs/sv/htmleditor/editor/editor.jsp/67 ?></option>
		<option value="H1"><? install/htdocs/sv/htmleditor/editor/editor.jsp/68 ?></option>
		<option value="H2"><? install/htdocs/sv/htmleditor/editor/editor.jsp/69 ?></option>
		<option value="H3"><? install/htdocs/sv/htmleditor/editor/editor.jsp/70 ?></option>
		<option value="H4"><? install/htdocs/sv/htmleditor/editor/editor.jsp/71 ?></option>
		<option value="H5"><? install/htdocs/sv/htmleditor/editor/editor.jsp/72 ?></option>
		<option value="H6"><? install/htdocs/sv/htmleditor/editor/editor.jsp/73 ?></option>
		<option value="PRE"><? install/htdocs/sv/htmleditor/editor/editor.jsp/74 ?></option>
		<option value="ADDRESS"><? install/htdocs/sv/htmleditor/editor/editor.jsp/75 ?></option>
		<option value="BLOCKQUOTE"><? install/htdocs/sv/htmleditor/editor/editor.jsp/76 ?></option>
	</select><br>
	<img src="images/1x1.gif" width="1" height="5"><br>
	<div align="center"><button id="btn27" unselectable="on" onClick="doFormatBlock();" class="fButtonSmall"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1007 ?></button></div></td>
</tr>
<tr>
	<td colspan="2" height="10">
	<img src="images/1x1_848284.gif" width="141" height="1"><br>
	<img src="images/1x1_white.gif" width="141" height="1"></td>
</tr>
<tr>
	<td nowrap><? install/htdocs/sv/htmleditor/editor/editor.jsp/77 ?></td>
	<td align="right"><button id="btn28" class="button" unselectable="On" onClick="doExecCommand('InsertHorizontalRule',true,null);" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/2005 ?>">
	<img src="images/btn_hr.gif"></button></td>
</tr>
<tr>
	<td colspan="2" height="10">
	<img src="images/1x1_848284.gif" width="141" height="1"><br>
	<img src="images/1x1_white.gif" width="141" height="1"></td>
</tr>
<tr style="display:none"><!-- disconnected -->
	<td nowrap><? install/htdocs/sv/htmleditor/editor/editor.jsp/1008 ?></td>
	<td align="right"><button id="btn29" class="button" unselectable="On" onClick="javascript://doExecCommand('InsertImage',true,null);" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/2006 ?>">
	<img src="images/btn_image.gif"></button></td>
</tr>
<tr style="display:none"><!-- disconnected -->
	<td colspan="2" height="10">
	<img src="images/1x1_848284.gif" width="141" height="1"><br>
	<img src="images/1x1_white.gif" width="141" height="1"></td>
</tr>
<tr>
	<td nowrap><? install/htdocs/sv/htmleditor/editor/editor.jsp/78 ?></td>
	<td align="right"><button id="btn30" unselectable="on" class="button" onClick="dummyText();" title="<? install/htdocs/sv/htmleditor/editor/editor.jsp/2007 ?>">
	<img src="images/btn_text.gif"></button>
</td>
</tr>
</table></div>







<div id="modeAdvFontClassDiv" style="position:absolute; left:535; top:155; width:141; z-index:5; visibility:hidden" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0" width="140">
<tr>
	<td colspan="2"><? install/htdocs/sv/htmleditor/editor/editor.jsp/79 ?></td>
</tr>
<SCRIPT LANGUAGE="JavaScript1.2">
<!--
var iCount = 0;
var sChecked='';
for (var i=0; i<arrFavClass.length; i++){
	if(i==0){
		sChecked=' checked';
	} else {
		sChecked='';
	}
	document.write('<tr>\n');
	document.write('	<td><input type="radio" name="useClass" value="' + (i+1) + '"' + sChecked + '></td>\n');
	document.write('	<td><input type="text" name="useClass' + (i+1) + '" value="' + arrFavClass[i] + '" size="12" maxlength="50" style="width:119"></td>\n');
	document.write('</tr>\n');
	iCount++;
}
//-->
</SCRIPT>
<tr>
	<td colspan="2"><? install/htdocs/sv/htmleditor/editor/editor.jsp/81 ?>
<SCRIPT LANGUAGE="JavaScript1.2">
<!--
var iCount = 0;
var sChecked='';
document.write('<select name="useClassTemp" unselectable="On" style="width:140" onChange="document.forms[0].useClass11.value = this.options[this.selectedIndex].value\; document.forms[0].useClass[10].checked = 1\; this.selectedIndex = 0">');
document.write('	<option value=""><? install/htdocs/sv/htmleditor/editor/editor.jsp/82/1 ?></option>\n');
for (var i=0; i<arrAllClass.length; i++){
	document.write('	<option value="' + arrAllClass[i] + '">' + arrAllClass[i] + '</option>\n');
}
document.write('</select>\n');
//-->
</SCRIPT></td>
</tr>
<tr>
	<td><input type="radio" name="useClass" value="11"></td>
	<td><input type="text" name="useClass11" value="- Annan Class" size="12" maxlength="50" style="width:119" onKeyDown="document.forms[0].useClass[10].checked = 1;"></td>
</tr>
<tr>
	<td colspan="2" align="center"><img src="images/1x1.gif" width="1" height="5"><br>
	<button unselectable="on" name="classBtn" id="classBtn" class="fButtonSmall"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1009 ?></button></td>
</tr>
</table></div>

<!--
*****************************************************************************************
*           FONT STYLE                                                                  *
*****************************************************************************************
-->

<div id="modeAdvFontStyleDiv" style="position:absolute; left:535; top:155; width:141; z-index:5; visibility:hidden" unselectable="on">
<? install/htdocs/sv/htmleditor/editor/editor.jsp/83 ?>
<select name="FontFamily" style="font-size:9px; width:140">
	<option value="" SELECTED><? install/htdocs/sv/htmleditor/editor/editor.jsp/84 ?></option>
	<option value="Verdana"><? install/htdocs/sv/htmleditor/editor/editor.jsp/85 ?></option>
	<option value="Arial"><? install/htdocs/sv/htmleditor/editor/editor.jsp/86 ?></option>
	<option value="Times"><? install/htdocs/sv/htmleditor/editor/editor.jsp/87 ?></option>
	<option value="Courier"><? install/htdocs/sv/htmleditor/editor/editor.jsp/88 ?></option>
	<option value="Comic"><? install/htdocs/sv/htmleditor/editor/editor.jsp/89 ?></option>
</select>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/90 ?></td>
	<td>
	<select name="FontFormat" style="font-size:9px">
		<option value="" SELECTED><? install/htdocs/sv/htmleditor/editor/editor.jsp/91 ?></option>
		<option value="normal"><? install/htdocs/sv/htmleditor/editor/editor.jsp/92 ?></option>
		<option value="bold"><? install/htdocs/sv/htmleditor/editor/editor.jsp/93 ?></option>
		<option value="italic"><? install/htdocs/sv/htmleditor/editor/editor.jsp/94 ?></option>
		<option value="both"><? install/htdocs/sv/htmleditor/editor/editor.jsp/95 ?></option>
	</select></td>
</tr>
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/96 ?></td>
	<td>
	<select name="FontSize" style="font-size:9px">
		<option value="" SELECTED><? install/htdocs/sv/htmleditor/editor/editor.jsp/97 ?></option>
		<option value="9px"><? install/htdocs/sv/htmleditor/editor/editor.jsp/98 ?></option>
		<option value="9pt"><? install/htdocs/sv/htmleditor/editor/editor.jsp/99 ?></option>
	<SCRIPT LANGUAGE="JavaScript">
<!--
for(i=10;i<=48;i++){
	document.write('		<option value=\"' + i + 'px\">' + i + 'px</option>\n');
	document.write('		<option value=\"' + i + 'pt\">' + i + 'pt</option>\n');
}
//-->
</SCRIPT>
	</select></td>
</tr>
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/1010 ?>&nbsp;&nbsp;</td>
	<td>
	<select name="LineHeight" style="font-size:9px">
		<option value="" SELECTED><? install/htdocs/sv/htmleditor/editor/editor.jsp/101 ?></option>
	<SCRIPT LANGUAGE="JavaScript">
<!--
for(i=9;i<=48;i++){
	document.write('		<option value=\"' + i + 'px\">' + i + 'px</option>\n');
	document.write('		<option value=\"' + i + 'pt\">' + i + 'pt</option>\n');
}
//-->
</SCRIPT>
	</select></td>
</tr>
</table>
<input type="checkbox" name="usecolor" value="1"><b><? install/htdocs/sv/htmleditor/editor/editor.jsp/103/1 ?></b><br>
<img src="images/1x1.gif" width="1" height="60"><br>
<div align="center"><button unselectable="on" name="styleBtn" id="styleBtn" class="fButtonSmall"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1011 ?></button></div></div>




<div id="modeAdvCodeStringDiv" style="position:absolute; left:535; top:155; width:141; z-index:5; visibility:hidden" unselectable="on">
<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/104/1 ?></b><br>
<textarea cols="15" rows="7" name="startCode" wrap="soft" class="fFieldSmall" style="width:140"></textarea><br>
<img src="images/1x1.gif" width="1" height="5"><br>
<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/105/1 ?></b><br>
<textarea cols="15" rows="3" name="endCode" wrap="soft" class="fFieldSmall" style="width:140"></textarea><br>
<img src="images/1x1.gif" width="1" height="5"><br>
<div align="center"><button unselectable="on" name="codeBtn" id="codeBtn" class="fButtonSmall"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1012 ?></button></div></div>




<div id="modeAdvSettingsDiv" style="position:absolute; left:535; top:155; width:141; z-index:5; visibility:hidden" unselectable="on">
<? install/htdocs/sv/htmleditor/editor/editor.jsp/1013 ?>
<img src="images/1x1.gif" width="1" height="5"><br>
<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/106/1 ?></b> <SPAN STYLE="color:CC0000"><sup><? install/htdocs/sv/htmleditor/editor/editor.jsp/107 ?></sup></SPAN><br>
<input type="text" name="previewWidth" value="100%" size="5" maxlength="5"><br>
<img src="images/1x1.gif" width="1" height="5"><br>
<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/108/1 ?></b><br>
<input type="text" name="previewBackground" value="rgb(255,255,255)" size="15" maxlength="30" style="width:140"><br>
<img src="images/1x1.gif" width="1" height="5"><br>
<b><? install/htdocs/sv/htmleditor/editor/editor.jsp/109/1 ?></b><br>
<input type="text" name="previewColor" value="rgb(0,0,0)" size="15" maxlength="30" style="width:140"><br>
<img src="images/1x1.gif" width="1" height="5"><br>
<div align="center"><button unselectable="on" class="fButtonSmall" onClick="setDefaultValues('do'); saveSettings('EditorSettings')" style="width:80"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1014 ?></button><button unselectable="on" class="fButtonSmall" onClick="setDefaultValues('reset'); saveSettings('EditorSettings')" style="width:60"><? install/htdocs/sv/htmleditor/editor/editor.jsp/110 ?></button></div>
<img src="images/1x1.gif" width="1" height="5"><br>
<SPAN CLASS="dim"><? install/htdocs/sv/htmleditor/editor/editor.jsp/111 ?>
<img src="images/1x1.gif" width="1" height="5"><br>
<? install/htdocs/sv/htmleditor/editor/editor.jsp/112/1 ?></SPAN><br>
<img src="images/1x1.gif" width="1" height="5"><br>
<SPAN STYLE="color:CC0000"><sup><? install/htdocs/sv/htmleditor/editor/editor.jsp/113 ?></sup></SPAN><SPAN CLASS="dim">&nbsp;<? install/htdocs/sv/htmleditor/editor/editor.jsp/1015 ?></SPAN>
<input type="hidden" name="previewFontSize" value="">
<input type="hidden" name="previewFontFamily" value="">
</div>









<!--
*****************************************************************************************
*           BOTTOM LEFT PANEL                                                           *
*****************************************************************************************
-->

<script language="JavaScript">
<!--
/*
NOT IN USE

if (showKBshortcuts) {
	document.write('');
} else {
	document.write('<div id="bottomLeftLayer" style="position:absolute; left:10; top:454; display:block" unselectable="on">');
}
*/
function toggleEditorOnOff(on) {
	if (on) {
		setCookie("imcms_use_editor", "true") ;
		document.getElementById("editorOnOffBtn1").style.display = "none" ;
		document.getElementById("editorOnOffBtn0").style.display = "block" ;
	} else {
		setCookie("imcms_use_editor", "") ;
		document.getElementById("editorOnOffBtn1").style.display = "block" ;
		document.getElementById("editorOnOffBtn0").style.display = "none" ;
	}
}
//-->
</script>
<div id="bottomLeftLayer" style="position:absolute; left:10; top:454" unselectable="on">
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td colspan="2"><img src="images/1x1.gif" width="1" height="16"></td>
</tr>
<tr>
	<td nowrap><? install/htdocs/sv/htmleditor/editor/editor.jsp/3000 ?> &nbsp;</td>
	<td><%
	if (getCookie("imcms_use_editor", request).equals("true")) { %>
	<button id="editorOnOffBtn0" onClick="toggleEditorOnOff(0);"
		class="fButtonSmall" style="width:40"><? global/off ?></button>
	<button id="editorOnOffBtn1" onClick="toggleEditorOnOff(1);"
		class="fButtonSmall" style="width:40; border-style:inset; display:none"><? global/on ?></button><%
	} else { %>
	<button id="editorOnOffBtn0" onClick="toggleEditorOnOff(0);"
		class="fButtonSmall" style="width:40; display:none"><? global/off ?></button>
	<button id="editorOnOffBtn1" onClick="toggleEditorOnOff(1);"
		class="fButtonSmall" style="width:40; border-style:inset"><? global/on ?></button><%
	} %></td>
</tr>
</table></div>


<!-- ***** Bottom Right panel ***** -->

<DIV style="position:absolute; left:335; top:465; width:346; text-align:right; z-index:6" unselectable="on">
<img src="images/1x1.gif" width="1" height="5"><br>
<SCRIPT LANGUAGE="JavaScript">
<!--
if (isWordEnabled) {
	document.write('<button id="btnFixWord" unselectable="on" cl'+'ass="fButtonSmall" style="width:100; visibility:hidden" onClick="doFixWordHTML()\;"><? install/htdocs/sv/htmleditor/editor/editor.jsp/116/1 ?></button><img src="images/1x1.gif" width="23" height="1">');
}

if (showCloseBtn) {
	document.write('<button unselectable="on" cl'+'ass="fButtonSmall" style="width:45" onClick="doClose()\;"><? install/htdocs/sv/htmleditor/editor/editor.jsp/116/2 ?></button><img src="images/1x1.gif" width="3" height="1">');
}
if (showResetBtn) {
	document.write('<button unselectable="on" cl'+'ass="fButtonSmall" style="width:45" onClick="if(confirm(\'<? install/htdocs/sv/htmleditor/editor/editor.jsp/116/3 ?>\'))\{ Clear()\; \}"><? install/htdocs/sv/htmleditor/editor/editor.jsp/116/4 ?></button><img src="images/1x1.gif" width="3" height="1">');
}
if (showSaveBtn) {
	document.write('<button unselectable="on" cl'+'ass="fButtonSmall" style="width:47" onClick="doSend()\; return false"><? install/htdocs/sv/htmleditor/editor/editor.jsp/116/5 ?></button><img src="images/1x1.gif" width="3" height="1">');
}
//-->
</SCRIPT>
</div>





<!-- ***** DISABLE BUTTON LAYERS ***** D6D3CE -->

<div id="disableTopBtnDiv" style="position:absolute; left:10; top:22; width:535; height:57; z-index:100; background-color:#D6D3CE; filter:Alpha(Opacity=60); visibility:hidden"></div>

<div id="disableRightBtnDiv" style="position:absolute; left:535; top:102; width:143; height:355; z-index:100; background-color:#D6D3CE; filter:Alpha(Opacity=60); visibility:hidden"></div>

<div id="disableBottomBtnDiv" style="position:absolute; left:5; top:462; width:674; height:33; z-index:100; background-color:#D6D3CE; filter:Alpha(Opacity=60); visibility:hidden"></div>


<div id="modeHtmlCodeDiv" style="position:absolute; left:535; top:80; z-index:4; visibility:hidden" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0" width="141">
<tr>
	<td align="center" bgcolor="#333366" height="22"><? install/htdocs/sv/htmleditor/editor/editor.jsp/117 ?></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"></td>
</tr>
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/118 ?>
	<img src="images/1x1.gif" width="1" height="5"><br>
	<? install/htdocs/sv/htmleditor/editor/editor.jsp/119/1 ?><br>
	<img src="images/1x1.gif" width="1" height="25"><br>
	<SPAN CLASS="dim"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1016 ?></SPAN></td>
</tr>
</table></div>



<div id="helpDescRightDiv" style="position:absolute; left:535; top:80; z-index:4; visibility:hidden" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0" width="141">
<tr>
	<td align="center" bgcolor="#333366" height="22"><? install/htdocs/sv/htmleditor/editor/editor.jsp/120 ?></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"></td>
</tr>
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/121 ?><script language="JavaScript">
<!--
if (showSimple || showAdv) {
	document.writeln('<br><img src="images/1x1.gif" width="1" height="5"><br>')
	document.writeln('<? install/htdocs/sv/htmleditor/editor/editor.jsp/122/1 ?>');
}
//-->
</script></td>
</tr>
</table></div>


<div id="helpAllDescRightDiv" style="position:absolute; left:535; top:80; z-index:4; visibility:hidden" unselectable="on">
<table border="0" cellpadding="0" cellspacing="0" width="141">
<tr>
	<td align="center" bgcolor="#333366" height="22"><? install/htdocs/sv/htmleditor/editor/editor.jsp/123 ?></td>
</tr>
<tr>
	<td><img src="images/1x1.gif" width="1" height="5"></td>
</tr>
<tr>
	<td><? install/htdocs/sv/htmleditor/editor/editor.jsp/124 ?></td>
</tr>
</table></div>

</DIV>

<input type="Hidden" value="" name="theCode"><!-- HTML code when submitted doSend()-->
</form>


<STYLE TYPE="text/css">
<!--
#theColorRedDiv { position:relative; top:+1; width:25; height:2; background-color:#FF0000 }
#theColorGreenDiv { position:relative; top:+1; width:25; height:2; background-color:#00FF00 }
#theColorBlueDiv { position:relative; top:+1; width:25; height:2; background-color:#0000FF }
#theColorDiv { position:relative; top:-1; width:131; height:14; border: 1px inset #FFFFFF; background-color:#000000 }
#theColorText { position:relative; width:131; height:15; color:#000000; text-align: center }
#fRed, #fGreen, #fBlue { border: 2px solid #000000 }
-->
</STYLE>









<!--
*****************************************************************************************
*           ADVANCED SETTINGS PANELS                                                    *
*****************************************************************************************


*****************************************************************************************
*           COLOR PICKER                                                                *
*****************************************************************************************
-->

<div id="modeColorDiv" style="position:absolute; left:535; top:140; z-index:10; visibility:hidden" unselectable="on">
<form name="colorForm">
<table border="0" cellspacing="0" cellpadding="0" align="center">
<tr>
	<td><DIV ID="theColorRedDiv"><img src="images/1x1.gif" width="25" height="2"></DIV><input unselectable="off" type="text" id="fRed" size="3" maxlength="3" class="fField" value="0" style="width:25; height:19" onChange="showColor();" onKeyUp="showColor();" onKeyPress="showColor();"></td>
	<td><a href="#" unselectable="on"><img src="images/btn_adjust_up_down.gif" width="15" height="21" alt="" border="0" usemap="#fRed"></a></td>
	<td><img src="images/1x1.gif" width="5" height="1"></td>
	<td><DIV ID="theColorGreenDiv"><img src="images/1x1.gif" width="25" height="2"></DIV><input unselectable="off" type="text" id="fGreen" size="3" maxlength="3" class="fField" value="0" style="width:25; height:19" onChange="showColor();" onKeyUp="showColor();" onKeyPress="showColor();"></td>
	<td><a href="#" unselectable="on"><img src="images/btn_adjust_up_down.gif" width="15" height="21" alt="" border="0" usemap="#fGreen"></a></td>
	<td><img src="images/1x1.gif" width="5" height="1"></td>
	<td><DIV ID="theColorBlueDiv"><img src="images/1x1.gif" width="25" height="2"></DIV><input unselectable="off" type="text" id="fBlue" size="3" maxlength="3" class="fField" value="0" style="width:25; height:19" onChange="showColor();" onKeyUp="showColor();" onKeyPress="showColor();"></td>
	<td><a href="#" unselectable="on"><img src="images/btn_adjust_up_down.gif" width="15" height="21" alt="" border="0" usemap="#fBlue"></a></td>
</tr>
<tr>
	<td colspan="8"><DIV ID="theColorDiv"></DIV>
	<DIV id="theColorText" unselectable="on">rgb(0,0,0)</DIV></td>
</tr>
</table>
</form></div>

<SCRIPT LANGUAGE="JavaScript">
<!--
function showColor(){
	var iRed = document.forms.colorForm.fRed.value;
	var iGreen = document.forms.colorForm.fGreen.value;
	var iBlue = document.forms.colorForm.fBlue.value;
	iRed = (iRed<0)? 0:iRed
	iRed = (iRed>255)? 255:iRed
	iGreen = (iGreen<0)? 0:iGreen
	iGreen = (iGreen>255)? 255:iGreen
	iBlue = (iBlue<0)? 0:iBlue
	iBlue = (iBlue>255)? 255:iBlue
	var theColor = 'rgb(' + iRed + ',' + iGreen + ',' + iBlue + ')';
	document.getElementById("theColorDiv").style.background = theColor;
	document.getElementById("fRed").style.borderColor = 'rgb('+iRed+',0,0)';
	document.getElementById("fGreen").style.borderColor = 'rgb(0,'+iGreen+',0)';
	document.getElementById("fBlue").style.borderColor = 'rgb(0,0,'+iBlue+')';
	document.getElementById("theColorText").innerText = 'rgb(' + iRed + ',' + iGreen + ',' + iBlue + ')';
	document.forms.editorForm.usecolor.checked = 1;
}

var mouseActiveUp = 0;
var mouseActiveDn = 0;

function fRedUp(){
	clearTimeout('timeDn');
	mouseActiveDn = 0;
	if(mouseActiveUp){
		var iVal = document.forms.colorForm.fRed.value;
		if(iVal == ''){
			iVal = 0;
		} else {
			iVal = parseInt(iVal);
		}
		if(iVal < 205){
			iVal += 51;
		} else {
			iVal = 255;
		}
		document.forms.colorForm.fRed.value = iVal;
		document.forms.colorForm.fRed.focus();
		showColor();
		var timeUp = setTimeout("fRedUp()", 300);
	}
}

function fRedDn(){
	clearTimeout('timeUp');
	mouseActiveUp = 0;
	if(mouseActiveDn){
		var iVal = document.forms.colorForm.fRed.value;
		if(iVal == ''){
			iVal = 0;
		} else {
			iVal = parseInt(iVal);
		}
		if(iVal > 50){
			iVal -= 51;
		} else {
			iVal = 0;
		}
		document.forms.colorForm.fRed.value = iVal;
		document.forms.colorForm.fRed.focus();
		showColor();
		var timeDn = setTimeout("fRedDn()", 300);
	}
}
//-->
</SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
<!--
var mouseActiveUp = 0;
var mouseActiveDn = 0;

function fGreenUp(){
	clearTimeout('timeDn');
	mouseActiveDn = 0;
	if(mouseActiveUp){
		var iVal = document.forms.colorForm.fGreen.value;
		if(iVal == ''){
			iVal = 0;
		} else {
			iVal = parseInt(iVal);
		}
		if(iVal < 205){
			iVal += 51;
		} else {
			iVal = 255;
		}
		document.forms.colorForm.fGreen.value = iVal;
		document.forms.colorForm.fGreen.focus();
		showColor();
		var timeUp = setTimeout("fGreenUp()", 300);
	}
}

function fGreenDn(){
	clearTimeout('timeUp');
	mouseActiveUp = 0;
	if(mouseActiveDn){
		var iVal = document.forms.colorForm.fGreen.value;
		if(iVal == ''){
			iVal = 0;
		} else {
			iVal = parseInt(iVal);
		}
		if(iVal > 50){
			iVal -= 51;
		} else {
			iVal = 0;
		}
		document.forms.colorForm.fGreen.value = iVal;
		document.forms.colorForm.fGreen.focus();
		showColor();
		var timeDn = setTimeout("fGreenDn()", 300);
	}
}
//-->
</SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
<!--
var mouseActiveUp = 0;
var mouseActiveDn = 0;

function fBlueUp(){
	clearTimeout('timeDn');
	mouseActiveDn = 0;
	if(mouseActiveUp){
		var iVal = document.forms.colorForm.fBlue.value;
		if(iVal == ''){
			iVal = 0;
		} else {
			iVal = parseInt(iVal);
		}
		if(iVal < 205){
			iVal += 51;
		} else {
			iVal = 255;
		}
		document.forms.colorForm.fBlue.value = iVal;
		document.forms.colorForm.fBlue.focus();
		showColor();
		var timeUp = setTimeout("fBlueUp()", 300);
	}
}

function fBlueDn(){
	clearTimeout('timeUp');
	mouseActiveUp = 0;
	if(mouseActiveDn){
		var iVal = document.forms.colorForm.fBlue.value;
		if(iVal == ''){
			iVal = 0;
		} else {
			iVal = parseInt(iVal);
		}
		if(iVal > 50){
			iVal -= 51;
		} else {
			iVal = 0;
		}
		document.forms.colorForm.fBlue.value = iVal;
		document.forms.colorForm.fBlue.focus();
		showColor();
		var timeDn = setTimeout("fBlueDn()", 300);
	}
}
//-->
</SCRIPT>

<map name="fRed">
	<area alt="" coords="0,0,14,9"
		href="#" onMouseDown="mouseActiveUp = 1; fRedUp();" onMouseUp="mouseActiveUp = 0; clearTimeout('timeDn');">
	<area alt="" coords="0,11,15,21"
		href="#" onMouseDown="mouseActiveDn = 1; fRedDn();" onMouseUp="mouseActiveDn = 0; clearTimeout('timeUp');">
</map>
<map name="fGreen">
	<area alt="" coords="0,0,14,9"
		href="#" onMouseDown="mouseActiveUp = 1; fGreenUp();" onMouseUp="mouseActiveUp = 0; clearTimeout('timeDn');">
	<area alt="" coords="0,11,15,21"
		href="#" onMouseDown="mouseActiveDn = 1; fGreenDn();" onMouseUp="mouseActiveDn = 0; clearTimeout('timeUp');">
</map>
<map name="fBlue">
	<area alt="" coords="0,0,14,9"
		href="#" onMouseDown="mouseActiveUp = 1; fBlueUp();" onMouseUp="mouseActiveUp = 0; clearTimeout('timeDn');">
	<area alt="" coords="0,11,15,21"
		href="#" onMouseDown="mouseActiveDn = 1; fBlueDn();" onMouseUp="mouseActiveDn = 0; clearTimeout('timeUp');">
</map>








<!-- ***** HELP LAYERS ***** -->

<div id="helpDiv" unselectable="off" style="position:absolute; left:5; top:80; width:525; height:380; z-index:200; background-color:#FFFFE0; text-align: left; padding:0; border: 2px inset #D6D3CE; visibility: hidden">
<table border="0" cellpadding="5" cellspacing="0" width="100%">
<tr>
	<td CLASS="imEditHelpHeadingBig"><? install/htdocs/sv/htmleditor/editor/editor.jsp/129 ?></td>
	<td align="right"><button id="hideHelpBtn" class="fButtonSmall" onClick="hideHelp('all');"><? install/htdocs/sv/htmleditor/editor/editor.jsp/130 ?></button></td>
</tr>
</table></div>


<div id="helpSubjectDiv" contenteditable="false" unselectable="off" style="position:absolute; left:5; top:120; width:525; height:338; z-index:201; text-align: left; padding:0; visibility:hidden; overflow=auto" class="imEditHelp">
<div id="helpHeadingDiv" style="position:relative; left:8; width:510"></div>
<div id="helpContentDiv" class="imEditHelp" style="position:relative; top:10; left:18; width:500"></div>
</div>





<div id="helpTextAllDiv" contenteditable="false" unselectable="off" style="position:absolute; left:5; top:80; width:525; height:380; z-index:201; text-align: left; padding:5; visibility:hidden; background-color:#FFFFE0; border: 2px inset #D6D3CE; overflow=auto">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td CLASS="imEditHelpHeadingBig"><? install/htdocs/sv/htmleditor/editor/editor.jsp/131 ?></td>
	<td align="right"><button class="fButtonSmall" onClick="hideHelp('all');"><? install/htdocs/sv/htmleditor/editor/editor.jsp/132 ?></button></td>
</tr>
</table><br>

<b>|&nbsp; <span class="imEditHelpLinkActive"><? install/htdocs/sv/htmleditor/editor/editor.jsp/133 ?></span> &nbsp;|&nbsp; <a href="javascript:showHelp('upperbuttons');" class="imEditHelpLink"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1018/1 ?></a> &nbsp;|&nbsp; <a href="javascript:showHelp('rightbuttons');" class="imEditHelpLink"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1018/2 ?></a> &nbsp;|&nbsp;</b><br><br>

<span class="imEditHelpHeading"><? install/htdocs/sv/htmleditor/editor/editor.jsp/134 ?></span><br><br>

Lorem ipsum, dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.<br><br>

Duis autem, vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.</div>


<div id="helpTextAllUpperDiv" contenteditable="false" unselectable="off" style="position:absolute; left:5; top:80; width:525; height:380; z-index:201; text-align: left; padding:5; visibility:hidden; background-color:#FFFFE0; border: 2px inset #D6D3CE; overflow=auto" class="imEditHelp">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td CLASS="imEditHelpHeadingBig"><? install/htdocs/sv/htmleditor/editor/editor.jsp/136 ?></td>
	<td align="right"><button class="fButtonSmall" onClick="hideHelp('all');"><? install/htdocs/sv/htmleditor/editor/editor.jsp/137 ?></button></td>
</tr>
</table><br>

<b>|&nbsp; <a href="javascript:showHelp('all');" class="imEditHelpLink"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1019/1 ?></a> &nbsp;|&nbsp; <span class="imEditHelpLinkActive"><? install/htdocs/sv/htmleditor/editor/editor.jsp/138 ?></span> &nbsp;|&nbsp; <a href="javascript:showHelp('rightbuttons');" class="imEditHelpLink"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1020/1 ?></a> &nbsp;|&nbsp;</b><br><br>

<span class="imEditHelpHeading"><? install/htdocs/sv/htmleditor/editor/editor.jsp/139 ?></span><br><br>

Lorem ipsum, dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.<br><br>

Duis autem, vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.

Lorem ipsum, dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.<br><br>

Duis autem, vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.<br><br>

Lorem ipsum, dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.<br><br>

Duis autem, vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.<br><br>

Lorem ipsum, dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.<br><br>

Duis autem, vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.</div>


<div id="helpTextAllRightDiv" contenteditable="false" unselectable="off" style="position:absolute; left:5; top:80; width:525; height:380; z-index:201; text-align: left; padding:5; visibility:hidden; background-color:#FFFFE0; border: 2px inset #D6D3CE; overflow=auto" class="imEditHelp">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td CLASS="imEditHelpHeadingBig"><? install/htdocs/sv/htmleditor/editor/editor.jsp/141 ?></td>
	<td align="right"><button class="fButtonSmall" onClick="hideHelp('all');"><? install/htdocs/sv/htmleditor/editor/editor.jsp/142 ?></button></td>
</tr>
</table><br>

<b>|&nbsp; <a href="javascript:showHelp('all');" class="imEditHelpLink"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1021/1 ?></a> &nbsp;|&nbsp; <a href="javascript:showHelp('upperbuttons');" class="imEditHelpLink"><? install/htdocs/sv/htmleditor/editor/editor.jsp/1021/2 ?></a> &nbsp;|&nbsp; <span class="imEditHelpLinkActive"><? install/htdocs/sv/htmleditor/editor/editor.jsp/143 ?></span> &nbsp;|&nbsp;</b><br><br>

<span class="imEditHelpHeading"><? install/htdocs/sv/htmleditor/editor/editor.jsp/144 ?></span><br><br>

Lorem ipsum, dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.<br><br>

Duis autem, vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.</div>

<script type="text/javascript">
<!--
var all = document.all;
var l = all.length;
for (var i = 0; i < l; i++) {
	if (all[i].tagName != "INPUT" && all[i].tagName != "TEXTAREA")
		all[i].unselectable = "on";
}
document.all.editorDiv.unselectable = "off";
// -->
</script>

<div id="waitingDiv" style="position:absolute; left:5; top:463; width:400; height:30; z-index:1000; text-align:left; padding:5; background-color:#D6D3CE; display:none" class="imEditHelpHeadingBig" style="font-size:20px"><? install/htdocs/sv/htmleditor/editor/editor.jsp/146 ?></div>

<div id="theOriginalCodeDiv" contenteditable style="position:absolute; left:-5000; top:-5000"><%= orgContent %></div>

<div id="theSavedCodeDiv" contenteditable style="position:absolute; left:-5000; top:-5000"><%= txtContent %></div>


<form name="saveForm" action="editor.jsp" method="post">
	<input type="hidden" name="action" value="ExecSave">
	<input type="hidden" name="meta_id" value="<%= meta_id %>">
	<input type="hidden" name="txt" value="<%= txt_no %>">
	<input type="hidden" name="label" value="<%= label %>">
	<input type="Hidden" name="orgContent" value="">
	<input type="Hidden" name="txtContent" value="">
</form>

<form name="previewForm" method="post">
	<input type="hidden" name="winW" value="675">
	<input type="hidden" name="winH" value="510">
	<input type="hidden" name="winDesc" value="OnLine+HTML%2DEditor">
	<input type="Hidden" name="html" value="0">
	<input type="Hidden" name="theCode" value=""><!-- HTML code on preview -->
</form>

</body>
</html>

