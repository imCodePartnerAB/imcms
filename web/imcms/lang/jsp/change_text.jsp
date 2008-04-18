<%@ page

	import="com.imcode.imcms.servlet.admin.ChangeText,
	        imcode.server.Imcms,
	        imcode.server.LanguageMapper,
	        imcode.server.document.textdocument.TextDomainObject,
	        imcode.util.Utility,
	        org.apache.commons.lang.StringEscapeUtils,
	        java.util.ArrayList,
	        java.util.Arrays,
	        java.util.List,
	        com.imcode.imcms.api.ContentManagementSystem"

    contentType="text/html; charset=UTF-8"

%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@taglib prefix="vel" uri="imcmsvelocity"
%><%!

private String getCookie( String name, HttpServletRequest request ) {
	String retVal = "" ;
	Cookie[] cookies = request.getCookies() ;
	if (cookies != null) {
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(name)) {
				retVal = cookies[i].getValue() ;
				break ;
			}
		}
	}
	return retVal ;
}

%><%

response.setContentType( "text/html; charset=" + Imcms.DEFAULT_ENCODING );
ChangeText.TextEditPage textEditPage = (ChangeText.TextEditPage) request.getAttribute(ChangeText.TextEditPage.REQUEST_ATTRIBUTE__PAGE);

pageContext.setAttribute("textEditPage", textEditPage);

List<String> formats = new ArrayList<String>();
String[] formatParameterValues = request.getParameterValues("format");
if (null != formatParameterValues) {
    formats.addAll(Arrays.asList(formatParameterValues));
    formats.remove("");
}

boolean showModeEditor = formats.isEmpty();
boolean showModeText   = formats.contains("text") || showModeEditor;
boolean showModeHtml   = formats.contains("html") || formats.contains("none") || showModeEditor ;
boolean editorHidden   = getCookie("imcms_hide_editor", request).equals("true") ;
int rows = (request.getParameter("rows") != null && request.getParameter("rows").matches("^\\d+$")) ? Integer.parseInt(request.getParameter("rows")) : 0 ;

if (rows > 0) {
	showModeEditor = false;
}

boolean isSwe = false ;
try {
	isSwe =	ContentManagementSystem.fromRequest(request).getCurrentUser().getLanguage().getIsoCode639_2().equals("swe");
} catch (Exception e) {}

/* *******************************************************************************************
 *         Get languages                                                                     *
 ******************************************************************************************* */

LanguageDao languageDao = (LanguageDao) Imcms.getServices().getSpringBean("languageDao") ;
List<I18nLanguage> languages = languageDao.getAllLanguages() ;
I18nLanguage defaultLanguage = languageDao.getDefaultLanguage() ;
I18nLanguage currentLanguage = (null != session.getAttribute("lang")) ? (I18nLanguage)session.getAttribute("lang") : defaultLanguage ;

DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper() ;
DocumentDomainObject document = documentMapper.getDocument(textEditPage.getDocumentId()) ;
Meta meta = document.getMeta() ;
List<I18nMeta> i18nMetas = meta.getI18nMetas() ;

%>
<%@page import="com.imcode.imcms.api.I18nMeta, com.imcode.imcms.dao.LanguageDao, com.imcode.imcms.api.I18nLanguage, com.imcode.imcms.api.Meta, imcode.server.document.DocumentDomainObject, com.imcode.imcms.mapping.DocumentMapper"%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/change_text.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
<script src="<%= request.getContextPath() %>/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
</head>
<body bgcolor="#FFFFFF" style="margin-bottom:0px;">
<% if (showModeEditor && !editorHidden) { %>
<script type="text/javascript">
    _editor_url  = "<%=request.getContextPath()%>/imcms/xinha/"  // (preferably absolute) URL (including trailing slash) where Xinha is installed
    _editor_lang = "<%= LanguageMapper.convert639_2to639_1(Utility.getLoggedOnUser(request).getLanguageIso639_2()) %>";      // And the language we need to use in the editor.
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/imcms/xinha/XinhaCore.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/imcms/xinha/plugins/ImcmsIntegration/init.js.jsp<%
if (TextDomainObject.TEXT_TYPE_HTML==textEditPage.getType() && !editorHidden) { %>?html=true<% } %>"></script>
<% } %>
<form method="POST" action="<%= request.getContextPath() %>/servlet/SaveText">
<input type="hidden" name="meta_id"  value="<%= textEditPage.getDocumentId() %>">
<input type="hidden" name="txt_no"   value="<%= textEditPage.getTextIndex() %>"><%
if (null != formatParameterValues) {
	for ( String formatParameter : formatParameterValues ) { %>
<input type="hidden" name="format" value="<%= formatParameter %>"><%
	}
	if (rows > 0) { %>
<input type="hidden" name="rows" value="<%= rows %>"><%
	}
} %>
<input type="hidden" name="label" value="<%= StringEscapeUtils.escapeHtml( textEditPage.getLabel() ) %>">
#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
		<input type="submit" value="<? global/back ?>" name="cancel" class="imcmsFormBtn"></td><%
		if (showModeEditor) { %>
		<td style="color:#ffffff;" nowrap>&nbsp; &nbsp; <? install/htdocs/sv/htmleditor/editor/editor.jsp/3000 ?> &nbsp;</td>
		<td><%
			if (editorHidden) { %>
		<button id="editorOnOffBtn0" onClick="toggleEditorOnOff(0); return false"
			class="imcmsFormBtn" style="width:40px;"><? global/off ?></button>
		<button id="editorOnOffBtn1" onClick="toggleEditorOnOff(1); return false"
			class="imcmsFormBtnActive" style="width:40px; display:none"><? global/on ?></button><%
			} else { %>
		<button id="editorOnOffBtn0" onClick="toggleEditorOnOff(0); return false"
			class="imcmsFormBtn" style="width:40px; display:none"><? global/off ?></button>
		<button id="editorOnOffBtn1" onClick="toggleEditorOnOff(1); return false"
			class="imcmsFormBtnActive" style="width:40px;"><? global/on ?></button><%
			} %></td><%
		} %>
	</tr>
	</table></td>

	<td align="right">
	<input type="button" tabindex="12" value="<? templates/sv/change_text.html/2004 ?>" title="<? templates/sv/change_text.html/2005 ?>" class="imcmsFormBtn" onClick="openHelpW('EditText')"></td>

</tr>
</table>

#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="720" align="center">
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="2" width="100%;">
	<tr valign="top">
		<td width="80%">
		<div id="theLabel"><b class="imcmsAdmHeadingSmall">Label:</b> <%= StringEscapeUtils.escapeHtml( textEditPage.getLabel() ) %></div></td>
		
		<td width="20%" align="right" style="padding-top:3px; padding-left:15px;">
		<input tabindex="6" type="button" class="imcmsFormBtnSmall" value="<%= isSwe ? "Återställ tidigare versioner" : "Restore earlier versions" %>"
		       onclick="openTextRestorer(); return false"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="2">#gui_hr( "blue" )<div id="messageDiv" style="display:none; color:#cc0000; padding:10px 0;"></div></td>
</tr><%
if (null != languages) { %>
<tr>
	<td colspan="2" style="padding: 3px 5px;">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr><%
	int iCount = 0 ;
	int languagesPerRow = 7 ;
	for ( I18nMeta i18nMeta : i18nMetas ) {
		I18nLanguage lang     = i18nMeta.getLanguage() ;
		String langCode       = lang.getCode() ;
		String langName       = lang.getName() ;
		String langNameNative = lang.getNativeName() ;
		boolean isEnabled  = (i18nMeta.getEnabled()) ;
		boolean isDefault  = (null != defaultLanguage && defaultLanguage.equals(lang)) ;
		boolean isCurrent  = (null != currentLanguage && currentLanguage.equals(lang)) ;
		String queryString = request.getQueryString().replaceAll("lang=[a-z]{2}&?", "") ;
		String href_0      = "<a href=\"ChangeText?lang=" + langCode + "&amp;" + queryString + "\" title=\"" + langName + "/" + langNameNative + "#DATA#\" style=\"#STYLE#\">" ;
		String href_1      = "</a>" ;
		String sData = "" ;
		if (isDefault)  sData += "default " ;
		if (isCurrent)  sData += "current " ;
		if (!isEnabled) sData += "disabled " ;
		if (!"".equals(sData)) {
			sData = " (" + sData.trim() + ")" ;
		}
		href_0 = href_0.replace("#DATA#", sData) ;
		String sStyle = "text-decoration:none; " ;
		if (isEnabled) {
			sStyle += "color:#000; " ;
		} else {
			sStyle += "color:#999; " ;
		}
		if (isCurrent)  sStyle += "font-weight:bold; " ;
		href_0 = href_0.replace("#STYLE#", sStyle.trim()) ;
		if (iCount > 0 && iCount % languagesPerRow == 0) { %>
	</tr>
	<tr><%
		} %>
		<td><%= href_0 %><img src="$contextPath/imcms/$language/images/admin/flags_iso_639_1/<%= langCode %>.gif" alt="" style="border:0;" /><%= href_1 %></td>
		<td style="width:<%= isDefault ? 4.6 : 2.2 %>em; padding-left:5px; font: 13px Verdana, Arial, sans-serif;"><%
		%><%= href_0 %><%= langCode %><%= isDefault ? "&nbsp;(d)" : "" %><%= href_1 %></td><%
		iCount++ ;
	}
	while(iCount % languagesPerRow != 0) { %>
		<td colspan="2">&nbsp;</td><%
		iCount++ ;
	} %>
	</tr>
	</table></td>
</tr><%
} %>
<tr>
	<td colspan="2" style="padding-top:5px; padding-bottom:15px;">
	<%= isSwe ? "Valt språk aktiverat" : "Current language active" %>:&nbsp;
	<c:choose>
		<c:when test="${textEditPage.enabled}"><b><%= isSwe ? "Ja" : "Yes" %></b></c:when>
		<c:otherwise><b><%= isSwe ? "Nej" : "No" %></b></c:otherwise>
	</c:choose>
	<c:if test="${textEditPage.substitutedWithDefault}">
	<div style="padding-top:3px;">
		<%= isSwe ? "Språket är inte aktiverat - visar standardspråket om fältet är tomt!" : "Language is not active - default language is shown if field is empty!" %>
	</div>
	</c:if></td>
</tr>
</vel:velocity>
<tr>
	<td colspan="2" class="imcmsAdmForm">
        <div id="editor"><%
	        if (rows == 1) { %>
	          <input type="text" name="text" id="text_1row" tabindex="1" value="<%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %>" style="width:100%;" /><%
	        } else { %>
            <textarea name="text" tabindex="1" id="text" cols="125" rows="<%= (rows > 1) ? rows : 25 %>" style="overflow: auto; width: 100%;"><%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %></textarea><%
	        } %>
        </div>
    </td>
</tr>
<vel:velocity>
<tr>
	<td>
        <% if (showModeEditor) { %>
<script type="text/javascript">

function getElementsByClassAttribute(node, tagname, sClass) {
	try {
		var result = new Array();
		var elements = node.getElementsByTagName(tagname);
		for (i = 0, j = 0; i < elements.length; ++i) {
				var element = elements[i];
				if (element.className == sClass) {
						result[j++] = element;
				}
		}
		return result;
	} catch (e) {
		return null ;
	}
}
				
function setTextMode() {
	try {
		var editors = getElementsByClassAttribute(document, "table", "htmlarea") ;
		var editor = editors[0];
		if (editor) {
			var textarea = document.getElementById("text");
			textarea.value = xinha_editors.text.getHTML();
			xinha_editors.text.deactivateEditor();
			editor.parentNode.replaceChild(textarea,editor);
			textarea.style.width = editor.style.width;
			textarea.style.height = editor.style.height;
			textarea.style.display = "block";
			document.forms[0].onsubmit = null ;
		}
	} catch (e) {}
}
function setHtmlMode() {
		var hasEditor = false ;
		try {
			var editors = getElementsByClassAttribute(document, "table", "htmlarea");
			hasEditor = editors[0];
			if (!hasEditor && getCookie("imcms_hide_editor") != "true") {
				Xinha.startEditors(xinha_editors) ;
				setTimeout(function() {
					try {
						xinha_editors.text.focusEditor() ;
					} catch (e) {}
				}, 500);
			}
		} catch (e) {}
}
</script>
        <% } %>
        <% if (showModeText && showModeHtml) { %>
        <input type="radio" name="format_type" id="format_type_text" value="0" <% if (TextDomainObject.TEXT_TYPE_PLAIN==textEditPage.getType()) { %> checked<% } %>
               <% if (showModeEditor) { %>onclick="setTextMode()"<% } %>>
        <label for="format_type_text">Text</label>
        <input type="radio" name="format_type" id="format_type_html" value="1" <% if (TextDomainObject.TEXT_TYPE_PLAIN!=textEditPage.getType()) { %> checked<% } %> 
               <% if (showModeEditor) { %>onclick="setHtmlMode()"<% } %>>
        <label for="format_type_html">Editor/HTML</label>
        <% } else if (showModeText) { %>
            <input type="hidden" name="format_type" id="format_type_text_hidden" value="<%= TextDomainObject.TEXT_TYPE_PLAIN %>">
        <% } else if (showModeHtml) { %>
            <input type="hidden" name="format_type" id="format_type_html_hidden" value="<%= TextDomainObject.TEXT_TYPE_HTML %>">
        <% } %>
    </td>
    <td align="right">
            <input tabindex="2" type="submit" class="imcmsFormBtn" name="ok" value="  <? templates/sv/change_text.html/2006 ?>  ">
            <input tabindex="3" type="submit" class="imcmsFormBtn" name="save" value="  <? templates/sv/change_text.html/save ?>  ">
            <input tabindex="4" type="reset" class="imcmsFormBtn" value="<? templates/sv/change_text.html/2007 ?>">
            <input tabindex="5" type="submit" class="imcmsFormBtn" name="cancel" value=" <? global/back ?> ">
    </td>
</tr>
</table>
#gui_bottom()
#gui_outer_end()
</form><%

if (showModeEditor) { %>

<script type="text/javascript"><%
if (TextDomainObject.TEXT_TYPE_PLAIN == textEditPage.getType() && !editorHidden) { %>
var oTimerHide = null ;
if (window.attachEvent) {
	window.attachEvent("onload",      function(){ oTimerHide = window.setInterval("hideLoadingDivBug()", 50); }) ;
} else if (window.addEventListener) {
	window.addEventListener("load",   function(){ oTimerHide = window.setInterval("hideLoadingDivBug()", 50); }, true) ;
}
function hideLoadingDivBug() {
	try {
		xinha_editors.text.removeLoadingMessage() ;
		window.clearInterval(oTimerHide) ;
	} catch (e) {}
}<%
} %>

function toggleEditorOnOff(on) {
	if (on) {
		setCookie("imcms_hide_editor", "true") ;
		document.getElementById("editorOnOffBtn1").style.display = "none" ;
		document.getElementById("editorOnOffBtn0").style.display = "block" ;
	} else {
		setCookie("imcms_hide_editor", "") ;
		document.getElementById("editorOnOffBtn1").style.display = "block" ;
		document.getElementById("editorOnOffBtn0").style.display = "none" ;
	}
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
	sCookieCont += (expire == null) ? "" : "\; expires=" + expire.toGMTString();
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
}
</script><%
} %>
<script type="text/javascript">
var ua = navigator.userAgent.toLowerCase() ;
var isIE7   = ua.indexOf("msie 7") != -1 ;
var isGecko = ua.indexOf("gecko") != -1 ;
var isMac   = ua.indexOf("mac") != -1 ;

function setEditorSize() {
	var oTextArea = document.getElementById("text") ;
	if (oTextArea) {
		var offsetH = (isMac && isGecko) ? 270 : (isIE7) ? 260 : 250 ;
		var winH = (window.innerHeight) ? window.innerHeight : (document.body && document.body.offsetHeight) ? document.body.offsetHeight - 4 : document.body.clientHeight ;
		if (winH > 400) {
			oTextArea.style.height = (winH - offsetH) + "px" ;
		}
	}
}<%
if (rows == 0) { %>
setEditorSize() ;
if (window.attachEvent) {
	window.attachEvent("onresize",    function(){ setEditorSize(); }) ;
} else if (window.addEventListener) {
	window.addEventListener("resize", function(){ setEditorSize(); }, true) ;
}<%
} %>

var oSessionTimer ;
var sessionTimeOutMs = <%= session.getMaxInactiveInterval() * 1000 %> ;
var firstVarningTimeMs = 300000 ;<% /* 5 minutes */ %>
var secondCountTimeS = 60 ;<% /* Count every second for the last minute */ %>
var timeLoaded = (new Date()).getTime() ;
var hasQuickRefresh = false ;
var oMessageDiv ;
try {
	oMessageDiv = document.getElementById("messageDiv") ;
} catch(e) {}

function initSessionChecker() {
	try {
		oSessionTimer = setInterval("sessionChecker()", 10000) ;
	} catch(e) {}
}
function sessionChecker() {
	try {
		var timeNow = (new Date()).getTime() ;<% /*
		//var mess = "timeLoaded: " + timeLoaded + ", timeNow: " + timeNow + ", diff: " + (timeNow - timeLoaded) ;
		//mess += ", (sessionTimeOutMs - firstVarningTimeMs): " + (sessionTimeOutMs - firstVarningTimeMs) ;*/ %>
		if ((timeNow - timeLoaded) > (sessionTimeOutMs - firstVarningTimeMs)) {
			var timeLeft = Math.round(-((timeNow - timeLoaded) - sessionTimeOutMs)/1000) ;<%
			//mess += " - timeLeft: " + timeLeft ;%>
			if (!hasQuickRefresh && timeLeft <= secondCountTimeS + 10) {
				hasQuickRefresh = true ;
				clearInterval(oSessionTimer) ;
				oSessionTimer = setInterval("sessionChecker()", 1000) ;
			}
			if (timeLeft >= secondCountTimeS) {
				oMessageDiv.innerHTML = <%= isSwe ?
						"'<b>Varning!<b/> Din sessionstid går ut om ' + Math.ceil(timeLeft/60) + ' minuter. Spara dina ändringar!'" :
						"'<b>Warning!<b/> Your session time will expire in ' + Math.ceil(timeLeft/60) + ' minutes. Please save your changes!'" %> ;
			} else if (timeLeft > 0) {
				oMessageDiv.innerHTML = <%= isSwe ?
						"'<b>Varning!<b/> Din sessionstid går ut om ' + timeLeft + ' sekunder. Spara dina ändringar!'" :
						"'<b>Warning!<b/> Your session time will expire in ' + timeLeft + ' seconds. Please save your changes!'" %> ;
			} else {
				clearInterval(oSessionTimer) ;
				oMessageDiv.innerHTML = <%= isSwe ?
						"'<b>Varning!<b/> Din sessionstid har gått ut. Öppna ett annat fönster och logga in - återvänd sedan och spara dina ändringar!'" :
						"'<b>Warning!<b/> Your session time has expired. Open another window and login - then return to this window and save your changes.'" %> ;
			}
			oMessageDiv.style.display = "block" ;
		}<%
		//document.getElementById("theLabel").innerHTML = mess ;%>
	} catch(e) {}
}
if (window.attachEvent) {
	window.attachEvent("onload",    function(){ initSessionChecker(); }) ;
} else if (window.addEventListener) {
	window.addEventListener("load", function(){ initSessionChecker(); }, true) ;
}

function popWinOpen(winW,winH,sUrl,sName,iResize,iScroll,iStatus) {
	if (screen) {
		if ((screen.height - winH) < 150) {
			var winX = (screen.width - winW) / 2;
			var winY = 0;
		} else {
			var winX = (screen.width - winW) / 2;
			var winY = (screen.height - winH) / 2;
		}
		var popWindow = window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",status=" + iStatus + ",width=" + winW + ",height=" + winH + ",top=" + winY + ",left=" + winX + "");
		if (popWindow) popWindow.focus();
	} else {
		window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",status=" + iStatus + ",width=" + winW + ",height=" + winH);
	}
}
function openTextRestorer() {
	popWinOpen(800,600,"<%= request.getContextPath() %>/imcms/eng/jsp/text_restorer.jsp?meta_id=<%= textEditPage.getDocumentId() %>&txt=<%= textEditPage.getTextIndex() %>","textRestorerWin",1,1,1) ;
}
</script>


</body>
</html>
</vel:velocity>
