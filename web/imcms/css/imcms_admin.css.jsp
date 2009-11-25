<%@ page
	import="org.apache.oro.text.perl.Perl5Util, org.apache.commons.lang.StringUtils"
	contentType="text/css"
%><%!

/* *******************************************************************************************
 *         Method that writes the right fontSize, depending on the browser                   *
 ******************************************************************************************* */

private int fontSize( int size, boolean isNS ) {
	return (isNS) ? size + 1 : size ;
}

%><%

Perl5Util re = new Perl5Util() ;

/* Check browser */

String uAgent   = StringUtils.defaultString(request.getHeader("USER-AGENT")) ;
boolean isIE    = re.match("/(MSIE \\d)/i", uAgent) ;
boolean isGecko = re.match("/Gecko/i", uAgent) ;
boolean isNS    = !isGecko && !isIE && re.match("/Mozilla/i", uAgent) ;

String BORDER_COLOR_NORMAL     = "#668db6 #000 #000 #668db6" ;
String BORDER_COLOR_NORMAL_SUB = "#668db6 #999 #999 #668db6" ;
String BORDER_COLOR_NORMAL_ACT = "#000 #668db6 #668db6 #000" ;
String BORDER_COLOR_DISABLED   = "#dae4ef #999 #999 #dae4ef" ;

if (isGecko) {
	BORDER_COLOR_NORMAL   = "#466d96 #333 #333 #466d96" ;
}

/* *******************************************************************************************
 *         Tests                                                                             *
 ******************************************************************************************* */ %>

.imcmsAdmHeadingTop {
	font-weight: bold;
	font-size: <%= fontSize(17, isNS) %>px;
	font-family: Tahoma,Arial,Verdana,sans-serif;
	color: #fff;
	padding: 5px 0 0 0;
}

.imcmsAdmHeading {
	font-weight: bold;
	font-size: <%= fontSize(11, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #20568d;
	padding: 5px 0 0 0;
}

.imcmsAdmText, .imcmsAdmTable TD, INPUT, SELECT, TEXTAREA, .imcmsAdmForm {
	font-size: <%= fontSize(11, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #000;
}
TEXTAREA.FileAdminEditEditor {
	font: 11px "Courier New", Courier, monospace;
	overflow: auto;
}

.imcmsAdmComment, .imcmsAdmTable .imcmsAdmComment, .imcmsAdmTextSmall, .imcmsAdmTable .imcmsAdmTextSmall, .imcmsAdmFormSmall {
	font-size: <%= fontSize(10, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #000;
}

.imcmsAdmDim, .imcmsAdmTable .imcmsAdmDim {
	font-size: <%= fontSize(10, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #999;
}

<%  /* DocFoot notations */ %>

.imNote {
	font-size: <%= fontSize(10, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #c00;
}

.imNoteComment {
	font-size: <%= fontSize(10, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #999;
}

<%
/* *******************************************************************************************
 *         BG Colors                                                                         *
 ******************************************************************************************* */ %>

.imcmsAdmBgHead { background-color:#20568d; color:#fff; }
.imcmsAdmBgCont { background-color:#f5f5f7; color:#000; }
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
	background-color: #20568d;
	color: #fff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668db6;
	border-color: <%= BORDER_COLOR_NORMAL %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %>px 8px; }

.imcmsFormBtnSub {
	background-color: #4076AD;
	color: #fff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668db6;
	border-color: <%= BORDER_COLOR_NORMAL_SUB %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %>px 8px; }

.imcmsFormBtnActive {
	background-color: #30669d;
	color: #fff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px inset #668db6;
	border-color: <%= BORDER_COLOR_NORMAL_ACT %>;
	cursor:pointer;
	padding: 3px 8px 1px 8px; }

.imcmsFormBtnDisabled {
	background-color: #b8c6d5;
	color: #fff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #dae4ef;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %>px 8px; }

.imcmsFormBtnSubDisabled {
	background-color: #b8c6d5;
	color: #fff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #dae4ef;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %>px 8px; }

<% /* Small */ %>

.imcmsFormBtnSmall {
	background-color: #20568d;
	color: #fff;
	font: 10px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668db6;
	border-color: <%= BORDER_COLOR_NORMAL %>;
	cursor:pointer;
	padding: <%= isGecko ? 1 : 0 %>px 2px; }

.imcmsFormBtnSmallDisabled {
	background-color: #b8c6d5;
	color: #fff;
	font: 10px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #dae4ef;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 1 : 0 %>px 2px; }<%
}

/* *******************************************************************************************
 *         Misc                                                                              *
 ******************************************************************************************* */ %>

.red   { color:#c00; }
.white { color:#fff; }
.lighterBlue { color:#20568d;}

.error { color: red; }

BODY {
	margin: 30px 10px;
	background-color:#efece7;
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
	color:#fff;
	text-decoration:none;
}

TD.NavBtnTextAct   { cursor: default; }
TD.NavBtnTextInact { cursor: pointer; }
