<%@ page
	import="org.apache.commons.lang.StringUtils"
	contentType="text/css"
%><%!

/* *******************************************************************************************
 *         Method that writes the right fontSize, depending on the browser                   *
 ******************************************************************************************* */

private int fontSize( int size, boolean isNS ) {
	return (isNS) ? size + 1 : size ;
}

%><%

/* Check browser */

String uAgent    = StringUtils.defaultString(request.getHeader("USER-AGENT")).toLowerCase() ;
boolean isIE     = uAgent.contains("msie") ;
boolean isGecko  = uAgent.contains("gecko") ;
boolean isWebKit = uAgent.contains("webkit") ;
boolean isNS     = false ;//NO NEED FOR NS 4 SUPPORT!!!      uAgent.contains("mozilla") && !isIE && !isGecko && !isWebKit ;

String BORDER_COLOR_NORMAL     = "#668DB6 #000000 #000000 #668DB6" ;
String BORDER_COLOR_NORMAL_SUB = "#668DB6 #999999 #999999 #668DB6" ;
String BORDER_COLOR_NORMAL_ACT = "#000000 #668DB6 #668DB6 #000000" ;
String BORDER_COLOR_DISABLED   = "#DAE4EF #999999 #999999 #DAE4EF" ;

if (isGecko) {
	BORDER_COLOR_NORMAL   = "#466D96 #333333 #333333 #466D96" ;
}

/* *******************************************************************************************
 *         Tests                                                                             *
 ******************************************************************************************* */ %>

.imcmsAdmHeadingTop {
	font-weight: bold;
	font-size: <%= fontSize(17, isNS) %>px;
	font-family: Tahoma,Arial,Verdana,sans-serif;
	color: #ffffff;
	padding: 5px 0 0 0;
}

.imcmsAdmHeading {
	font-weight: bold;
	font-size: <%= fontSize(11, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #20568D;
	padding: 5px 0 0 0;
}

.imcmsAdmText, .imcmsAdmTable TD, INPUT, SELECT, TEXTAREA, .imcmsAdmForm {
	font-size: <%= fontSize(11, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #000000;
}

.imcmsAdmComment, .imcmsAdmTable .imcmsAdmComment, .imcmsAdmTextSmall, .imcmsAdmTable .imcmsAdmTextSmall, .imcmsAdmFormSmall {
	font-size: <%= fontSize(10, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #000000;
}

.imcmsAdmDim, .imcmsAdmTable .imcmsAdmDim {
	font-size: <%= fontSize(10, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #999999;
}

<%  /* DocFoot notations */ %>

.imNote {
	font-size: <%= fontSize(10, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #cc0000;
}

.imNoteComment {
	font-size: <%= fontSize(10, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #999999;
}

<%
/* *******************************************************************************************
 *         BG Colors                                                                         *
 ******************************************************************************************* */ %>

.imcmsAdmBgHead { background-color:#20568D; color:#ffffff; }
.imcmsAdmBgCont { background-color:#f5f5f7; color:#000000; }
.imcmsAdmBorder { background-color:#e1ded9; }
input.imcmsDisabled { background-color:#ccc; }

<%
/* *******************************************************************************************
 *         Buttons                                                                           *
 ******************************************************************************************* */

if (isNS) { %>

.imcmsFormBtn, .imcmsFormBtnActive, .imcmsFormBtnDisabled, .imcmsAdmTable TD .imcmsFormBtn {
	font-size: 12px;
	font-family: Tahoma, Arial, sans-serif;
	color: #000;
}

.imcmsFormBtnSmall, .imcmsFormBtnSmallDisabled {
	font-size: 11px;
	font-family: Tahoma, Arial, sans-serif;
	color: #000;
}<%
} else { %>

<% /* Normal */ %>

.imcmsFormBtn {
	background-color: #20568D;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668DB6;
	border-color: <%= BORDER_COLOR_NORMAL %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %>px 8px; }

.imcmsFormBtnSub {
	background-color: #4076ad;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668DB6;
	border-color: <%= BORDER_COLOR_NORMAL_SUB %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %>px 8px; }

.imcmsFormBtnActive {
	background-color: #30669D;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px inset #668DB6;
	border-color: <%= BORDER_COLOR_NORMAL_ACT %>;
	cursor:pointer;
	padding: 3px 8px 1px 8px; }

.imcmsFormBtnDisabled {
	background-color: #B8C6D5;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #DAE4EF;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %>px 8px; }

.imcmsFormBtnSubDisabled {
	background-color: #B8C6D5;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #DAE4EF;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %>px 8px; }

<% /* Small */ %>

.imcmsFormBtnSmall {
	background-color: #20568D;
	color: #ffffff;
	font: 10px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668DB6;
	border-color: <%= BORDER_COLOR_NORMAL %>;
	cursor:pointer;
	padding: <%= isGecko ? 1 : 0 %>px 2px; }

.imcmsFormBtnSmallDisabled {
	background-color: #B8C6D5;
	color: #ffffff;
	font: 10px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #DAE4EF;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 1 : 0 %>px 2px; }<%
}

/* *******************************************************************************************
 *         Misc                                                                              *
 ******************************************************************************************* */ %>

.red   { color:#cc0000; }
.white { color:#ffffff; }
.lighterBlue { color:#20568D;}

.error { color: red; }

BODY {
	margin: 30px 10px;
	background-color:#efece7;
	<%= "overflow: -moz-scrollbars-vertical;" %>
}

TH   { text-align: left; }

B    { font-weight:bold; }
I    { font-style:italic; }<%
if (!isNS) { %>
TH   { font-size: 90%; }<%
}

/* ******************************************************************************************
 *      Admin Manager Tab                                                                   *
 ********************************************************************************************/ %>

SPAN.NavBtnTextAct, SPAN.NavBtnTextAct A:link, SPAN.NavBtnTextAct A:visited, SPAN.NavBtnTextAct A:hover {
	font: bold 10px Verdana, Geneva, Helvetica, sans-serif;
	color:#20568d;
	text-decoration:none;
}

SPAN.NavBtnTextInact, SPAN.NavBtnTextInact A:link, SPAN.NavBtnTextInact A:visited, SPAN.NavBtnTextInact A:hover {
	font: 10px Verdana, Geneva, Helvetica, sans-serif;
	color:#ffffff;
	text-decoration:none;
}

TD.NavBtnTextAct   { cursor: default; }
TD.NavBtnTextInact { cursor: pointer; }
