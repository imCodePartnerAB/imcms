<%@ page
	
	import="org.apache.oro.text.perl.Perl5Util,
	        org.apache.commons.lang.StringUtils"
	
	contentType="text/css"
	pageEncoding="UTF-8"
	
%><%

Perl5Util re = new Perl5Util() ;

/* Check browser */

String uAgent   = StringUtils.defaultString(request.getHeader("USER-AGENT")) ;
boolean isIE    = re.match("/(MSIE \\d)/i", uAgent) ;
boolean isGecko = re.match("/Gecko/i", uAgent) ;

%>
.imcms_label, .imcms_label:link, .imcms_label:visited { font: 10px Verdana; color:#c00000; text-decoration:none; background-color:#ffc }
.imcms_label:active, .imcms_label:hover { font: 10px Verdana; color:#009; text-decoration:underline; background-color:#ffc }

/* adminMode */

#adminPanelDiv    { padding: 15px 0 10px 0; }
.adminPanelTable  { border-right: 1px solid #000; border-bottom: 1px solid #000; background-color: #f5f5f7; }
.adminPanelTd1    { padding: 2px; background-color: #20568D; }
#adminPanelTd1_1  { }
.adminPanelLogo   { font: bold 11px Verdana,Geneva,sans-serif; color: #ddf; letter-spacing: -1px; }
#adminPanelTd1_2  {  }
.adminPanelText,
.adminPanelText SPAN { font: 11px Verdana,Geneva,sans-serif; color: #fff; }
#adminPanelTd1_3  {  }
.adminPanelTd2    { padding: 3px; height: 32px; vertical-align: top; }

.adminPanelTd2 A:hover IMG {<%
	if (isGecko) { %>
	<%= "-moz-opacity: 0.5;" %><%
	} else if (isIE) { %>
	<%= "filter: progid:DXImageTransform.Microsoft.BasicImage(grayscale=0, xray=0, mirror=0, invert=0, opacity=0.5, rotation=0);" %><%
	} else { %>
	<%= "opacity: 0.5;" %><%
	} %>
}
.adminPanelTable B { font-weight: bold; }

/* changePage */

#changePageDiv { padding: 0 0 10px 0; }
#changePageTable { border-right: 1px solid #000; border-bottom: 1px solid #000; background-color:#f5f5f7; }
#changePageTdTop { padding: 2px; background-color:#20568D; }
#changePageTd1, #changePageTd1 SPAN { font: 10px Verdana,Geneva,sans-serif; color:#fff; }
#changePageTd2 {  }
.changePageTdBottom TD { padding-top: 3px; }
.changePageSelect { font: 10px Verdana,Geneva,sans-serif; color:#000; }
.changePageHeading { font: bold 11px Verdana,Geneva,sans-serif; color:#cce; }
.changePageButton {
	background-color:#e2e2e4;
	font: 10px Tahoma, Arial, sans-serif;
	color:#000;
	padding: 0 4px;
	border-width: 1px;
	border-style: outset;
	border-color: #ccc #666 #666 #ccc;
	cursor:pointer;
}
INPUT.imcmsFormBtnSmall {
	background-color: #20568D;
	color: #ffffff;
	font: 10px Tahoma, Arial, sans-serif;
	border: 2px outset #668DB6;
	border-color: #668DB6 #000 #000 #668DB6;
	padding: 0 2px;
	cursor:pointer;
}
#changePageTable B { font-weight: bold; }
A.imLinkHelp:link, A.imLinkHelp:visited, A.imLinkHelp:active, A.imLinkHelp:hover {
	font: bold 15px Arial, Tahoma,Verdana,sans-serif; color:#ee0; text-decoration:none;
}



.ui-effects-transfer {
	border: 2px dotted gray;
}