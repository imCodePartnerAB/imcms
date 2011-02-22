<%@ page
	
	import="java.text.DateFormat,
	        java.text.SimpleDateFormat,
	        java.util.Date,
	        java.util.Calendar,
	        java.sql.ResultSet,
	        java.sql.Connection,
	        java.sql.PreparedStatement,
	        org.apache.commons.lang.StringUtils,
	        imcode.server.document.textdocument.TextDomainObject,
	        imcode.util.Parser,
	        java.util.Locale,
	        com.imcode.imcms.api.*,
	        org.apache.commons.lang.StringEscapeUtils,
	        java.net.URLEncoder"
	
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	
%><%@taglib prefix="vel" uri="imcmsvelocity"
%><%!

private String colorCodeSimple(String html) {
	html = StringEscapeUtils.unescapeHtml(html) ;
	html = html.replaceAll("(<[^>]+?>)", "#SPAN#$1#/SPAN#") ;
	html = StringEscapeUtils.escapeHtml(html) ;
	html = html
				.replaceAll("#SPAN#", "<span>")
				.replaceAll("#/SPAN#", "</span>") ;
	return html ;
}

%><%

ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest(request) ;
DocumentService documentService     = imcmsSystem.getDocumentService() ;
DatabaseService databaseService     = imcmsSystem.getDatabaseService() ;

String action   = (request.getParameter("action") != null)   ? request.getParameter("action")  : "" ;
String view     = (request.getParameter("view") != null)     ? request.getParameter("view")     : "" ;
String restore  = (request.getParameter("restore") != null)  ? request.getParameter("restore")  : "" ;
String save     = (request.getParameter("save") != null)     ? request.getParameter("save")     : "" ;
int meta_id     = (request.getParameter("meta_id") != null)  ? Integer.parseInt(request.getParameter("meta_id")) : 0 ;
int txtNo       = (request.getParameter("txt") != null)      ? Integer.parseInt(request.getParameter("txt")) : 0 ;




String sSql ;

Connection connection = null ;
PreparedStatement preparedStatement = null ;
ResultSet resultSet = null ;

boolean isSwe = false ;
try {
	isSwe =	imcmsSystem.getCurrentUser().getLanguage().getIsoCode639_2().equals("swe");
} catch (Exception e) {}


//DateFormat df  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
DateFormat dfD = new SimpleDateFormat("yyyy-MM-dd") ;
DateFormat dfT = new SimpleDateFormat("HH:mm:ss") ;
DateFormat dfS = isSwe ?
                   new SimpleDateFormat("d MMMM -yyyy' kl. 'HH:mm:ss", new Locale("sv")) :
                   new SimpleDateFormat("MMMM d -yyyy' at 'HH:mm:ss", Locale.ENGLISH );


Calendar cal = Calendar.getInstance() ;

cal.setTime(new Date()) ;
cal.add(Calendar.DATE, +1) ;
Date tomorrow = cal.getTime() ;

cal.setTime(new Date()) ;
cal.add(Calendar.DATE, -2) ;
Date last3days = cal.getTime() ;

cal.setTime(new Date()) ;
cal.add(Calendar.DATE, -6) ;
Date lastWeek = cal.getTime() ;

cal.setTime(new Date()) ;
cal.add(Calendar.DATE, -13) ;
Date last2Weeks = cal.getTime() ;

cal.setTime(new Date()) ;
cal.add(Calendar.MONTH, -1) ;
Date lastMonth = cal.getTime() ;

cal.setTime(new Date()) ;
cal.add(Calendar.MONTH, -3) ;
Date last3Months = cal.getTime() ;

cal.setTime(new Date()) ;
cal.add(Calendar.MONTH, -6) ;
Date last6Months = cal.getTime() ;

cal.setTime(new Date()) ;
cal.add(Calendar.YEAR, -1) ;
Date lastYear = cal.getTime() ;



String dateSpan ;

if (action.equals("setDateSpan")) {
	dateSpan = (request.getParameter("date_span") != null) ? request.getParameter("date_span") : "" ;
	session.setAttribute("EDITOR_SAVER_DATE_SPAN", dateSpan) ;
} else if (session.getAttribute("EDITOR_SAVER_DATE_SPAN") != null) {
	dateSpan = (String) session.getAttribute("EDITOR_SAVER_DATE_SPAN") ;
} else {
	// OLD DEFAULT - ONE DAY: dateSpan = " AND t.modified_datetime >= '" + dfD.format(new Date()) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'" ;
	dateSpan = "" ; // NEW DEFAULT - ALL
}

String sContent  = "" ;
String sContentRaw = "" ;
Date dDate       = null ;
int type         = 0 ;
String firstName = "" ;
String lastName  = "" ;
String userName ;
boolean isSavedInTextField = false ;

if (view.matches("^\\d+$") || restore.matches("^\\d+$") || save.matches("^\\d+$")) {
	boolean viewAsHtml = (request.getParameter("html") != null) ;
	try {
		connection = databaseService.getConnection() ;
		sSql = "SELECT t.text, t.modified_datetime, t.type, u.first_name, u.last_name\n" +
					 "FROM texts_history t INNER JOIN users u ON t.user_id = u.user_id\n" +
					 "WHERE t.counter = ?" ;
		preparedStatement = connection.prepareStatement(sSql) ;
		preparedStatement.setInt(1, (view.matches("\\d+") ? Integer.parseInt(view) : (save.matches("\\d+") ? Integer.parseInt(save) : Integer.parseInt(restore)))) ;
		resultSet = preparedStatement.executeQuery() ;
		boolean hasContent = false ;
		while (resultSet.next()) {
			sContent  = resultSet.getString(1) ;
			sContentRaw = sContent ;
			dDate     = resultSet.getTimestamp(2) ;
			type      = resultSet.getInt(3) ;
			firstName = resultSet.getString(4) ;
			lastName  = resultSet.getString(5) ;
			if (type == TextDomainObject.TEXT_TYPE_PLAIN) {
				String[] vp = new String[]{
						"&", "&amp;",
						"<", "&lt;",
						">", "&gt;",
						"\"", "&quot;",
						"\r\n", "\n",
						"\r", "\n",
						"\n", "<br />\n",
				};
				sContent = Parser.parseDoc( sContent, vp ) ;
			}
			hasContent = true ;
		}
		if (hasContent && meta_id > 0 && txtNo > 0 && save.matches("\\d+")) {
			TextDocument thisDoc = documentService.getTextDocument(meta_id) ;
			thisDoc.setTextField(txtNo, sContentRaw, ((type == TextDomainObject.TEXT_TYPE_PLAIN) ? TextDocument.TextField.Format.PLAIN : TextDocument.TextField.Format.HTML)) ;
			documentService.saveChanges(thisDoc) ;
			isSavedInTextField = true ;
		}
	} catch(Exception e) {
		//out.print("ERROR: Ett fel har uppstått! " + e.getMessage()) ;
	} finally {
		try {
			if (null != resultSet) resultSet.close() ;
			if (null != preparedStatement) preparedStatement.close() ;
			if (null != connection) connection.close() ;
		} catch(Exception ex) {}
	} %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Viewer</title>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/skolverket.css.jsp">

<style type="text/css">
<!-- 
BODY {
	background-color: #ffffff;
	margin: 0;
	padding: 0;
}
BODY, DIV, P, SPAN, TH, TD {
	font: 10px Verdana;
	color: #000000;
}
#htmlContent {
	margin: 10px;
}
PRE {
	font: 11px 'Courier New', Courier, monospace;
	color: #000000;
}
PRE SPAN {
	color: #0000cc;
}
-->
</style><%

if (isSavedInTextField) { %>
<script type="text/javascript">
try {
	parent.parent.opener.document.location = "<%= request.getContextPath() %>/servlet/ChangeText?meta_id=<%= meta_id %>&txt=<%= txtNo %>&label=<%=
	URLEncoder.encode( isSwe ? "Texten återställd!" : "The text is restored!", "UTF-8") %>" ;
	parent.parent.opener.focus() ;
	parent.window.close() ;
} catch(e) {
	alert("<%= isSwe ?
		"Texten är sparad, men det gick inte att stänga fönstret och ladda om det andra!" :
		"The text is saved, but it wasn't possible to close this window and reload the other one!" %>") ;
}
</script><%
} %>

</head>
<body>

<div style="width:100%; background-color:#ffff66; border-bottom: 1px solid #000000; font: bold 10px/12px verdana; margin:0; padding:0;">
	<div style="padding:5px;">
		<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
		<tr>
			<td><%
			if (isSwe) { %>
			Version från <%= dfS.format(dDate) %> av <%= (firstName + " " + lastName) .trim() %>.<%
			} else { %>
			Version from <%= dfS.format(dDate) %> by <%= (firstName + " " + lastName) .trim() %>.<%
			} %></td>
			<td align="right">[<%= (type == TextDomainObject.TEXT_TYPE_PLAIN) ? (isSwe ? "Ren text" : "Plain text") : "Editor/HTML" %>]</td>
		</tr>
		</table>
	</div>
</div>

<div id="htmlContent"><%
if (viewAsHtml) { %>
<pre><%= colorCodeSimple(StringEscapeUtils.escapeHtml(sContent)) %></pre><%
} else { %>
<%= sContent %><%
}
%></div>

<form action="">
<input type="hidden" name="theContent" id="theContent" value="<%= StringEscapeUtils.escapeHtml(sContentRaw) %>" />
</form>

<%
if (restore.matches("\\d+")) { %>
<script language="JavaScript" type="text/javascript">
<!--
function initRestore() {
	try {
		var isPlainText = <%= type == TextDomainObject.TEXT_TYPE_PLAIN %> ;
		var theContent = document.getElementById("theContent").value ;
		var theContentHasMoreThanOneLine = theContent.split("\\r?\\n").length < 2 ;
		var openerTextField        = eval("parent.parent.opener.document.getElementById('text_1row')") ;
		var openerTextArea         = eval("parent.parent.opener.document.getElementById('text')") ;
		var isOneLinerMode         = openerTextField ;
		var openerFormatText       = eval("parent.parent.opener.document.getElementById('format_type_text')") ;
		var openerFormatHtml       = eval("parent.parent.opener.document.getElementById('format_type_html')") ;
		var openerIsFormatTextOnly = eval("parent.parent.opener.document.getElementById('format_type_text_hidden')") ;
		var openerIsFormatHtmlOnly = eval("parent.parent.opener.document.getElementById('format_type_html_hidden')") ;
		var openerIsFormatText     = openerFormatText && openerFormatText.checked ;
		var openerIsFormatHtml     = openerFormatHtml && openerFormatHtml.checked ;
		var errorMess = "" ;
		
		// rows = 1, but content too long
		
		if (isOneLinerMode && theContentHasMoreThanOneLine) {
			errorMess += '<%= isSwe ?
			           "\\n\\n- denna text var sparad med mer än en rad, men din text-editor har bara en rad aktiv.\\nAnvänd [Spara i textfältet och ladda om] istället!" :
			           "\\n\\n- this text was saved with more than one lines, but your text editor only has one line active.\\nPlease use [Save in textfield and reload] instead!" %>' ;
		}
		
		// Wrong format - and not changeable
		
		if ((isPlainText && openerIsFormatHtmlOnly) || (!isPlainText && openerIsFormatTextOnly)) {
			errorMess += '<%= isSwe ?
			           "\\n\\n- denna text var sparad i ett format som inte längre är tillgängligt i din text editor.\\nAnvänd [Spara i textfältet och ladda om] istället!" :
			           "\\n\\n- this text was saved with a format that is no longer avaliable in your text editor.\\nPlease use [Save in textfield and reload] instead!" %>' ;
		}
		
		if (errorMess != "") {
			errorMess = '<%= isSwe ? "Kan inte spara p.g.a:" : "Can\\\'t save because:" %>' + errorMess ;
			alert(errorMess) ;
			return ;
		}
		
		if (confirm("<%= isSwe ? "Vill du kopiera denna version till text-editorn?" : "Do you want to copy this version to the the text editor?" %>")) {
			
			var editor = null ;
			var editorActive = false ;
			try {
				editor = eval("parent.parent.opener.CKEDITOR.instances['text']") ;
				//var xinhaIframe = eval("parent.parent.opener.document.getElementById('XinhaIFrame_text')") ;
				editorActive = (undefined != editor) ;
			} catch (ex) {}
			
			// Copy plain text
			
			var isCopied = false ;
			
			if (isPlainText && (openerIsFormatHtml || openerIsFormatText || openerIsFormatTextOnly)) {
				if (openerTextField) {
					openerTextField.value = theContent ;
					isCopied = true ;
				} else if (editorActive) {
					parent.parent.opener.setTextMode() ;
					window.setTimeout(function() {
						openerTextArea.value = theContent;
					}, 500) ;
					isCopied = true ;
				} else if (openerTextArea) {
					openerTextArea.value = theContent ;
					isCopied = true ;
				}
				if (openerIsFormatHtml) {
					openerFormatText.checked = true ;
				}
			}
			
			// Copy HTML
			
			if (!isPlainText && (openerIsFormatHtml || openerIsFormatText || openerIsFormatHtmlOnly)) {
				if (openerTextField) {
					openerTextField.value = theContent ;
					isCopied = true ;
				} else if (editorActive) {
					editor.setData(theContent) ;
					isCopied = true ;
				} else if (openerTextArea) {
					openerTextArea.value = theContent ;
					isCopied = true ;
				}
				if (openerIsFormatText) {
					openerFormatHtml.checked = true ;
				}
			}
			if (!isCopied) {
				alert("ERROR - <%= isSwe ? "Kopieringen misslyckades! Försök kopiera manuellt istället." :
				                           "The copying failed! Try copy manually instead." %>") ;
			} else {
				parent.parent.opener.focus() ;
				parent.window.close() ;
			}
		}
	} catch(e) {
		alert("ERROR - <%= isSwe ? "Kopieringen misslyckades!" : "The copying failed!" %>\n" + e.message) ;
	}
}
initRestore() ;
//-->
</script><%
} %>

</body>
</html><%
	return ;
} else if (request.getParameter("blank") != null) { %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Viewer</title>
</head>
<body style="background-color: #ffffff;"></body>
</html><%
	return ;
}



TextDocument thisDoc = documentService.getTextDocument(meta_id) ;


String heading = isSwe ? "Tidigare versioner av textfält #TXT# på sida #META##ALIAS#" : "Earlier versions of textfield #TXT# on page #META##ALIAS#" ;

String alias = thisDoc.getAlias() != null ? thisDoc.getAlias() : "" ;
if (!alias.equals("")) {
	alias = " &nbsp;<span style='font-size:11px; font-weight:normal;' title='" + StringEscapeUtils.escapeHtml(alias) + "'>&quot;" + StringUtils.abbreviate(alias, 45) + "&quot;</span>" ;
}

heading = heading
			.replaceAll("#TXT#", txtNo + "")
			.replaceAll("#META#", meta_id + "")
			.replaceAll("#ALIAS#", alias) ;

%><vel:velocity>
<html>
<head>
<title><%= heading.replaceAll("<[^>]+?>", "") %></title>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
<script src="<%= request.getContextPath() %>/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
</head>
<body class="imcmsAdmBgCont" style="border:0; margin:0">

#gui_outer_start_noshade()
#gui_head_noshade( "<%= heading %>" )
<style type="text/css">
<!-- 
SELECT {
	font: 11px 'Courier New',Courier,monospace !important;
}
-->
</style>
<script language="JavaScript" type="text/javascript">
<!--
function doView(id) {
	if (!/^\d+$/.test(id)) return ;
	var asHtmlParam = (document.getElementById("preview_type1").checked) ? "&html=true" : "" ;
	document.getElementById("restoreIframe").src = "text_restorer.jsp?view=" + id + asHtmlParam ;
	var oBtnSave       = document.forms.selectForm.theBtnSave ;
	var oBtnCopy       = document.forms.selectForm.theBtnCopy ;
	oBtnSave.id        = id ;
	oBtnSave.disabled  = false ;
	oBtnSave.className = "imcmsFormBtnSmall" ;
	oBtnCopy.id        = id ;
	oBtnCopy.disabled  = false ;
	oBtnCopy.className = "imcmsFormBtnSmall" ;
}
function checkViewType(val) {
	var iframe    = document.getElementById("restoreIframe") ;
	var iframeSrc = iframe.src ;
	var id        = (iframeSrc.indexOf("view=") != -1) ? /view=(\d+)/.exec(iframeSrc)[1] :
	                (iframeSrc.indexOf("restore=") != -1) ? /restore=(\d+)/.exec(iframeSrc)[1] :
	                (iframeSrc.indexOf("save=") != -1) ? /save=(\d+)/.exec(iframeSrc)[1] :
	                0 ;
	if (isNaN(id) || id < 1) return ;
	if (val == "html") {
		iframe.src = "text_restorer.jsp?view=" + id + "&html=true" ;
	} else if (val == "text") {
		iframe.src = "text_restorer.jsp?view=" + id ;
	}
}

function doRestore(id) {
	if (!/^\d+$/.test(id)) return ;
	document.getElementById("restoreIframe").src = "text_restorer.jsp?restore=" + id ;
}
function doSave(id) {
	if (!/^\d+$/.test(id)) return ;
	document.getElementById("restoreIframe").src = "text_restorer.jsp?meta_id=<%= meta_id %>&txt=<%= txtNo %>&save=" + id ;
}
//-->
</script>
<table border="0" cellspacing="0" cellpadding="0">
<form action="">
<tr>
	<td><input type="button" value="<%= isSwe ? "Stäng" : "Close" %>" class="imcmsFormBtn" onClick="if (confirm('<%= isSwe ? "Vill du stänga fönstret?" : "Do you want to close the window?" %>')) window.close();"></td>
</tr>
</form>
</table>
#gui_mid_noshade()
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td colspan="2">#gui_heading("<%= isSwe ? "Granska/välj tidigare versioner" : "View/choose earlier versions" %>")</td>
</tr>
<form name="selectForm" action="text_restorer.jsp" method="post">
<input type="hidden" name="action" value="setDateSpan">
<input type="hidden" name="meta_id" value="<%= meta_id %>">
<input type="hidden" name="txt" value="<%= txtNo %>">
<tr>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td nowrap><%= isSwe ? "Visa versioner från" : "Show versions from" %> &nbsp;</td>
			<td>
			<select name="date_span" onChange="this.form.submit()">
				<option value=""><%= isSwe ? "Alla versioner" : "All versions" %></option>
				<option value=" AND t.modified_datetime >= '<%= dfD.format(new Date()) %>' AND t.modified_datetime <= '<%= dfD.format(tomorrow) %>'"<%=
				dateSpan.equals(" AND t.modified_datetime >= '" + dfD.format(new Date()) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'") ? " selected" : "" %>><%
				%><%= isSwe ? "Dagens" : "Today" %></option>
				<option value=" AND t.modified_datetime >= '<%= dfD.format(last3days) %>' AND t.modified_datetime <= '<%= dfD.format(tomorrow) %>'"<%=
				dateSpan.equals(" AND t.modified_datetime >= '" + dfD.format(last3days) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'") ? " selected" : "" %>><%
				%><%= isSwe ? "Senaste 3 dagarna" : "Last 3 days" %></option>
				<option value=" AND t.modified_datetime >= '<%= dfD.format(lastWeek) %>' AND t.modified_datetime <= '<%= dfD.format(tomorrow) %>'"<%=
				dateSpan.equals(" AND t.modified_datetime >= '" + dfD.format(lastWeek) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'") ? " selected" : "" %>><%
				%><%= isSwe ? "Senaste veckan" : "Last week" %></option>
				<option value=" AND t.modified_datetime >= '<%= dfD.format(last2Weeks) %>' AND t.modified_datetime <= '<%= dfD.format(tomorrow) %>'"<%=
				dateSpan.equals(" AND t.modified_datetime >= '" + dfD.format(last2Weeks) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'") ? " selected" : "" %>><%
				%><%= isSwe ? "Senaste 2 veckorna" : "Last 2 weeks" %></option>
				<option value=" AND t.modified_datetime >= '<%= dfD.format(lastMonth) %>' AND t.modified_datetime <= '<%= dfD.format(tomorrow) %>'"<%=
				dateSpan.equals(" AND t.modified_datetime >= '" + dfD.format(lastMonth) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'") ? " selected" : "" %>><%
				%><%= isSwe ? "Senaste månaden" : "Last month" %></option>
				<option value=" AND t.modified_datetime >= '<%= dfD.format(last3Months) %>' AND t.modified_datetime <= '<%= dfD.format(tomorrow) %>'"<%=
				dateSpan.equals(" AND t.modified_datetime >= '" + dfD.format(last3Months) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'") ? " selected" : "" %>><%
				%><%= isSwe ? "Senaste 3 månaderna" : "Last 3 months" %></option>
				<option value=" AND t.modified_datetime >= '<%= dfD.format(last6Months) %>' AND t.modified_datetime <= '<%= dfD.format(tomorrow) %>'"<%=
				dateSpan.equals(" AND t.modified_datetime >= '" + dfD.format(last6Months) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'") ? " selected" : "" %>><%
				%><%= isSwe ? "Senaste 6 månaderna" : "Last 6 months" %></option>
				<option value=" AND t.modified_datetime >= '<%= dfD.format(lastYear) %>' AND t.modified_datetime <= '<%= dfD.format(tomorrow) %>'"<%=
				dateSpan.equals(" AND t.modified_datetime >= '" + dfD.format(lastYear) + "' AND t.modified_datetime <= '" + dfD.format(tomorrow) + "'") ? " selected" : "" %>><%
				%><%= isSwe ? "Senaste året" : "Last year" %></option>
			</select></td>
		</tr>
		</table></td>
		
		<td align="right">
		<input type="button" name="theBtnSave" id="save" value="<%=
		isSwe ? "Spara i textfältet och ladda om" :
			      "Save in textfield and reload" %>" class="imcmsFormBtnSmallDisabled" disabled="true" onclick="doSave(this.id); return false">
		<input type="button" name="theBtnCopy" id="copy" value="<%=
		isSwe ? "Kopiera denna version till text-editorn" :
		        "Copy this version to the the text editor" %>" class="imcmsFormBtnSmallDisabled" disabled="true" onclick="doRestore(this.id); return false"></td>
	</tr>
	</table>
	</td>
</tr>
</form>
<tr>
	<td colspan="2">#gui_hr( "cccccc" )</td>
</tr>
<form name="theForm" action=""><%


try {
	connection = databaseService.getConnection() ;
	sSql = "SELECT t.counter, t.modified_datetime, t.type, u.first_name, u.last_name, u.login_name\n" +
				 "FROM texts_history t INNER JOIN users u ON t.user_id = u.user_id\n" +
				 "WHERE t.meta_id = ? AND t.name = ?" + dateSpan + "\n" +
				 "ORDER BY t.counter DESC" ;
	preparedStatement = connection.prepareStatement(sSql) ;
	preparedStatement.setInt(1, meta_id) ;
	preparedStatement.setInt(2, txtNo) ;
	resultSet = preparedStatement.executeQuery() ;

	//out.println("<!-- " + sSql + " -->") ;
	//out.println(sSql) ;
	%>
<input type="hidden" name="selected_content" value="">
<tr valign="top">
	<td width="10%" style="padding-right:30px;">
	<div style="margin-bottom:5px;">
		<b><%= isSwe ? "Sparade versioner" : "Saved versions" %>:</b>
	</div>
	<select name="content" id="restoreSelect" size="19" style="height:220px;" onchange="doView(this.options[this.selectedIndex].value);"><%
		String lastDate = "" ;
		int itemCount = 0 ;
		while(resultSet.next()) {
			int id    = resultSet.getInt(1) ;
			dDate     = resultSet.getTimestamp(2) ;
			//type      = resultSet.getInt(3) ;
			//firstName = resultSet.getString(4) ;
			//lastName  = resultSet.getString(5) ;
			userName  = resultSet.getString(6) ;
			String sDate  = dfD.format(dDate) ;
			String sText  = !sDate.equals(lastDate) ? sDate : StringUtils.repeat("&nbsp; ", 5) ;
			if (sDate.equals(dfD.format(new Date()))) {
				sText  = !sDate.equals(lastDate) ? (isSwe ? "Idag" : "Today") + StringUtils.repeat("&nbsp;", (isSwe ? 6 : 5)) : StringUtils.repeat("&nbsp; ", 5) ;
			}
			sText    += "&nbsp;" + dfT.format(dDate) ;
			sText    += " (" + userName + ")" ;
			lastDate  = sDate ;
		%>
		<option value="<%= id %>"><%= sText %> &nbsp;</option><%
			itemCount++ ;
		}
		if (itemCount == 0) { %>
		<option value="" style="font-style:italic;"><%= isSwe ? "- Inga versioner att visa" : "- No versions to show" %> &nbsp;</option><%
		} %>
	</select>
	<div style="margin-top:10px;">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding-right:10px;"><%= isSwe ? "Förhandsgranska&nbsp;som" : "Preview&nbsp;as" %></td>
			<td><input type="radio" name="preview_type" id="preview_type0" value="text" onclick="checkViewType('text');" checked="checked" /></td>
			<td style="padding-right:10px;"><label for="preview_type0">Text</label></td>
			<td><input type="radio" name="preview_type" id="preview_type1" value="html" onclick="checkViewType('html');" /></td>
			<td><label for="preview_type1">HTML</label></td>
		</tr>
		</table>
	</div>
	<div style="margin-top:10px;" class="imcmsAdmDim"><%
		if (isSwe) { %>
		<p style="margin: 4px 0;"><b>Obs!</b></p>
		<p style="margin: 4px 0;">Om du väljer <nobr>[Kopiera denna version till text-editorn]</nobr>, kopieras bara det förhandsgranskade innehållet ner till den ordinarie editorn.</p>
		<p style="margin: 4px 0;">Vill man spara det återställda innehållet måste man själv spara.</p>
		<p style="margin: 4px 0;">Ev. innehåll i den ordinarie editorn kommer att skrivas över.</p><%
		} else { %>
		<p style="margin: 4px 0;"><b>Note!</b></p>
		<p style="margin: 4px 0;">If you choose <nobr>[Copy this version to the the text editor]</nobr>, the previewed content is only copied down to ordinary editor.</p>
		<p style="margin: 4px 0;">If you want to save the restored content you'll have to save it yourself.</p>
		<p style="margin: 4px 0;">Any previous written content in the ordinary editor will be overwritten.</p><%
		} %>
	</div></td>
	
	<td width="90%">
	<div style="margin-bottom:5px;">
		<b><%= isSwe ? "Förhandsgranska versionen:" : "Preview the version" %></b>
	</div>
	<iframe name="restoreIframe" id="restoreIframe" width="100%" height="600" frameborder="0" marginwidth="0" marginheight="0" style="height:600px;" src="text_restorer.jsp?blank=true"></iframe></td>
</tr><%
} catch(Exception e) {
	//out.print("ERROR: Ett fel har uppstått! " + e.getMessage()) ;
} finally {
	try {
		if (null != resultSet) resultSet.close() ;
		if (null != preparedStatement) preparedStatement.close() ;
		if (null != connection) connection.close() ;
	} catch(Exception ex) {}
} %>
</form>
</table>
#gui_bottom_noshade()
#gui_outer_end_noshade()
<script language="JavaScript" type="text/javascript">
<!--
function rePos() {
	if (document.getElementById) {
		try {
			var winH = (document.all) ? document.body.offsetHeight - 4 : document.body.clientHeight ;
			document.getElementById("restoreIframe").style.height = (winH - 230) + "px" ;
			document.getElementById("restoreSelect").style.height = (winH - 380) + "px" ;
		} catch (e) {}
	}
}

if (window.attachEvent) {
	window.attachEvent("onload", rePos) ;
	window.attachEvent("onresize", rePos) ;
} else if (window.addEventListener) {
	window.addEventListener("load", rePos, true) ;
	window.addEventListener("resize", rePos, true) ;
} else {
	rePos() ;
}
//-->
</script>

</body>
</html>
</vel:velocity>