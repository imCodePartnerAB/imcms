<%@ page

	import="com.imcode.imcms.servlet.admin.ChangeText,
	        imcode.server.Imcms,
	        imcode.server.ImcmsConstants,
	        imcode.server.LanguageMapper,
	        imcode.server.document.textdocument.TextDomainObject,
	        imcode.util.Utility,
	        org.apache.commons.lang.StringEscapeUtils,
	        java.util.ArrayList,
	        java.util.Arrays,
	        java.util.List,
	        com.imcode.imcms.api.ContentManagementSystem,
	        com.imcode.imcms.servlet.AjaxServlet"

    contentType="text/html; charset=UTF-8"

%><%@taglib prefix="vel" uri="imcmsvelocity"
%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%

boolean DEBUG_SESSION_TIMER = false ;
boolean DEBUG_VALIDATION    = false ;
boolean DEBUG_EDITOR        = false ;
boolean DEBUG_CHANGED       = false ;
boolean DEBUG_CHANGED_CONT  = false ;
boolean DEBUG_SAVE          = false ;

%><%!

private String getCookie( String name, HttpServletRequest request ) {
	String retVal = "" ;
	Cookie[] cookies = request.getCookies() ;
	if (null != cookies) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				retVal = cookie.getValue() ;
				break ;
			}
		}
	}
	return retVal ;
}

%><%

String cp = request.getContextPath() ;

response.setContentType( "text/html; charset=" + Imcms.DEFAULT_ENCODING );
ChangeText.TextEditPage textEditPage = (ChangeText.TextEditPage) request.getAttribute(ChangeText.TextEditPage.REQUEST_ATTRIBUTE__PAGE);

List<String> formats = new ArrayList<String>();
String[] formatParameterValues = textEditPage.getFormats();
if (null != formatParameterValues) {
    formats.addAll(Arrays.asList(formatParameterValues));
    formats.remove("");
}

boolean showModeEditor = formats.isEmpty();
boolean showModeText   = formats.contains("text") || showModeEditor;
boolean showModeHtml   = formats.contains("html") || formats.contains("none") || showModeEditor ;
boolean editorHidden   = getCookie("imcms_hide_editor", request).equals("true") ;
int rows = (null != textEditPage.getRows() && textEditPage.getRows().matches("^\\d+$")) ? Integer.parseInt(textEditPage.getRows()) : 0 ;

if (rows > 0) {
	showModeEditor = false;
}

int width = (null != textEditPage.getWidth() && textEditPage.getWidth().matches("^\\d+$")) ? Integer.parseInt(textEditPage.getWidth()) : 0 ;

if (!(width >= 150 && width <= 600)) {
	width = 0 ;
}

boolean editorActive = (TextDomainObject.TEXT_TYPE_HTML == textEditPage.getType() && !editorHidden) ;
boolean validationIsActive = !"false".equals(getCookie("validationActive", request)) ;

boolean isSwe = false ;
try {
	isSwe =	ContentManagementSystem.fromRequest(request).getCurrentUser().getLanguage().getIsoCode639_2().equals("swe");
} catch (Exception e) {}

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<vel:velocity>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="sv" lang="sv">
<head>
	<title><? templates/sv/change_text.html/1 ?></title>

	<link rel="stylesheet" type="text/css" href="<%= cp %>/imcms/css/imcms_admin.css.jsp" />
	<link rel="stylesheet" type="text/css" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/themes/redmond/jquery-ui.css" />
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"></script>
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/jquery-ui.min.js"></script>
	<script src="<%= cp %>/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
	<script type="text/javascript" src="<%= cp %>/imcms/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="<%= cp %>/imcms/ckeditor/plugins/imcms_integration/init.js.jsp"></script>

</vel:velocity>
<style type="text/css">
body {
	<%= "overflow: -moz-scrollbars-vertical;" %>
}
</style>
<vel:velocity>

</head>
<body bgcolor="#fff" style="margin-bottom:0;">


<form id="mainForm" method="POST" action="<%= cp %>/servlet/SaveText">
<input type="hidden" name="meta_id" value="<%= textEditPage.getDocumentId() %>" />
<input type="hidden" name="txt_no" value="<%= textEditPage.getTextIndex() %>" /><%
if (null != textEditPage.getLabel() && !"".equals(textEditPage.getLabel())) { %>
<input type="hidden" name="label" value="<%= StringEscapeUtils.escapeHtml(textEditPage.getLabel()) %>" /><%
}
if (null != textEditPage.getFormats()) {
	for (String format : textEditPage.getFormats()) { %>
<input type="hidden" name="format" value="<%= StringEscapeUtils.escapeHtml(format) %>" /><%
	}
}
if (null != textEditPage.getWidth() && !"".equals(textEditPage.getWidth())) { %>
<input type="hidden" name="width" value="<%= StringEscapeUtils.escapeHtml(textEditPage.getWidth()) %>" /><%
}
if (rows > 0) { %>
<input type="hidden" name="rows"  value="<%=rows%>" /><%
}
if (null != textEditPage.getReturnUrl() && !"".equals(textEditPage.getReturnUrl())) { %>
<input type="hidden" name="<%= ImcmsConstants.REQUEST_PARAM__RETURN_URL %>" value="<%= StringEscapeUtils.escapeHtml(textEditPage.getReturnUrl()) %>" /><%
} %>

#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
		<input type="submit" id="backBtnTop" value="<? global/back ?>" name="cancel" class="imcmsFormBtn leaveChangeTextBtn" /></td><%
		if (showModeEditor) { %>
		<td style="color:#fff; padding: 0 10px 0 20px;" nowrap="nowrap"><? install/htdocs/sv/htmleditor/editor/editor.jsp/3000 ?></td>
		<td><%
			if (editorHidden) { %>
		<button id="editorOnOffBtn0" onclick="toggleEditorOnOff(0); return false"
			class="imcmsFormBtn" style="width:40px;"><? global/off ?></button>
		<button id="editorOnOffBtn1" onclick="toggleEditorOnOff(1); return false"
			class="imcmsFormBtnActive" style="width:40px; display:none"><? global/on ?></button><%
			} else { %>
		<button id="editorOnOffBtn0" onclick="toggleEditorOnOff(0); return false"
			class="imcmsFormBtn" style="width:40px; display:none"><? global/off ?></button>
		<button id="editorOnOffBtn1" onclick="toggleEditorOnOff(1); return false"
			class="imcmsFormBtnActive" style="width:40px;"><? global/on ?></button><%
			} %></td><%
		} %>
		<td style="color:#fff; padding-left:20px;" nowrap="nowrap">
			<div id="contentChangedDiv" style="display:block; float:left; margin-right:10px;">
				<div id="contentChangedDiv_true" style="display:none; color:#faa; font-style:italic;">
					<%= isSwe ? "Innehållet har ändrats!" : "The content has been changed!" %>
				</div>
				<div id="contentChangedDiv_false" style="display:block; color:#afa; font-style:italic;">
					<%= "&nbsp;" %>
				</div>
			</div>
			<div id="contentSavedDiv_true" style="display:none; float:left; color:#afa; font-style:italic; font-weight:bold;">
				<%= isSwe ? "Innehållet har sparats!" : "The content has been saved!" %>
			</div>
			<div id="contentSavedDiv_false" style="display:none; float:left; color:#faa; font-style:italic; font-weight:bold;">
				<%= isSwe ? "Innehållet kunde inte sparats!" : "The content could not be saved!" %>
			</div>
			<div style="clear:both;"></div>
		</td>
	</tr>
	</table></td>

	<td align="right">
	<input type="button" tabindex="12" value="<? templates/sv/change_text.html/2004 ?>" title="<? templates/sv/change_text.html/2005 ?>" class="imcmsFormBtn" onclick="openHelpW('EditText')" /></td>

</tr>
</table>

#gui_mid()

<table id="mainTable" border="0" cellspacing="0" cellpadding="2" width="720" align="center">
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="2" width="100%;">
	<tr valign="top">
		<td width="80%">
		<div id="theLabel"><%= StringEscapeUtils.escapeHtml( textEditPage.getLabel() ) %></div>
		<div id="messageDiv" style="display:none; color:#cc0000; padding:10px 0;"></div></td>
		
		<td width="20%" align="right" style="padding-top:3px; padding-left:15px; white-space:nowrap;">
		<label for="validationActive" class="toolTip" title="<%=
			StringEscapeUtils.escapeHtml(isSwe ?
					"Om denna är ikryssad körs automatiskt en<br/>" +
					"validering på texten när en ändring registerats.<br/>" +
					"En validering körs alltid när sidan laddats.<br/>" +
					"Man kan även köra en manuell validering<br/>" +
					"med knappen &#91;Validera texten&#93;.<br/>" +
					"Automatisk validering kräver en del kraft,<br/>" +
					"så stäng av den när den inte behövs." :
					
					"If this is checked a validation is automatically executed<br/>" +
					"on the text when changes in the text has been registered.<br/>" +
					"A validation is always executed when the page is loaded.<br/>" +
					"You may also run a manual validation<br/>" +
					"with the button &#91;Validate the text&#93;.<br/>" +
					"Automatic validation require some memory,<br/>" +
					"so turn it off when it's not needed.")
			%>"><input type="checkbox" id="validationActive" value="true"<%=
			validationIsActive ? " checked=\"checked\"" : "" %> style="vertical-align:-2px;" />
			<%= isSwe ? "Validera automatiskt" : "Validate automatically" %></label>
		<button id="validateBtn" class="imcmsFormBtnSmall imcmsFormBtnMedium toolTip iconValidate_pending" style="width:110px; margin-left:10px;"
			      title="<%= StringEscapeUtils.escapeHtml(isSwe ?
					"Validera texten och visa resultat av<br/>" +
					"W3C-valideringen i ett popupfönster.<br/>" +
					"Genom resultatet kan du se vilken rad<br/>" +
					"ev. fel ligger på." :
					
					"Validera the text and show the result of<br/>" +
					"the W3C validation in a popup window.<br/>" +
					"Through the result you may see what row<br/>" +
					"any faults are on, if there are any.") %>"><%= isSwe ? "Validera texten" : "Validate the text" %></button>
		<input tabindex="6" type="button" class="imcmsFormBtnSmall imcmsFormBtnMedium" value="<%=
			isSwe ? "Återställ tidigare versioner" : "Restore earlier versions"
			%>" onclick="openTextRestorer('$language'); return false" /></td>
	</tr>
	</table></td>
<tr>
</vel:velocity>
<tr>
	<td colspan="2" class="imcmsAdmForm">
		<div id="toolBar"></div>
	</td>
</tr><%
	if (width > 0) { %>
<tr>
	<td colspan="2" class="imcmsAdmForm" style="padding: 3px 0 0 5px;">
	<%= isSwe ? "Innehåll - Bredd: " + width + "px." : "Content - Width: " + width + "px." %> &nbsp;
	<a id="resetWidthLink" href="javascript://resetWidth()"><%=
			isSwe ? "Ladda om och visa med normal bredd" : "Reload and show with normal width"
			%> &raquo;</a></td>
</tr><%
	} %>
<tr>
	<td colspan="2" class="imcmsAdmForm">
	<div id="editor"><%
	if (1 == rows) { %>
	<input type="text" name="text" id="text_1row" tabindex="1" value="<%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %>" style="width:100%;" /><%
	} else { %>
	<textarea name="text" tabindex="1" id="text" cols="125" rows="<%= (rows > 1) ? rows : 25 %>" style="overflow: auto; width:<%= width > 0 ? (width + 6) + "px" : "100%" %>;"><%=
	StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %></textarea><%
	} %>
	</div></td>
</tr>
<vel:velocity>
<tr>
	<td><%

	if (showModeText && showModeHtml) { %>
	<label for="format_type_text">
		<input type="radio" name="format_type" id="format_type_text" value="<%= TextDomainObject.TEXT_TYPE_PLAIN %>"<%=
		(TextDomainObject.TEXT_TYPE_PLAIN == textEditPage.getType()) ? " checked=\"checked\"" : "" %><%
		if (showModeEditor) {
			%> onclick="setTextMode()"<%
		} %> /> Text
	</label>
	
	<label for="format_type_html">
		<input type="radio" name="format_type" id="format_type_html" value="<%= TextDomainObject.TEXT_TYPE_HTML %>"<%=
		(TextDomainObject.TEXT_TYPE_HTML==textEditPage.getType()) ? " checked=\"checked\"" : "" %><%
		if (showModeEditor) {
			%> onclick="setHtmlMode()"<%
		} %> /> <%= showModeEditor ? "Editor/" : "" %>HTML
	</label><%
	} else if (showModeText) { %>
	<input type="hidden" name="format_type" id="format_type_text_hidden" value="<%= TextDomainObject.TEXT_TYPE_PLAIN %>" /><%
	} else if (showModeHtml) { %>
	<input type="hidden" name="format_type" id="format_type_html_hidden" value="<%= TextDomainObject.TEXT_TYPE_HTML %>" /><%
	} %></td>
	
	<td align="right">
	<input tabindex="2" type="submit" class="imcmsFormBtn" id="saveCloseBtn" name="ok" value="<fmt:message key="templates/sv/change_text.html/2006" />" />
	<input tabindex="3" type="submit" class="imcmsFormBtn" id="saveBtn" name="save" value="<fmt:message key="templates/sv/change_text.html/save" />" />
	<input tabindex="4" type="submit" class="imcmsFormBtn" id="reloadBtn" value="<fmt:message key="templates/sv/change_text.html/reload" />" />
	<input tabindex="5" type="reset" class="imcmsFormBtn" id="resetFormBtn" value="<fmt:message key="templates/sv/change_text.html/2007" />" />
	<input tabindex="6" type="submit" class="imcmsFormBtn leaveChangeTextBtn" id="backBtn" name="cancel" value="<fmt:message key="global/back" />" /></td>
</tr>
</table>
#gui_bottom()
#gui_outer_end()
</form>
<form action="#dummy"><%
// jQuery's changed check got different content if I only used type='hidden' or display:none
if (1 == rows) { %>
<input type="text" id="savedHtml" style="position:absolute; top:-1000px;" value="<%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %>" /><%
} else { %>
<textarea id="savedHtml" cols="1" rows="<%= (rows > 1) ? rows : 25 %>" style="position:absolute; top:-1000px;"><%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %></textarea><%
} %>
</form>
</vel:velocity>

<style type="text/css">
#validationDiv {<%--
	display: none;
	position: absolute;
	top: 10px;
	right: 10px;
	width: 700px;
	height: 600px;
	overflow: auto;
	background-color: #ffc;
	border: 1px solid #000;--%>
	font-family: Verdana;
}
#validationDiv #validationHeading {
	margin: 0;
	padding: 5px 10px;
	background-color: #0b0;
	color: #fff;
	border: 1px solid #000;
	border-width: 1px 0;
}
#validationDiv h2.error {
	background-color: #f00 !important;
	color: #fff;
}
#validationDiv h2.message {
	background-color: #0b0 !important;
	color: #fff;
}
#validationDiv ol.source li {
	font: 11px 'Courier New', Courier, monospace;
}
#validationDiv ol.source li.heading {
	font-size: 13px;
	font-weight: bold !important;
}
#validationDiv ol.source li.errorLine {
	background-color: #f99;
}
#validationDiv #validationDebug,
#validationDiv LI,
#validationDiv P {
	font-size: 11px;
}
</style>
<div id="disableDiv" style="position:absolute; display:none; top:0; left:0; background-color:#f5f5f7;"></div>

<div id="disableDivInfoValidation" style="<%
	%>position:absolute; display:none; top:0; left:0; width:300px; padding:20px; background-color:#ffd;<%
	%>border: 1px solid #ccc; font-size:13px !important; font-style:italic; color:#000; text-align:center;">
	<img src="<%= cp %>/imcms/images/icons/ajax-loader.gif" alt="" style="vertical-align:middle; margin-right:10px;" /><%
	%><%= isSwe ? "Validerar" : "Validating" %><span id="validationInfoCount"></span>...
	<div style="font-size:12px !important; padding-top:15px;">
		<%= isSwe ? "Denna ruta stängs automatiskt när" : "This box closes automatically when" %><br/>
		<%= isSwe ? "valideringen är genomförd." : "the validation is completed." %>
	</div>
	<div id="disableDivInfoValidationClose"
	     style="position:absolute; top:5px; right:5px; padding: 2px 3px; background-color:#f33; color:#fff; font-weight:bold; font-style:normal; cursor:pointer;">X</div>
</div>

<div id="disableDivInfoSaving" style="<%
	%>position:absolute; display:none; top:0; left:0; width:300px; padding:20px; background-color:#ffd;<%
	%>border: 1px solid #ccc; font-size:13px !important; font-style:italic; color:#000; text-align:center;">
	<img src="<%= cp %>/imcms/images/icons/ajax-loader.gif" alt="" style="vertical-align:middle; margin-right:10px;" /><%
	%><%= isSwe ? "Sparar" : "Saving" %>...
	<div style="font-size:12px !important; padding-top:15px;">
		<%= isSwe ? "Denna ruta stängs automatiskt" : "This box closes automatically" %><br/>
		<%= isSwe ? "när detta är genomfört." : "when this is completed." %>
	</div>
	<div id="disableDivInfoSavingClose"
	     style="position:absolute; top:5px; right:5px; padding: 2px 3px; background-color:#f33; color:#fff; font-weight:bold; font-style:normal; cursor:pointer;">X</div>
</div>

<div style="display:none;">
	<div id="validationDiv" title="<%= isSwe ? "Validering - Resultat" : "Validation - Result" %>">
		<a name="jumpbar"></a>
		<h2 id="validationHeading"></h2>
		<div id="validationResults"></div>
		<div id="validationSource"></div><%
		if (DEBUG_VALIDATION) { %>
		<div id="validationDebug">
			<b>All:</b><br/>
			<textarea id="responseAllTa" rows="30" cols="50" style="width:100%;"></textarea>
		</div><%
		} %>
	</div>
</div>

<%--
/* *******************************************************************************************
 *         Editor initiation                                                                 *
 ******************************************************************************************* */
--%>

<script type="text/javascript">

function setTextMode() {<%
	if (showModeEditor) { %>
	enableFormButtons(jQuery) ;
	try {
		var editor = CKEDITOR.instances['text'] ;
		if (editor) {
			editor.destroy() ;
		}
	} catch (e) {}<%
	} %>
	addEventsToNoEditor(jQuery) ;
}

function setHtmlMode() {<%
	if (showModeEditor) { %>
	enableFormButtons(jQuery) ;
	try {
		var editor = CKEDITOR.instances['text'] ;
		if (undefined == editor && 'true' != getCookie("imcms_hide_editor")) {
			initEditor(jQuery) ;
			addEventsToEditor(jQuery) ;
		}
	} catch (e) {}<%
	} %>
}

function toggleEditorOnOff(on) {
	if (on) {
		setCookie("imcms_hide_editor", "true") ;
		jQuery('#editorOnOffBtn1').hide(0) ;
		jQuery('#editorOnOffBtn0').show(0) ;
	} else {
		setCookie("imcms_hide_editor", "") ;
		jQuery('#editorOnOffBtn1').show(0) ;
		jQuery('#editorOnOffBtn0').hide(0) ;
	}
}

function checkIframeScroll($, theEditor) {<%
	if (width > 0) { %>
	var wantedW = <%= width %> ;
	var editor  = (theEditor || CKEDITOR.instances['text']) ;
	var $iframeBody = $('#editor iframe:first').contents().find('body') ;
	if ($iframeBody && editor) {
		var $editorContainer = $('#cke_text') ;
		var containerW   = $editorContainer.data('orgWidth') || $editorContainer.width() ;
		if (containerW > 0) {
			var iframeBodyW  = $iframeBody.width() ;
			var noScrollDiff = $editorContainer.data('noScrollDiff') || (containerW - wantedW) ;
			if ('' == $editorContainer.data('noScrollDiff')) {
				$editorContainer.data('noScrollDiff', noScrollDiff) ;
			}<%--
			console.log(', iframeBodyW: ' + iframeBodyW + ', wantedW: ' + wantedW + ', (iframeBodyW < wantedW): ' + (iframeBodyW < wantedW) + ', containerW: ' + containerW + ', noScrollDiff: ' + noScrollDiff) ; --%>
			if (iframeBodyW < wantedW) {
				$editorContainer.data('orgWidth', containerW) ;
				$editorContainer.width(wantedW + (wantedW - iframeBodyW) + noScrollDiff) ;<%--
				console.log('checkIframeScroll: (SCROLL) ' + (wantedW + (wantedW - iframeBodyW) + noScrollDiff)) ; --%>
			} else if (iframeBodyW > wantedW) {
				$editorContainer.width(wantedW + noScrollDiff) ;<%--
				console.log('checkIframeScroll: (NO SCROLL) ' + (wantedW + noScrollDiff)) ;
			} else {
				console.log('checkIframeScroll: (NO CHANGE) - (iframeBodyW == wantedW): ' + (iframeBodyW == wantedW)) ; --%>
			}
		}
	}<%
	} %>
}

function setSizeOfEditor($) {
	var newH = $(window).height() - 350 ;
	if (newH < 200) {
		newH = 200 ;
	}
	if ($('#editor iframe:first').length > 0) $('#editor iframe:first').height(newH) ;
	if ($('textarea.cke_source').length > 0) $('textarea.cke_source').height(newH) ;
	if ($('#cke_contents_text').length > 0) $('#cke_contents_text').height(newH) ;
}

var editorHasBeenInitiated = false ;

function initEditor($) {
	if ($('textarea#text').length > 0) {
		startCkEditor($) ;
		if (!editorHasBeenInitiated) {
			$(window).resize(function() {
				setSizeOfEditor($) ;
				checkIframeScroll($) ;
			}) ;
			editorHasBeenInitiated = true ;
		}
	}
}

function startCkEditor($) {
    var customConfig = {
        imcmsMetaId: '<%= textEditPage.getDocumentId() %>'
    };
	initCkEditor($, 'text', '<%= LanguageMapper.convert639_2to639_1(Utility.getLoggedOnUser(request).getLanguageIso639_2()) %>', '<%= (width > 0) ? (width+18) + "" : "" %>', 'imCMS_ALL', customConfig) ;
}

function setTextAreaSize($) {
	if ($('#text').length > 0) {
		var winH = $(window).height() ;
		if (winH > 400) {
			$('#text').height(winH - 270) ;
		}
	}
}

jQuery(document).ready(function($) {<%
	
	if (editorActive && showModeEditor && !editorHidden) { %>
	initEditor($) ;<%
	}
	
	if (0 == rows) { %>
	setTextAreaSize($) ;
	
	$(window).resize(function() {
		setTextAreaSize($) ;
	}) ;<%
	} %>
	
	initSessionChecker($) ;
	
}) ;

<%--
/* *******************************************************************************************
 *         Check Session TimeOut                                                             *
 ******************************************************************************************* */
--%>

var oSessionTimer ;
var sessionTimeOutMs = <%= session.getMaxInactiveInterval() * 1000 %> ;<%-- session.getMaxInactiveInterval() * 1000 --%>
var firstVarningTimeMs = 300000 ;<%-- 5 minutes (300000 ms) --%>
var secondCountTimeS = 60 ;<%-- Count every second for the last minute --%>
var timeLoaded = (new Date()).getTime() ;
var hasQuickRefresh = false ;

function initSessionChecker($) {
	try {
		oSessionTimer = setInterval(function() {
			sessionChecker($) ;
		}, 10000) ;
	} catch(e) {}
}

function sessionChecker($) {
	try {
		var timeNow = (new Date()).getTime() ;<%
		if (DEBUG_SESSION_TIMER) { %>
		var mess = "timeLoaded: " + timeLoaded + ", timeNow: " + timeNow + ", diff: " + (timeNow - timeLoaded) ;
		mess += ", (sessionTimeOutMs - firstVarningTimeMs): " + (sessionTimeOutMs - firstVarningTimeMs) ;<%
		} %>
		if ((timeNow - timeLoaded) > (sessionTimeOutMs - firstVarningTimeMs)) {
			var timeLeft = Math.round(-((timeNow - timeLoaded) - sessionTimeOutMs)/1000) ;<%
			if (DEBUG_SESSION_TIMER) { %>
			mess += " - timeLeft: " + timeLeft ;<%
			} %>
			if (!hasQuickRefresh && timeLeft <= secondCountTimeS + 10) {
				hasQuickRefresh = true ;
				clearInterval(oSessionTimer) ;
				oSessionTimer = setInterval(function() {
					sessionChecker($) ;
				}, 1000) ;
			}
			if (timeLeft >= secondCountTimeS) {
				$('#messageDiv').html('<%= isSwe ?
						"<b>Varning!<b/> Din sessionstid går ut om ' + Math.ceil(timeLeft/60) + ' minuter. Spara dina ändringar!" :
						"<b>Warning!<b/> Your session time will expire in ' + Math.ceil(timeLeft/60) + ' minutes. Please save your changes!" %>') ;
			} else if (timeLeft > 0) {
				$('#messageDiv').html('<%= isSwe ?
						"<b>Varning!<b/> Din sessionstid går ut om ' + timeLeft + ' sekunder. Spara dina ändringar!" :
						"<b>Warning!<b/> Your session time will expire in ' + timeLeft + ' seconds. Please save your changes!" %>') ;
			} else {
				clearInterval(oSessionTimer) ;
				$('#messageDiv').html('<%= isSwe ?
						"<b>Varning!<b/> Din sessionstid har gått ut. Öppna ett annat fönster och logga in - återvänd sedan och spara dina ändringar!" :
						"<b>Warning!<b/> Your session time has expired. Open another window and login - then return to this window and save your changes." %>') ;
			}
			$('#messageDiv').slideDown('slow') ;
		}<%
		if (DEBUG_SESSION_TIMER) { %>
		console.log(mess) ;<%
		} %>
	} catch(e) {}
}

function popWinOpen(winW,winH,sUrl,sName,iResize,iScroll,iStatus) {
	if (screen) {
		var winX, winY ;
		if ((screen.height - winH) < 150) {
			winX = (screen.width - winW) / 2;
			winY = 0;
		} else {
			winX = (screen.width - winW) / 2;
			winY = (screen.height - winH) / 2;
		}
		var popWindow = window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",status=" + iStatus + ",width=" + winW + ",height=" + winH + ",top=" + winY + ",left=" + winX + "");
		if (popWindow) popWindow.focus();
	} else {
		window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",status=" + iStatus + ",width=" + winW + ",height=" + winH);
	}
}

function openTextRestorer(lang) {
	popWinOpen(800,600,"<%= cp %>/imcms/" + lang + "/jsp/text_restorer.jsp?meta_id=<%= textEditPage.getDocumentId() %>&txt=<%= textEditPage.getTextIndex() %>","textRestorerWin",1,1,1) ;
}


/* *******************************************************************************************
 *         Set Cookie                                                                        *
 ******************************************************************************************* */

function setCookie(name, value) {
	var sPath = '/';
	var today = new Date();
	var expire = new Date();
	expire.setTime(today.getTime() + 1000*60*60*24*365); // 365 days
	var sCookieCont = name + "=" + escape(value);
	sCookieCont += (expire == null) ? "" : "; expires=" + expire.toGMTString();
	sCookieCont += "; path=" + sPath;
	document.cookie = sCookieCont;
}

/* *******************************************************************************************
 *         Get Cookie                                                                        *
 ******************************************************************************************* */

function getCookie(Name) {
	var search = Name + "=";
	if (document.cookie.length > 0) {
		var offset = document.cookie.indexOf(search);
		if (offset != -1) {
			offset += search.length;
			end = document.cookie.indexOf(";", offset);
			if (end == -1) {
				end = document.cookie.length;
			}
			return unescape(document.cookie.substring(offset, end));
		}
	}
	return null ;
}
<%--
/* *******************************************************************************************
 *         Validate One Series - (series 0 = All)                                            *
 ******************************************************************************************* */
--%>
var validationIsActive = <%= validationIsActive %> ;
var $dialog = null ;

function getHeightOfDialog($) {
	var winH         = $(window).height() ;
	var dialogMinH   = 400 ;
	var dialogOffset = 100 ;
	var dialogH      = (winH > (dialogMinH + dialogOffset)) ? (winH - dialogOffset) : dialogMinH ;<%
	if (DEBUG_VALIDATION) { %>
	if (console) console.log('winH:' + winH + ', dialogH:' + dialogH + ', (winH > (dialogMinH + dialogOffset)):' + (winH > (dialogMinH + dialogOffset))) ;<%
	} %>
	return dialogH ;
}


var oTimerChangeCheckNoEditor = null ;
var oTimerChangeCheckEditor = null ;

jQuery(document).ready(function($) {
	
	<%-- Validation dialog --%>
	$dialog = $('#validationDiv').dialog({
		width: 770,
		height: getHeightOfDialog($),
		minHeight: 300,
		modal: true,
		autoOpen: false,
		buttons: {
			'<%= isSwe ? "Stäng" : "Close" %>' : function() {
				$(this).dialog("close");
			}
		}
	}) ;
	
	$(window).resize(function() {
		if ($dialog.dialog('isOpen')) {
			var oldOuterH = $dialog.dialog('option', 'height') ;
			var oldInnerH = $('#validationDiv').height() ;
			var deltaH    = (oldOuterH - oldInnerH) ;
			var newOuterH = getHeightOfDialog($) ;
			var newInnerH = (newOuterH - deltaH) ;
			$('#validationDiv').height(newInnerH) ;
			$dialog.dialog('option', 'height', newOuterH) ;<%
			if (DEBUG_VALIDATION) { %>
			if (console) console.log('oldOuterH:' + oldOuterH + ', oldInnerH:' + oldInnerH + ', deltaH:' + deltaH + ', newOuterH:' + newOuterH + ', newInnerH:' + newInnerH) ;<%
			} %>
		} else {
			$dialog.dialog('option', 'height', getHeightOfDialog($)) ;
		}
	}) ;
	
	checkValidationActive($, false) ;
	
	$('#validationActive').click(function() {
		checkValidationActive($, true) ;
	}) ;
	
	<%-- Validation initiation --%>
	validateText($, false) ;
	
	$('#validateBtn').click(function(event) {
		event.preventDefault() ;
		validateText($, true) ;
	}) ;
	
	<%-- Editor Start Events - Reset/Changed/Auto Validation --%>
	var editor = CKEDITOR.instances['text'] ;
	if (editor) {
		addEventsToEditor($) ;
	} else if (<%= !editorActive %>) {
		addEventsToNoEditor($) ;
	}
	
}) ;

function checkValidationActive($, doSetCookie) {
	validationIsActive = $('#validationActive').is(':checked') ;<%
	if (DEBUG_VALIDATION) { %>
	if (console) console.log('checkValidationActive: ' + validationIsActive) ;<%
	} %>
	if (doSetCookie) {
		setCookie('validationActive', validationIsActive + '') ;
	}
}

var autoValidationTimer = null ;

function autoValidation($) {
	if (autoValidationTimer) {
		window.clearTimeout(autoValidationTimer) ;<%
		if (DEBUG_VALIDATION) { %>
		if (console) console.log('window.clearTimeout(autoValidationTimer) ;') ;<%
		} %>
	}<%
	if (DEBUG_VALIDATION) { %>
	if (console) console.log('autoValidationTimer = window.setTimeout(validateText($, false), 5000) ;') ;<%
	} %>
	if (validationIsActive) {
		autoValidationTimer = window.setTimeout(function() {<%
			if (DEBUG_VALIDATION) { %>
			if (console) console.log('TIMER: validateText($, false) ;') ;<%
			} %>
			validateText($, false) ;
		}, 5000) ;<%
	if (DEBUG_VALIDATION) { %>
	} else {
		if (console) console.log('autoValidation($) DISABLED - validationIsActive: ' + validationIsActive) ;<%
	} %>
	}
}

<%--
/* *******************************************************************************************
 *         Check for changes                                                                 *
 ******************************************************************************************* */
--%>
var isSaving  = false ;
var isChanged = false ;<%
if (DEBUG_CHANGED_CONT) { %>
var hasCheckedCompare = false ;<%
} %>
var isCheckingEditorChanged = false ;

function checkEditorChanged($, editor) {
	isCheckingEditorChanged = true ;<%
	if (DEBUG_CHANGED) { %>
	if (console) console.log('function checkEditorChanged($, ' + (null != editor ? 'editor' : 'null') + ')') ;<%
	} %>
	try {
		if (null == editor && $('#text,#text_1row').length > 0) {
			var savedHtml  = $('#savedHtml').val() ;
			var editedHtml = $('#text,#text_1row').first().val() ;
			isChanged = (editedHtml != savedHtml) ;<%
			if (DEBUG_CHANGED_CONT) { %>
			if (console && !hasCheckedCompare) {
				console.log('isChanged (NO EDITOR): ' + isChanged + ', savedHtml: ' + savedHtml.length + ', editedHtml: ' + editedHtml.length) ;
				console.log('savedHtml: ' + savedHtml) ;
				console.log('editedHtml: ' + editedHtml) ;
				hasCheckedCompare = true ;
			}<%
			} %>
		} else if (editor) {
			isChanged = editor.checkDirty() ;<%
			if (DEBUG_CHANGED) { %>
			if (console) console.log('isChanged (EDITOR): ' + isChanged) ;<%
			} %>
		}
	} catch (e) {<%
	if (DEBUG_CHANGED) { %>
	if (console) console.log('isChanged ERROR: ' + e) ;<%
	} %>}<%
	if (DEBUG_CHANGED) { %>
	if (console) console.log('isChanged: ' + isChanged) ;<%
	} %>
	$(':submit.leaveChangeTextBtn').unbind('click') ;
	$('#reloadBtn').unbind('click') ;
	$('#resetWidthLink').unbind('click') ;
	if (isChanged) {
		var changedMessage = '<%= isSwe ? "Innehållet har ändrats!\\nVill du ladda om sidan utan att spara?" : "The content is changed!\\nDo you want to reload the page without saving?" %>' ;
		$(':submit.leaveChangeTextBtn').bind('click', function() {
			return confirm('<%= isSwe ? "Innehållet har ändrats!\\nVill du lämna utan att spara?" : "The content is changed!\\nDo you want to leave without saving?" %>') ;
		}) ;
		$('#reloadBtn').bind('click', function(event) {
			event.preventDefault() ;
			if (confirm(changedMessage)) {
				location.replace(location.href) ;			
			}
		}) ;
		$('#resetWidthLink').bind('click', function(event) {
			event.preventDefault() ;
			if (confirm(changedMessage)) {
				location.replace(removeURLParam(location.href, 'width')) ;				
			}
		}) ;
	} else {
		$('#reloadBtn').bind('click', function(event) {
			event.preventDefault() ;
			location.replace(location.href) ;
		}) ;
		$('#resetWidthLink').bind('click', function(event) {
			event.preventDefault() ;
			location.replace(removeURLParam(location.href, 'width')) ;
		}) ;
	}
	if (!isSaving) {
		if (isChanged) {
			$('#reloadBtn').disableImcmsBtn() ;
		} else {
			$('#reloadBtn').enableImcmsBtn() ;
		}
		$('#contentChangedDiv_' + !isChanged).fadeOut('fast', function() {
			$('#contentChangedDiv_' + isChanged).fadeIn('fast') ;
		}) ;
	}
	isCheckingEditorChanged = false ;
}

function resetChangedContent($) {
	var editor  = CKEDITOR.instances['text'] ;
	if (editor) {
		editor.resetDirty() ;
		editor.updateElement() ;
	}
	var editedHtml = $('#text,#text_1row').first().val() ;
	$('#savedHtml').val(editedHtml) ;
	isChanged = false ;
	isSaving  = false ;
	checkEditorChanged($, editor) ;
}

function disableFormButtons($) {
	$('#resetFormBtn').disableImcmsBtn() ;
}
function enableFormButtons($) {
	$('#resetFormBtn').enableImcmsBtn() ;
}

function getIFrameDocument(iframe) {
	if (iframe.contentDocument) {
		return iframe.contentDocument; 
	} else if (iframe.contentWindow) {
		return iframe.contentWindow.document;
	} else if (iframe.document) {
		return iframe.document;
	} else {
		return null;
	}
}

function addEventsToNoEditor($) {<%
	if (DEBUG_EDITOR) { %>
	if (console) console.log('addEventsToNoEditor()!') ;<%
	} %>
	if (oTimerChangeCheckEditor) {
		window.clearInterval(oTimerChangeCheckEditor) ;<%
		if (DEBUG_CHANGED) { %>
		if (console) console.log('EXEC: clearInterval(checkEditorChanged($, editor))') ;<%
		} %>
	}<%
	if (DEBUG_CHANGED) { %>
	if (console) console.log('EXEC: setInterval(checkEditorChanged($, null))') ;<%
	} %>
	oTimerChangeCheckNoEditor = window.setInterval(function() {
		if (!isCheckingEditorChanged) checkEditorChanged($, null) ;
	}, 2000) ;
	$('#text,#text_1row').live('keyup mouseup blur paste', function() {
		autoValidation($) ;
	}) ;
}

function addEventsToEditor($) {
	var editor = CKEDITOR.instances['text'] ;
	if (editor) {<%
		if (DEBUG_EDITOR) { %>
		if (console) console.log('addEventsToEditor() : editor active!') ;<%
		} %>
		
		if (!editor.document) {
			window.setTimeout(function() {<%
				if (DEBUG_EDITOR) { %>
				if (console) console.log('NO editor.document - Retry...') ;<%
				} %>
				addEventsToEditor($) ;
			}, 500) ;
			return ;
		}
		try {
			editor.document.on("keyup", function() {
				autoValidation($) ;
			}) ;
			editor.document.on("mouseup", function() {
				autoValidation($) ;
			}) ;
			editor.document.on("blur", function() {
				autoValidation($) ;
			}) ;
			editor.document.on("paste", function() {
				autoValidation($) ;
			}) ;
		} catch (e) {<%
		if (DEBUG_EDITOR) { %>
			if (console) console.log('editor.document:' + editor.document + ' - editor.document.on ERROR: ' + e) ;<%
		} %>}

		$('#resetFormBtn').live('click', function() {<%
			if (DEBUG_EDITOR) { %>
			if (console) console.log('resetFormBtn click()') ;<%
			} %>
			editor.setData($('#savedHtml').val()) ;<%-- The textarea is resetted but not the editor --%>
		}) ;
		
		if (oTimerChangeCheckNoEditor) {
			window.clearInterval(oTimerChangeCheckNoEditor) ;<%
			if (DEBUG_CHANGED) { %>
			if (console) console.log('EXEC: clearInterval(checkEditorChanged($, null))') ;<%
			} %>
		}<%
		if (DEBUG_CHANGED) { %>
		if (console) console.log('EXEC: setInterval(checkEditorChanged($, editor))') ;<%
		} %>
		oTimerChangeCheckEditor = window.setInterval(function() {
			if (!isCheckingEditorChanged) checkEditorChanged($, editor) ;
			checkIframeScroll($, editor) ;
		}, 2000) ;
		
		editor.on('afterCommandExec', function(e) {<%--
			CKEDITOR.TRISTATE_DISABLED = 0
			CKEDITOR.TRISTATE_ON       = 1
			CKEDITOR.TRISTATE_OFF      = 2
			--%>
			if (CKEDITOR.TRISTATE_OFF == e.data.command.previousState && CKEDITOR.TRISTATE_DISABLED == e.data.command.state) {<%
				
				if (DEBUG_EDITOR) { %>
				if (console) console.log('source - state: ' + e.data.command.state + ' / previousState: ' + e.data.command.previousState) ;<%
				} %>
				disableFormButtons($) ;
				$(window).resize() ;<%-- Trigger it to get the right size. --%>
				
			} else if (CKEDITOR.TRISTATE_ON == e.data.command.previousState) {<%
				
				if (DEBUG_EDITOR) { %>
				if (console) console.log('wysiwyg - addEventsToEditor($) - state: ' + e.data.command.state + ' / previousState: ' + e.data.command.previousState) ;<%
				} %>
				enableFormButtons($) ;
				addEventsToEditor($) ;
				$(window).resize() ;<%-- Trigger it to get the right size - WebKit/Crome. --%>
				
			} else if (CKEDITOR.TRISTATE_OFF == e.data.command.state) {<%
				
				if (DEBUG_EDITOR) { %>
				if (console) console.log('off - state: ' + e.data.command.state + ' / previousState: ' + e.data.command.previousState) ;<%
				} %>
				disableFormButtons($) ;
				
			} else {<%
				
				if (DEBUG_EDITOR) { %>
				if (console) console.log('ELSE - e.data.command.state: ' + e.data.command.state + ' / previousState: ' + e.data.command.previousState) ;<%
				} %>
				enableFormButtons($) ;
				
			}
		}) ;
		
		editor.on('focus', function(e) {<%
			if (DEBUG_EDITOR) { %>
			if (console) console.log('focus') ;<%
			} %>
			enableFormButtons($) ;
		}) ;
		
	} else if (<%= (showModeEditor && !editorHidden) %>) {<%
		if (DEBUG_EDITOR) { %>
		if (console) console.log('addEventsToEditor() : editor NOT active - RETRY!') ;<%
		} %>
		window.setTimeout(function() {
			addEventsToEditor($) ;
		}, 500) ;
	}
}
	
<%-- Button extensions --%>
jQuery.fn.extend({
	disableBtn: function(btnText){
		$(this).html(btnText).fadeTo(100, 0.2).attr('disabled', 'disabled') ;<%
		if (DEBUG_VALIDATION) { %>
		if (console) console.log('disableBtn') ;<%
		} %>
	},
	enableBtn: function(btnText){
		$(this).html(btnText).fadeTo(100, 1).removeAttr('disabled') ;<%
		if (DEBUG_VALIDATION) { %>
		if (console) console.log('enableBtn') ;<%
		} %>
	},
	enableBtnAndHide: function(btnText, speed){
		$(this).html(btnText).fadeTo(100, 1).removeAttr('disabled').hide(speed) ;
	},
	enableImcmsBtn: function(){
		$(this).removeAttr('disabled').addClass('imcmsFormBtn').removeClass('imcmsFormBtnDisabled') ;<%
		if (DEBUG_VALIDATION) { %>
		if (console) console.log('enableImcmsBtn') ;<%
		} %>
	},
	disableImcmsBtn: function(){
		$(this).addClass('imcmsFormBtnDisabled').removeClass('imcmsFormBtn').attr('disabled','disabled') ;<%
		if (DEBUG_VALIDATION) { %>
		if (console) console.log('disableImcmsBtn') ;<%
		} %>
	},
	overBtn: function(){
		//$(this).fadeTo(10, 1) ;
	},
	outBtn: function(){
		//$(this).fadeTo(10, 0.8) ;
	}
}) ;


function showDisableDiv($, isValidation, completeFn) {
	var oMain    = $('#mainTable') ;
	var oOverlay = $('#disableDiv') ;
	var oInfo    = (isValidation) ? $('#disableDivInfoValidation') : $('#disableDivInfoSaving') ;
	var oClose   = (isValidation) ? $('#disableDivInfoValidationClose') : $('#disableDivInfoSavingClose') ;
	var oPos     = $(oMain).position() ;
	if ($(oOverlay).is(':hidden')) {
		$('#hoverTitleHeadDiv,#hoverTitleBody').hide(0) ;
		$(oOverlay).css({
			top: oPos.top + 'px',
			left: oPos.left + 'px',
			width: $(oMain).width() + 'px',
			height: $(oMain).height() + 'px'
		}).fadeTo(0, 0.8) ;
		$(oInfo).css({
			top: (oPos.top + 100) + 'px',
			left: (oPos.left + ($(oMain).width()/2 - 150)) + 'px'
		}).fadeIn(0) ;<%
		if (DEBUG_VALIDATION) { %>
		if (console) console.log('showDisableDiv()') ;<%
		} %>
		$(oClose).click(function() {
			hideDisableDiv($, isValidation) ;
		}) ;
	}
	if ($.isFunction(completeFn)) {
		completeFn() ;
	}
}
function hideDisableDiv($, isValidation, completeFn) {
	var oOverlay = $('#disableDiv') ;
	var oInfo    = (isValidation) ? $('#disableDivInfoValidation') : $('#disableDivInfoSaving') ;
	if ($(oOverlay).is(':visible')) {
		$(oInfo).fadeOut(0, function() {
			$(oOverlay).fadeOut(0) ;<%
			if (DEBUG_VALIDATION) { %>
			if (console) console.log('hideDisableDiv()') ;<%
			} %>
		}) ;
	}
	if ($.isFunction(completeFn)) {
		completeFn() ;
	}
}

function validateText($, showResults) {
	var oBtn = $('#validateBtn') ;<%
	if (DEBUG_VALIDATION) { %>
	if (console) console.log('validateText($, ' + showResults + ')') ;<%
	} %>
	$(oBtn).disableBtn('<%= isSwe ? "Validerar..." : "Validates..." %>') ;
	$('#validationErrorDiv').ajaxError(function(e, xhr, settings, exception){
		hideDisableDiv($, true) ;
		if (0 == $(this).find('#validationErrorDivStatus').length) {
			$(this).append('<div id="validationErrorDivStatus" style="color:red;">Error i:<br/>' + getDataUrl + '<br/>' + settings.url + '<br/><i>' + xhr.statusText + '</i></div>').slideDown('slow') ;
		}
	}) ;
	if (showResults) {
		showDisableDiv($, true) ;
	}
	var textContent = '' ;
	var editor = CKEDITOR.instances['text'] ;
	if (editor) {
		textContent = editor.getData() ;<%
		if (DEBUG_VALIDATION) { %>
		console.log('Validate editor!') ;<%
		} %>
	} else if ($('#text_1row').length > 0) {
		textContent = $('#text_1row').val() ;<%
		if (DEBUG_VALIDATION) { %>
		console.log('Validate 1 row!') ;<%
		} %>
	} else if ($('#text').length > 0) {
		var isTextMode = (<%= (showModeText && !showModeHtml) + " || " %>$('#format_type_text').is(':checked')) ;
		textContent = (isTextMode) ? $('#text').html() : $('#text').val() ;<%
		if (DEBUG_VALIDATION) { %>
		console.log('Validate textarea! - isTextMode: ' + isTextMode) ;<%
		} %>
	}
	textContent = textContent.replace(/<\?[^\?]+?\?>/g, '') ;
	$.ajax({
		url     : '<%= AjaxServlet.getPath(cp) %>',
		type    : 'POST',		
		data    : {
			action : 'getCompleteHtmlForW3cValidation',
			value  : textContent
		},
		cache   : false,
		success : function(data) {
			$.ajax({
				url      : '<%= AjaxServlet.getPath(cp) %>',
				type     : 'POST',
				dataType : 'json',
				data     : {
					action         : 'sendValidationToW3cAndReturnJson',
					showResults    : showResults,
					htmlToValidate : data
				},
				success : function(response) {
					var theClassName ;
					var isOk          = (null != response && response.isOk) ;
					var isValid       = (null != response && response.isValid) ;
					var responseHtml  = (null == response || !showResults) ? '' : response.getHtml ;
					var responseError = (null == response) ? '' : response.error ;
					if (!isOk) {
						hideDisableDiv($, true, function() {
							if ('' != responseError && 0 == $(this).find('#validationErrorDivStatus').length) {
								$('#validationErrorDiv').append('<div id="validationErrorDivStatus" style="color:red;">Error: <i>' + responseError + '</i></div>')
							}
							$('#validationErrorDiv').slideDown('slow') ;
						}) ;
					} else {
						if (!showResults) {
							$(oBtn)
									.removeClass('iconValidate_pending')
									.removeClass('iconValidate_' + !isValid)
									.addClass('iconValidate_' + isValid)
									.enableBtn('<%= isSwe ? "Validera texten" : "Validate the text" %>') ;
							hideDisableDiv($, true) ;
						} else {
							$(oBtn)
									.removeClass('iconValidate_pending')
									.removeClass('iconValidate_' + !isValid)
									.addClass('iconValidate_' + isValid) ;
							if (isValid) {
								heading      = "<%= isSwe ? "Godkänd XHTML!" : "Approved XHTML!" %>" ;
								theClassName = "message" ;
							} else {
								heading      = "<%= isSwe ? "Ej godkänd XHTML!" : "NOT approved XHTML!" %>" ;
								theClassName = "error" ;
							}
							$('#validationHeading').attr('class', theClassName).html('').html(heading) ;
							if (!isValid) {
								$('#validationResults').html('').html($(responseHtml).find('#result').html()) ;
							} else {
								$('#validationResults').html('') ;
							}
							$('#validationSource').html('').html($(responseHtml).find('#source').html()) ;
							$('#responseAllTa').val(responseHtml) ;<%----%>
							for (var i = 1; i <= 8; i++) {
								$('#line-' + i).remove() ;<%--.css('color', '#999') ;--%>
							}
							for (i = 1; i <= 2; i++) {
								$('li[id^=line-]:last').remove() ;
							}
							$('li[id^=line-]').each(function() {
								if (/^\s*&lt;(h2|h3|h4)/gi.test($(this).html())) {
									$(this).addClass('heading') ;
								}
							}) ;
							$('li[id^=line-]:first').parent()<%-- It didn't find a direct selector to '#sourse h3'!!! --%>
											.prev('p').html('<%= isSwe ?
											"Nedan listas koden som validerades.<br/>Vid fel kan du lätt kolla vilken rad felet ligger på och hitta rätt stycke för felet." :
											"The code that was validated is listed below.<br/>If there\\'s any errors you can easily see what row they\\'re on and find the right paragraph for the errors." %>')
											.prev('h3').html('XHTML <%= isSwe ? "kodlistning" : "source code" %>') ;
							if ($('a[href^=#line-]').length > 0) {
								$('a[href^=#line-]').each(function() {
									var errLine = parseInt($(this).attr('href').replace(/[^\d]/g, '')) ;
									$('#line-' + errLine).addClass('errorLine') ;
									$(this).html((errLine - 8) + '') ;
								}) ;
							}
							$('#validationDiv').scrollTop(0) ;
							$(oBtn).enableBtn('<%= isSwe ? "Validera texten" : "Validate the text" %>') ;
							hideDisableDiv($, true) ;
							$dialog.dialog('open') ;
						}
					}
				}
			}) ;
		},
		error: function() {
			$('#validationErrorDiv').slideDown('slow') ;
		}
	}) ;
}


<%--
/* *******************************************************************************************
 *         Save changes through Ajax                                                         *
 ******************************************************************************************* */
--%>

jQuery(document).ready(function($) {
	$('#saveCloseBtn').live('click', function(event) {
		event.preventDefault() ;
		saveText($, true) ;
	}) ;
	$('#saveBtn').live('click', function(event) {
		event.preventDefault() ;
		saveText($, false) ;
	}) ;
}) ;

function saveText($, closeAfter) {<%
	if (DEBUG_SAVE) { %>
	console.log('closeAfter: ' + closeAfter) ;<%
	} %>
	isSaving = true ;
	var isSavedSuccess = false ;
	var $btnSaveClose = $('#saveCloseBtn') ;
	var $btnSave      = $('#saveBtn') ;
	var $btnReset     = $('#resetBtn') ;
	var $btnBack      = $('#backBtn') ;
	$btnSaveClose.disableImcmsBtn() ;
	$btnSave.disableImcmsBtn() ;
	$btnReset.disableImcmsBtn() ;
	$btnBack.disableImcmsBtn() ;
	$('#validationErrorDiv').ajaxError(function(e, xhr, settings, exception){
		hideDisableDiv($, false) ;
		if (0 == $(this).find('#validationErrorDivStatus').length) {
			$(this).append('<div id="validationErrorDivStatus" style="color:red;">Error i:<br/>' + getDataUrl + '<br/>' + settings.url + '<br/><i>' + xhr.statusText + '</i></div>').slideDown('slow') ;
		}
	}) ;
	showDisableDiv($, false) ;
	var textContent = '' ;
	var editor = CKEDITOR.instances['text'] ;
	if (editor) {
		textContent = editor.getData() ;<%
		if (DEBUG_SAVE) { %>
		console.log('Save editor!') ;<%
		} %>
	} else if ($('#text_1row').length > 0) {
		textContent = $('#text_1row').val() ;<%
		if (DEBUG_SAVE) { %>
		console.log('Save 1 row!') ;<%
		} %>
	} else if ($('#text').length > 0) {
		textContent = $('#text').val() ;<%
		if (DEBUG_SAVE) { %>
		console.log('Save textarea!') ;<%
		} %>
	}
	$.ajax({
		url     : '<%= AjaxServlet.getPath(cp) %>',
		type    : 'POST',
		dataType : 'json',
		data    : {
			action  : 'saveText',
			meta_id : <%= textEditPage.getDocumentId() %>,
			txt_no  : <%= textEditPage.getTextIndex() %>,
			do_log  : isChanged,
			format  : $(':radio[name=format_type]:checked').val() || $('input[name=format_type]').val(),
			text    : textContent
		},
		cache   : false,
		success : function(response) {
			var isSaved       = (null != response && response.isSaved) ;
			var responseError = (null == response) ? '' : response.error ;
			if (isSaved) {<%
				if (DEBUG_SAVE) { %>
				console.log('saved!') ;<%
				} %>
				$('#contentSavedDiv_true')
					.fadeIn('fast')<%
					for (int i = 0; i < 2; i++) { %>
					.animate({color:'#20568d'}, 200)
					.animate({color:'#afa'}, 200)<%
					} %>
					.delay(2000)
					.slideUp('fast') ;
				resetChangedContent($) ;
				isSavedSuccess = true ;
				isSaving = false ;<%
				if (DEBUG_SAVE) { %>
				console.log('closeAfter:' + closeAfter + ', isSavedSuccess:' + isSavedSuccess) ;<%
				} %>
				if (closeAfter && isSavedSuccess) {<%
					if (DEBUG_SAVE) { %>
					console.log('back') ;<%
					} %>
					$('#backBtnTop').trigger('click') ;
				}
			} else {<%
				if (DEBUG_SAVE) { %>
				console.log('NOT saved!') ;<%
				} %>
				hideDisableDiv($, false, function() {
					if ('' != responseError && 0 == $(this).find('#validationErrorDivStatus').length) {
						$('#validationErrorDiv').append('<div id="validationErrorDivStatus" style="color:red;">Error: <i>' + responseError + '</i></div>')
					}
					$('#validationErrorDiv').slideDown('slow') ;
				}) ;
				isSaving = false ;
			}
		},
		error: function() {
			$('#validationErrorDiv').slideDown('slow') ;
			isSaving = false ;
		}
	}) ;
	hideDisableDiv($, false, function() {
		$btnSaveClose.enableImcmsBtn() ;
		$btnSave.enableImcmsBtn() ;
		$btnReset.enableImcmsBtn() ;
		$btnBack.enableImcmsBtn() ;
	}) ;
}

function removeURLParam(url, param) {
 var urlparts= url.split('?');
 if (urlparts.length>=2) {
  var prefix= encodeURIComponent(param)+'=';
  var pars= urlparts[1].split(/[&;]/g);
  for (var i=pars.length; i-- > 0;)
   if (pars[i].indexOf(prefix, 0)==0)
    pars.splice(i, 1);
  if (pars.length > 0)
   return urlparts[0]+'?'+pars.join('&');
  else
   return urlparts[0];
 } else
  return url;
}

</script>


</body>
</html>
