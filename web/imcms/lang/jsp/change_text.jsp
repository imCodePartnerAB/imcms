<%@ page

	import="imcode.server.document.textdocument.TextDomainObject,
	        com.imcode.imcms.servlet.admin.ChangeText,
	        org.apache.commons.lang.StringEscapeUtils,
            org.apache.commons.lang.StringUtils"

	contentType="text/html"

%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"
%><%

ChangeText.TextEditPage textEditPage = (ChangeText.TextEditPage) request.getAttribute(ChangeText.TextEditPage.REQUEST_ATTRIBUTE__PAGE);

int EDITED_META  = textEditPage.getDocumentId() ;

boolean imcmsModeHtml    = (textEditPage.getType() == TextDomainObject.TEXT_TYPE_HTML) ;
boolean imcmsModeText    = !imcmsModeHtml ;
boolean showEditorCookie = !getCookie("imcms_hide_editor", request).equals("true") ;
%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/change_text.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>
<%@ include file="../../htmlarea/_editor_scripts.jsp" %>

</head>
<body bgcolor="#FFFFFF" style="margin-bottom:0px;" onLoad="checkMode();<%=
(isHtmlAreaSupported && showModeEditor) ? " checkEditor(" + ((imcmsModeText && !textEditPage.getTextString().equals("")) ? "true":"false") + ");" : "" %>"<%=
(isHtmlAreaSupported) ? " onResize=\"setEditorSize()\"" : "" %>>

<div id="theLabel" style="display:none"><i><%= StringEscapeUtils.escapeHtml( textEditPage.getLabel() ) %></i></div>


#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<table border="0" cellspacing="0" cellpadding="0" width="100%">
<form method="POST" action="SaveText">
<input type="hidden" name="meta_id"  value="<%= textEditPage.getDocumentId() %>">
<input type="hidden" name="txt_no"   value="<%= textEditPage.getTextIndex() %>">
<input type="hidden" name="type"     value="<%= textEditPage.getType() %>">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="SUBMIT" value="<? global/back ?>" name="cancel" class="imcmsFormBtn"></td><%
		if (isHtmlAreaSupported && showModeEditor) { %>
		<td style="color:#ffffff;" nowrap><span id="editorOpenText" style="display:none">&nbsp; &nbsp; <? install/htdocs/sv/htmleditor/editor/editor.jsp/3000 ?> &nbsp;</span></td>
		<td><span id="editorOpenBtn" style="display:none"><%
			if (getCookie("imcms_hide_editor", request).equals("true")) { %>
		<button id="editorOnOffBtn0" onClick="toggleEditorOnOff(0); return false"
			class="imcmsFormBtn" style="width:40"><? global/off ?></button>
		<button id="editorOnOffBtn1" onClick="toggleEditorOnOff(1); return false"
			class="imcmsFormBtnActive" style="width:40; display:none"><? global/on ?></button><%
			} else { %>
		<button id="editorOnOffBtn0" onClick="toggleEditorOnOff(0); return false"
			class="imcmsFormBtn" style="width:40; display:none"><? global/off ?></button>
		<button id="editorOnOffBtn1" onClick="toggleEditorOnOff(1); return false"
			class="imcmsFormBtnActive" style="width:40;"><? global/on ?></button><%
			} %></span></td><%
		} %>
	</tr>
	</table></td>

	<td align="right">
	<input type="button" value="<? templates/sv/change_text.html/2004 ?>" title="<? templates/sv/change_text.html/2005 ?>" class="imcmsFormBtn" onClick="openHelpW(36)"></td>

</tr>
</table>

#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<tr>#set( $textIndex = "<%= textEditPage.getTextIndex() %>" )#set( $documentId = "<%= textEditPage.getDocumentId() %>" )
	<td colspan="2">#gui_heading( "<? templates/sv/change_text.html/4/1 ?><%=
	(textEditPage != null && StringUtils.isNotBlank(textEditPage.getLabel()))
		? " &nbsp;&#150;&nbsp; <i>" + StringEscapeUtils.escapeHtml(textEditPage.getLabel()) + "</i>" : "" %>" )</td>
</tr>
<tr>
	<td colspan="2" class="imcmsAdmForm"><%
	if (rows == 1) { %>
	<input type="text" name="text" id="txtCont" style="width:100%" value="<%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %>"><%
	} else { %>
	<textarea name="text" id="txtCont" cols="125" rows="<%= (rows > 1) ? rows : 18 %>" style="overflow: auto; width: 100%" wrap="virtual">
<%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %></textarea><%
	} %></td>
</tr>
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="imcmsAdmText">&nbsp;<? templates/sv/change_text.html/1001 ?>&nbsp;</td><%
		if (showModeText) { %>
			<td><input type="RADIO" name="format_type" id="format_type0" value="0"<%
			%><%= (imcmsModeText) ? "" : " checked" %>></td>
			<td class="imcmsAdmText">
			<label for="format_type0" accesskey="T" title="Text (<%= isMac ? "Ctrl" : "Alt" %> + T)">
			&nbsp;<u>T</u>ext&nbsp;</label>&nbsp;</td><%
		}
		if (showModeHtml) { %>
			<td><input type="RADIO" name="format_type" id="format_type1" value="1"<%
			%><%= (isHtmlAreaSupported && showModeEditor) ? " onClick=\"showHideHtmlArea(false);\"" : "" %><%
			%><%= ((imcmsModeHtml || (imcmsModeText && !showModeText)) && (!showEditorCookie || !showModeEditor || !showModeText)) ? " checked" : "" %>></td>
			<td class="imcmsAdmText">
			<label for="format_type1" accesskey="H" title="HTML (<%= isMac ? "Ctrl" : "Alt" %> + H)">
			&nbsp;<u>H</u>TML&nbsp;</label>&nbsp;</td><%
		}
		if (isHtmlAreaSupported && showModeEditor) { %>
			<td><input type="RADIO" name="format_type" id="format_type2" style="display:none" value="1" <%
			%>onClick="showHideHtmlArea(true);"<%
			%><%= (imcmsModeHtml && showEditorCookie) ? " checked" : "" %>></td>
			<td class="imcmsAdmText">
			<label for="format_type2" id="format_type2_label" style="display:none" accesskey="<%= isMac ? "D" : "E" %>" title="Editor (<%= isMac ? "Ctrl" : "Alt" %> + <%= isMac ? "D" : "E" %>)">
			&nbsp;<%= isMac ? "E<u>d</u>itor" : "<u>E</u>ditor" %>&nbsp;</label>&nbsp;</td><%
		} %>
		</tr>
		</table></td>
		<td align="right">
		<input type="SUBMIT" class="imcmsFormBtn" name="ok" value="  <? templates/sv/change_text.html/2006 ?>  ">
		<input type="RESET" class="imcmsFormBtn" value="<? templates/sv/change_text.html/2007 ?>">
		<input type="SUBMIT" class="imcmsFormBtn" name="cancel" value=" <? templates/sv/change_text.html/2008 ?> "></td>
	</tr>
	</table></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()

<script language="javascript">
<!--
var winW = (document.all) ? document.body.offsetWidth - 20 : (document.getElementById) ? document.body.clientWidth  : 0 ;
var winH = (document.all) ? document.body.offsetHeight - 4 : (document.getElementById) ? document.body.clientHeight : 0 ;

function setEditorSize() {
	if (document.getElementById) {
		winW  = (document.all) ? document.body.offsetWidth - 20 : document.body.clientWidth ;
		editW = winW - 130 ;
		editW = (editW < 650) ? 650 : editW ;
		document.getElementById("txtCont").style.width    = editW ;
		if (document.getElementById("theIframe")) {
			document.getElementById("theIframe").style.width    = editW ;
		}<%
		if (rows == -1) { %>
		winH  = (document.all) ? document.body.offsetHeight - 4 : document.body.clientHeight ;
		editH = winH - 250 ;
		editH = (editH < 250) ? 250 : editH ;
		document.getElementById("txtCont").style.height   = editH ;
		if (document.getElementById("theIframe")) {
			document.getElementById("theIframe").style.height   = editH - 41 ;
		}<%
		} %>
		document.getElementById("txtCont").style.overflow = "auto";
	}
}
setEditorSize() ;

function checkMode() {
	var f = document.forms[0] ;
	window.status = f.format_type + "" + (f.format_type.length > 0) + "" ;
	if (!(f.format_type.length > 0)) {
		f.format_type.checked = true ;
	} else if (f.format_type.length == 2 && !f.format_type[0].checked && !f.format_type[1].checked) {
		f.format_type[0].checked = true ;
	}
}

if (document.getElementById) {
	document.getElementById("editorOpenText").style.display     = "block" ;
	document.getElementById("editorOpenBtn").style.display      = "block" ;
	document.getElementById("format_type2").style.display       = "block" ;
	document.getElementById("format_type2_label").style.display = "inline" ;
}
//-->
</script>

</body>
</html>
</vel:velocity>
