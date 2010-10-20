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
.imcms_label,
.imcms_label:link,
.imcms_label:visited {
	font: 10px Verdana !important;
	color: #c00000 !important;
	text-decoration: none !important;
	background-color: #ffc !important;
}
.imcms_label:active,
.imcms_label:hover {
	font: 10px Verdana !important;
	color: #009 !important;
	text-decoration: underline !important;
	background-color: #ffc !important;
}

/* adminMode */

#adminPanelDiv {
	padding: 15px 0 10px 0 !important;
}
.adminPanelTable {
	border-right: 1px solid #000 !important;
	border-bottom: 1px solid #000 !important;
	background-color: #f5f5f7 !important;
}
.adminPanelTd1 {
	padding: 2px !important;
	background-color: #20568D !important;
}
#adminPanelTd1_1 {}
.adminPanelLogo {
	font: bold 11px Verdana,Geneva,sans-serif !important;
	color: #ddf !important;
	letter-spacing: -1px !important;
}
#adminPanelTd1_2 {}
.adminPanelText,
.adminPanelText SPAN {
	font: 11px Verdana,Geneva,sans-serif !important;
	color: #fff !important;
}
#adminPanelTd1_2 .adminPanelText SPAN {
	white-space: nowrap !important;
}
#adminPanelTd1_3 {}
.adminPanelTd2 {
	padding: 3px !important;
	height: 32px !important;
	vertical-align: top !important;
}

.adminPanelTd2 A:hover IMG {<%
	if (isGecko) { %>
	<%= "-moz-opacity: 0.5 !important;" %><%
	} else if (isIE) { %>
	<%= "filter: progid:DXImageTransform.Microsoft.BasicImage(grayscale=0, xray=0, mirror=0, invert=0, opacity=0.5, rotation=0) !important;" %><%
	} else { %>
	<%= "opacity: 0.5 !important;" %><%
	} %>
}
.adminPanelTable B {
	font-weight: bold !important;
}

/* changePage */

#changePageDiv {
	padding: 0 0 10px 0 !important;
}
#changePageTable {
	border-right: 1px solid #000 !important;
	border-bottom: 1px solid #000 !important;
	background-color: #f5f5f7 !important;
}
#changePageTdTop {
	padding: 2px !important;
	background-color: #20568d !important;
}
#changePageTd1 {
	font: 10px Verdana,Geneva,sans-serif !important;
	color: #fff !important;
	padding-left: 5px !important;
}
#changePageTd1 SPAN {
	white-space: nowrap !important;
}
#changePageTd2 {
	padding-left: 10px !important;
}
.changePageTdBottom TD {
	padding-top: 3px !important;
}
.changePageSelect {
	font: 10px Verdana,Geneva,sans-serif !important;
	color: #000 !important;
}
.changePageHeading {
	font: 10px Verdana,Geneva,sans-serif !important;
	color: #fff !important;
}
.changePageButton {
	background-color: #e2e2e4 !important;
	font: 10px Tahoma, Arial, sans-serif !important;
	color: #000 !important;
	padding: 0 4px !important;
	border-width: 1px !important;
	border-style: outset !important;
	border-color: #ccc #666 #666 #ccc !important;
	cursor:pointer !important;
}
#changePageDiv .imcmsFormBtnSmall {
	background-color: #20568d !important;
	color: #fff !important;
	font: 10px Tahoma, Arial, sans-serif !important;
	border: 1px outset #668db6 !important;
	border-color: #668db6 #000 #000 #668db6 !important;
	padding: 0 2px !important;
	cursor: pointer !important;
}
#changePageTable B {
	font-weight: bold !important;
}
A.imLinkHelp:link,
A.imLinkHelp:visited,
A.imLinkHelp:active,
A.imLinkHelp:hover {
	font: bold 15px Arial, Tahoma,Verdana,sans-serif !important;
	color: #ee0 !important;
	text-decoration:none !important;
}

<%-- Testing feature --%>
.ui-effects-transfer {
	border: 2px dotted #20568d !important;
}
