<%@ page import="imcode.server.document.textdocument.TextDomainObject,
                 com.imcode.imcms.servlet.admin.ChangeText,
                 org.apache.commons.lang.StringEscapeUtils"%>

<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<%
    ChangeText.TextEditPage textEditPage = (ChangeText.TextEditPage) request.getAttribute(ChangeText.TextEditPage.REQUEST_ATTRIBUTE__PAGE);

%>

<vel:velocity>
<html>
<head>
<title><? templates/sv/change_text.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

<script language="JavaScript">
<!--
/* HTML-EDITOR - OPEN FUNCTION */

function openEditor(metaId,txtNr) {
	if(isIE55) {
		var sLabel = document.getElementById("theLabel").innerText;
		sLabel = sLabel.replace(/^\s*(.*)\s*$/g, "$1");
		var sUrl = "$contextPath/imcms/$language/htmleditor/editor.jsp?meta_id=" + metaId + "&txt=" + txtNr + "&label=" + sLabel;
		var iWidth = 680;
		var iHeight = 495;
		var sMenu = 0;
		var sScroll = 0;
		var sStatus = 0;
		var window_width = iWidth;
		var window_height = iHeight;
		if(screen.height < 700){
			var window_top = 0;
			var window_left = (screen.width-window_width)/2;
		} else {
			var window_top = (screen.height-window_height)/2;
			var window_left = (screen.width-window_width)/2;
		}
		sUrl = (location.search.indexOf("admin") != -1) ? sUrl + "?admin=1" : sUrl;
		var popWindow = window.open(sUrl,"Editor","resizable=no,menubar=" + sMenu + ",scrollbars=" + sScroll + ",status=" + sStatus + ",width=" + window_width + ",height=" + window_height + ",top=" + window_top + ",left=" + window_left + ",location=0,directories=0");
		if (popWindow) popWindow.focus();
	} else {
		var sMess = "<? templates/sv/change_text.html/13 ?> ";
		sMess += (isMac) ? "<? templates/sv/change_text.html/14 ?> " : "";
		sMess += "<? templates/sv/change_text.html/15 ?>";
		alert(sMess);
	}
}

function openLinkEditor() {
	popWinOpen(450,395,"$contextPath/imcms/$language/html/link_editor.jsp","linkEditorWin",1,0) ;
}

function readCookie(name) {
	if (document.cookie) {
		var namearg = name + "=";
		var nlen = namearg.length;
		var clen = document.cookie.length;
		var i = 0;
		while (i < clen) {
			var j = i + nlen;
			if (document.cookie.substring(i, j) == namearg) {
				var endpos = document.cookie.indexOf(";", j);
				if (endpos == -1) endpos = document.cookie.length;
				return unescape(document.cookie.substring(j, endpos));
			}
			i = document.cookie.indexOf(" ", i) + 1;
			if (i == 0) break;
		}
		return "" ;
	} else {
		return "" ;
	}
}
//-->
</script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(0,'text'); checkMode();">

<div id="theLabel" style="display:none"><i><%= textEditPage.getLabel() %></i></div>

<script language="JavaScript">
<!--
if(isIE55 && readCookie("imcms_use_editor") != "true") {
	openEditor("<%= textEditPage.getDocumentId() %>","<%= textEditPage.getTextIndex() %>") ;
	document.location = "AdminDoc?meta_id=<%= textEditPage.getDocumentId() %>&flags=65536" ;
}
//-->
</script>

#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<table border="0" cellspacing="0" cellpadding="0">
<form method="POST" action="SaveText">
<input type="hidden" name="meta_id"  value="<%= textEditPage.getDocumentId() %>">
<input type="hidden" name="txt_no"   value="<%= textEditPage.getTextIndex() %>">
<input type="hidden" name="type"     value="<%= textEditPage.getType() %>">
<tr>
	<td><input type="SUBMIT" value="<? templates/sv/change_text.html/2001 ?>" name="cancel" class="imcmsFormBtn"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? templates/sv/change_text.html/2002 ?>" title="<? templates/sv/change_text.html/2003 ?>" name="editorBtn" class="imcmsFormBtn" onClick="openEditor('<%= textEditPage.getDocumentId() %>','<%= textEditPage.getTextIndex() %>')"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? templates/sv/change_text.html/2004 ?>" title="<? templates/sv/change_text.html/2005 ?>" class="imcmsFormBtn" onClick="openHelpW(36)"></td>
</tr>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<tr>
	<td colspan="2">#gui_heading( "<? templates/sv/change_text.html/4/1 ?>" )<div class="imcmsAdmText"><i><%= textEditPage.getLabel() %></i></div></td>
</tr>
<tr>
	<td colspan="2" class="imcmsAdmForm"><textarea name="text" id="txtCont" cols="125" rows="18" style="overflow: auto; width: 100%" wrap="virtual">
<%= textEditPage.getTextString() %></textarea></td>
</tr>
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="imcmsAdmText">&nbsp;<? templates/sv/change_text.html/1001 ?>&nbsp;</td>
			<td><input type="RADIO" name="format_type"  value="0" onClick="checkMode()" onChange="checkMode()" <%= textEditPage.getType() == TextDomainObject.TEXT_TYPE_HTML ? "" : "checked" %>></td>
			<td class="imcmsAdmText">&nbsp;<? templates/sv/change_text.html/1002 ?>&nbsp;&nbsp;</td>
			<td><input type="RADIO" name="format_type"  value="1" onClick="checkMode()" onChange="checkMode()" <%= textEditPage.getType() == TextDomainObject.TEXT_TYPE_HTML ? "checked" : "" %>></td>
			<td class="imcmsAdmText">&nbsp;<? templates/sv/change_text.html/1003 ?></td>
		</tr>
		</table></td>
		<td align="right">
		<input type="SUBMIT" value="  <? templates/sv/change_text.html/2006 ?>  " name="ok" class="imcmsFormBtn">
		<input type="RESET" value="<? templates/sv/change_text.html/2007 ?>" class="imcmsFormBtn">
		<input type="SUBMIT" value=" <? templates/sv/change_text.html/2008 ?> " name="cancel" class="imcmsFormBtn"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="2">&nbsp;<br>#gui_heading( '<? templates/sv/change_text.html/8/1 ?> <span class="imcmsAdmDim"><? templates/sv/change_text.html/8/2 ?></span>' )</td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/change_text.html/9 ?></td>
	<td align="right"><input type="button" value="<? templates/sv/change_text.html/2009 ?>" name="replaceBreakBtn" id="replaceBreakBtn" onclick="replaceBreaks();" width="120" style="width:120" class="imcmsFormBtnSmall"></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/change_text.html/10 ?></td>
	<td align="right"><input type="button" value="<? templates/sv/change_text.html/2010 ?>" name="createLinkBtn" id="createLinkBtn" onclick="openLinkEditor();" width="120" style="width:120" class="imcmsFormBtnSmall"></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()



<script language="javascript">
<!--
if (hasGetElementById) {
	document.getElementById("txtCont").style.width    = 656;
	document.getElementById("txtCont").style.overflow = "auto";
}

function checkMode() {
	var f = document.forms[0] ;
	var elFormatRadio = (f.format_type) ? f.format_type : f.type ;
	if (elFormatRadio) {
		var oBtn1 = (hasGetElementById) ? document.getElementById("replaceBreakBtn") : f.replaceBreakBtn ;
		var oBtn2   = (hasGetElementById) ? document.getElementById("createLinkBtn"): f.createLinkBtn ;
		if (elFormatRadio[1].checked) { // HTML
			oBtn1.disabled = 0 ;
			oBtn2.disabled = 0 ;
			if (hasGetElementById) {
				oBtn1.className = "imcmsFormBtnSmall" ;
				oBtn2.className = "imcmsFormBtnSmall" ;
			}
		} else { // Text
			oBtn1.disabled = 1 ;
			oBtn2.disabled = 1 ;
			if (hasGetElementById) {
				oBtn1.className = "imcmsFormBtnSmallDisabled" ;
				oBtn2.className = "imcmsFormBtnSmallDisabled" ;
			}
		}
	}
}

function replaceBreaks() {
	var f = document.forms[0];
	var theText = f.text.value;
	if (!(f.format_type[1].checked == 1)) {
		if(confirm("<? templates/sv/change_text.html/12 ?>")){
			f.format_type[1].checked = 1;
			doReplaceBreaks();
		}
	} else {
		doReplaceBreaks();
	}
}

function doReplaceBreaks() {
	var f = document.forms[0];
	var theText = f.text.value;
	var tempText = theText.toUpperCase();
	var reFr = (hasGetElementById && !hasDocumentAll) ? /\n/g : /\r/g;
	var reTo = (hasGetElementById && !hasDocumentAll) ? "<BR>\n" : "<BR>\r";
	if (tempText.indexOf("<BR>") < 0) {
		theText = theText.replace(reFr,reTo);
		f.text.value = theText;
	} else {
		if (confirm("<? templates/sv/change_text.html/2020 ?>")) {
			theText = theText.replace(reFr,reTo);
			f.text.value = theText;
		}
	}
}
//-->
</script>
</body>
</html>
</vel:velocity>
