<%@ page
	
	import="imcode.server.Imcms,
	        java.io.BufferedReader,
	        java.io.InputStream,
	        java.io.InputStreamReader,
	        java.net.URL,
	        java.net.URLConnection,
	        org.apache.commons.lang.StringUtils,
	        org.apache.commons.lang.StringUtils,
	        org.apache.oro.text.perl.Perl5Util,
	        org.apache.oro.text.perl.Perl5Util"
	
	contentType="text/css"
	pageEncoding="UTF-8"
	
%><%

/*

	Parsed in to all templates - If the user has an admin panel.

*/

boolean isTextMode = ("true".equals(StringUtils.defaultString(request.getParameter("textMode")))) ;

Perl5Util re = new Perl5Util() ;

/* Check browser */

String uAgent   = StringUtils.defaultString(request.getHeader("USER-AGENT")) ;
boolean isIE    = re.match("/(MSIE \\d)/i", uAgent) ;
boolean isGecko = re.match("/Gecko/i", uAgent) ;
//boolean isWebKit = re.match("/webkit/i", uAgent) ;

String BORDER_COLOR_NORMAL = "#668db6 #000 #000 #668db6" ;

if (isGecko) {
	BORDER_COLOR_NORMAL = "#466d96 #333 #333 #466d96" ;
}

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

<%-- Inline Text Edit - [Save], [Abort] --%>

.imcmsFormBtnSmall {
	background-color: #20568d;
	color: #fff;
	font: 10px Tahoma, Arial, sans-serif;
	border: <%= isGecko ? 1 : 2 %>px outset #668db6;
	border-color: <%= BORDER_COLOR_NORMAL %>;
	cursor:pointer;
	padding: <%= isGecko ? 1 : 0 %>px 2px;
}

#imcmsInlineEditMessage_ok {
	display: none;
	margin: 5px 0 !important;
	padding: 10px !important;
	text-align: center !important;
	font: bold italic 14px Tahoma, Arial, sans-serif !important;<%----%>
	color: #0b0;
	background-color: #ffc !important;
	border: 1px solid #000 !important;
}

#imcmsInlineEditMessage_error {
	display: none;
	margin: 5px 0 !important;
	padding: 10px !important;
	text-align: center !important;
	font: bold italic 14px Tahoma, Arial, sans-serif !important;<%----%>
	color: #f00;
	background-color: #ffc !important;
	border: 1px solid #000 !important;
}

.imcmsFormBtnDiv {
	margin-top: -2px !important;
	padding: 3px;
	background-color: #f0f0f2;
	border: 1px solid #ccc !important;
	border-top: 0 !important;
}

.imcmsFormBtnDiv .textMode {
	float: left;
	padding: 2px 0 0 0;
	white-space: pre;
	font: bold normal 11px 'Courier New', Courier, monospace !important;
	color: #999 !important;
}


<%-- adminMode --%>

#adminPanelDiv {
	padding: 15px 0 10px 0 !important;
}
.adminPanelTable {
	border-width: 1px 2px 2px 1px !important;
	border-style: solid !important;
	border-color: #ccc #000 #000 #ccc !important;
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
#adminPanelTd1_3 {
	text-align: right !important;
}
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

<%-- changePage --%>

#changePageDiv {
	padding: 0 0 10px 0 !important;
}
#changePageTable {
	border-width: 1px 2px 2px 1px !important;
	border-style: solid !important;
	border-color: #ccc #000 #000 #ccc !important;
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

<%--
imCMS version of jQuery UI CSS:
--%>

<jsp:include page="imcms_jquery-ui.css.jsp" />

<%-- Testing feature --%>
.ui-effects-transfer {
	border: 2px dotted #20568d !important;
}


<%--!

// Make new imCMS version of jQuery UI CSS: https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/themes/redmond/jquery-ui.css


private final static int CONNECTION_TIMEOUT_MILLIS = 3000 ;

public static String getURLcontent( String urlString, String encoding ) {
		try {
				URL url = new URL(urlString) ;
				URLConnection con = url.openConnection() ;
				con.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
				con.connect() ;
				InputStream is = (InputStream) con.getContent() ;
				InputStreamReader isr = new InputStreamReader(is, encoding) ;
				BufferedReader br = new BufferedReader(isr) ;
				String line = br.readLine() ;
				StringBuffer retVal = new StringBuffer();
				while (line != null) {
						retVal.append( line ).append( "\n" ) ;
						line = br.readLine() ;
				}
				br.close() ;
				return retVal.toString();
		} catch (Exception ex) {
				return null ;
		}
}

%><%

if (true) {
	try {
		String jQueryUiCss = getURLcontent("https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/themes/redmond/jquery-ui.css", Imcms.UTF_8_ENCODING).replace("@VERSION", "1.8.16") ;
		
		out.print("\n\n\n\n" + jQueryUiCss.replaceAll("\\n.ui-", "\n.imcmsAdmin.ui-")) ;
		
		out.print("\n\n\n\n" + jQueryUiCss.replaceAll("\\n.ui-", "\n.imcmsAdmin .ui-")) ;
		
	} catch (Exception e) {
		out.print("/* ERROR: " + e.getMessage() + " */");
	}
}

--%>