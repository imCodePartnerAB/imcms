<%@ page
	import="org.apache.oro.text.perl.Perl5Util"
	contentType="text/css"
%><%!

public boolean isIE, isNS, isMoz, isMac ;

/* *******************************************************************************************
 *         Method that writes the right fontSize, depending on the browser                   *
 ******************************************************************************************* */

private int fontSize( int size ) {
	return (isNS) ? size + 1 : size ;
}

%><%
Perl5Util re = new Perl5Util() ;

/* Check browser */

String uAgent = request.getHeader("USER-AGENT") ;
isIE  = re.match("/(MSIE 4|MSIE 5|MSIE 5\\.5|MSIE 6|MSIE 7)/i", uAgent) ;
isNS  = (re.match("/Mozilla/i", uAgent) && !re.match("/Gecko/i", uAgent) && !re.match("/MSIE/i", uAgent)) ? true : false ;
isMoz = re.match("/Gecko/i", uAgent) ;
isMac = re.match("/Mac/i", uAgent) ;


if (true == false) {
	%><style><% // Dummy - To get the right colors in the editor
} %>
/* *******************************************************************************************
 *         Tests                                                                             *
 ******************************************************************************************* */

.imcmsAdmHeadingTop {
	font: bold <%= fontSize(17) %>px Tahoma,Arial,Verdana,sans-serif; color:#ffffff; padding: 5 0 0 0;
}
.imcmsAdmHeading    {
	font: bold <%= fontSize(11) %>px Tahoma,Arial,sans-serif; color:#20568D; padding: 5 0 0 0;
}

.imcmsAdmText, .imcmsAdmTable TD, INPUT, SELECT, TEXTAREA, .imcmsAdmForm {
	font: <%= fontSize(11) %>px Tahoma,Arial,sans-serif; color:#000000;
}

.imcmsAdmComment, .imcmsAdmTable .imcmsAdmComment, .imcmsAdmTextSmall, .imcmsAdmTable .imcmsAdmTextSmall, .imcmsAdmFormSmall {
	font: <%= fontSize(10) %>px Tahoma,Arial,sans-serif; color:#000000;
}

.imcmsAdmDim, .imcmsAdmTable .imcmsAdmDim {
	font: <%= fontSize(10) %>px Tahoma,Arial,sans-serif; color:#999999;
}

  /* DocFoot notations */

.imNote             { font: <%= fontSize(10) %>px Tahoma,Arial,sans-serif; color:#cc0000; }
.imNoteComment      { font: <%= fontSize(10) %>px Tahoma,Arial,sans-serif; color:#999999; }

/* *******************************************************************************************
 *         BG Colors                                                                         *
 ******************************************************************************************* */

.imcmsAdmBgHead { background-color:#20568D; color:#ffffff; }
.imcmsAdmBgCont { background-color:#f5f5f7; color:#000000; }
.imcmsAdmBorder { background-color:#e1ded9; }

/* *******************************************************************************************
 *         Buttons                                                                           *
 ******************************************************************************************* */<%
if (isNS) { %>

.imcmsFormBtn, .imcmsFormBtnDisabled, .imcmsAdmTable TD .imcmsFormBtn {
	color: #ffffff;
	font: 12px Tahoma, Arial, sans-serif;
}

.imcmsFormBtnSmall, .imcmsFormBtnSmallDisabled {
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
}<%
} else { %>

 /* Normal */

.imcmsFormBtn {
	background-color: #20568D;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: 2px outset #668DB6;
	border-color: #668DB6 #000000 #000000 #668DB6;
	cursor:hand;
	height:24;
	padding: 2 8; }

.imcmsFormBtnDisabled {
	background-color: #B8C6D5;
	color: #ffffff;
	font: 11px Tahoma, Arial, sans-serif;
	border: 2px outset #DAE4EF;
	border-color: #DAE4EF #999999 #999999 #DAE4EF;
	cursor:hand;
	height:24;
	padding: 2 8; }

 /* Small */

.imcmsFormBtnSmall {
	background-color: #20568D;
	color: #ffffff;
	font: 10px Tahoma, Arial, sans-serif;
	border: 2px outset #668DB6;
	border-color: #668DB6 #000000 #000000 #668DB6;
	cursor:hand;
	height:18;
	padding: 0 2; }

.imcmsFormBtnSmallDisabled {
	background-color: #B8C6D5;
	color: #ffffff;
	font: 10px Tahoma, Arial, sans-serif;
	border: 2px outset #DAE4EF;
	border-color: #DAE4EF #999999 #999999 #DAE4EF;
	cursor:hand;
	height:18;
	padding: 0 2; }<%
} %>

/* *******************************************************************************************
 *         Misc                                                                              *
 ******************************************************************************************* */

.red   { color:#cc0000; }
.white { color:#ffffff; }

BODY { margin: 30 10; background-color:#efece7; }
B    { font-weight:bold; }
I    { font-style:italic; }<%
if (!isNS) { %>
TH   { font-size: 90%; }<%
}

if (true == false) {
	%></style><% // Dummy - To get the right colors in the editor
} %>