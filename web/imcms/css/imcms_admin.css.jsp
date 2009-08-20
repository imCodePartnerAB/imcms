<%@ page
	import="org.apache.oro.text.perl.Perl5Util"
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

String uAgent = request.getHeader("USER-AGENT") ;
boolean isIE  = re.match("/(MSIE 4|MSIE 5|MSIE 5\\.5|MSIE 6|MSIE 7|MSIE 8)/i", uAgent) ;
boolean isGecko = re.match("/Gecko/i", uAgent) ;
boolean isNS  = re.match("/Mozilla/i", uAgent) && !isGecko && !isIE ;

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
	padding: 5 0 0 0;
}

.imcmsAdmHeading {
	font-weight: bold;
	font-size: <%= fontSize(11, isNS) %>px;
	font-family: Tahoma,Arial,sans-serif;
	color: #20568D;
	padding: 5 0 0 0;
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
	color: #ffffff;
}

.imcmsFormBtnSmall, .imcmsFormBtnSmallDisabled {
	font-size: 11px;
	font-family: Tahoma, Arial, sans-serif;
	color: #ffffff;
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
	padding: <%= isGecko ? 3 : 2 %> 8; }

.imcmsFormBtnSub {
	background-color: #4076ad;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668DB6;
	border-color: <%= BORDER_COLOR_NORMAL_SUB %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %> 8; }

.imcmsFormBtnActive {
	background-color: #30669D;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px inset #668DB6;
	border-color: <%= BORDER_COLOR_NORMAL_ACT %>;
	cursor:pointer;
	padding: 3 8 1 8; }

.imcmsFormBtnDisabled {
	background-color: #B8C6D5;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #DAE4EF;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %> 8; }

.imcmsFormBtnSubDisabled {
	background-color: #B8C6D5;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #DAE4EF;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 3 : 2 %> 8; }

<% /* Small */ %>

.imcmsFormBtnSmall {
	background-color: #20568D;
	color: #ffffff;
	font: 10px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668DB6;
	border-color: <%= BORDER_COLOR_NORMAL %>;
	cursor:pointer;
	padding: <%= isGecko ? 1 : 0 %> 2; }

.imcmsFormBtnSmallDisabled {
	background-color: #B8C6D5;
	color: #ffffff;
	font: 10px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #DAE4EF;
	border-color: <%= BORDER_COLOR_DISABLED %>;
	cursor:pointer;
	padding: <%= isGecko ? 1 : 0 %> 2; }<%
}

/* *******************************************************************************************
 *         Misc                                                                              *
 ******************************************************************************************* */ %>

.red   { color:#cc0000; }
.white { color:#ffffff; }
.lighterBlue { color:#20568D;}

.error { color: red; }

BODY {
	margin: 30 10;
	background-color:#efece7;
	overflow: -moz-scrollbars-vertical;
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
