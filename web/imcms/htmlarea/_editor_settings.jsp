<%@ page
	
	import="com.imcode.imcms.api.*,
	        imcode.server.ImcmsServices,
	        imcode.server.Imcms,
	        org.apache.oro.text.perl.Perl5Util"
	
%><%

String EDITOR_URL   = request.getContextPath() + "/imcms/htmlarea/" ;
String SERVLET_PATH = request.getContextPath() + "/servlet/" ;

ContentManagementSystem imcmsSystem = (ContentManagementSystem) request.getAttribute(RequestConstants.SYSTEM) ;
DocumentService documentService     = imcmsSystem.getDocumentService() ;
ImcmsServices imcref                = Imcms.getServices() ;
UserService userService             = imcmsSystem.getUserService() ;
User user                           = imcmsSystem.getCurrentUser() ;

%><%!

/* *******************************************************************************************
 *         LANGUAGE                                                                          *
 ******************************************************************************************* */

private boolean isLangSwe = true ;
private boolean isLangEng = !isLangSwe ;

%><%

isLangSwe = user.getLanguage().getIsoCode639_2().equals("swe") ;
isLangEng = !isLangSwe ;

/* *******************************************************************************************
 *         BROWSER SNIFFER  @returns "is[BrowtypeName]" (boolean) / dBrowserVer (double)     *
 ******************************************************************************************* */

boolean isIE       = true ;
boolean isNS       = false ;
boolean isNS6      = false ;
boolean isMoz      = false ;
boolean isWin      = true ;
boolean isMac      = false ;
boolean isSafari   = false ;
double dBrowserVer = 0.0 ;

boolean isHtmlAreaSupported = false ;



Perl5Util re  = new Perl5Util() ;
String uAgent = request.getHeader("USER-AGENT") ;

isIE     = re.match("/(MSIE 4|MSIE 5|MSIE 5\\.5|MSIE 6|MSIE 7)/i", uAgent) ;
isNS     = (re.match("/Mozilla/i", uAgent) && !re.match("/Gecko/i", uAgent) && !re.match("/MSIE/i", uAgent)) ? true : false ;
isNS6    = (re.match("/Mozilla/i", uAgent) && re.match("/Gecko/i", uAgent) && re.match("/Netscape/i", uAgent)) ? true : false ;
isMoz    = (re.match("/Gecko/i", uAgent) && !isNS6) ? true : false ;
isWin    = re.match("/Win/i", uAgent) ;
isMac    = re.match("/Mac/i", uAgent) ;
isSafari = (re.match("/Safari/i", uAgent) && isMac) ? true : false ;

String sTempVer    = "" ;

try {
	if (isIE) {
		if (re.match("/MSIE (\\d\\.\\d+)/i",uAgent)) dBrowserVer = Double.parseDouble(re.group(1)) ;
	} else if (isMoz) {
		if (re.match("/rv:([\\d\\.]+)/i",uAgent)) sTempVer = re.group(1) ;
		sTempVer = sTempVer.replaceFirst("\\.", ",") ;
		sTempVer = sTempVer.replaceAll("\\.", "") ;
		sTempVer = sTempVer.replaceAll(",", "\\.") ;
		dBrowserVer = Double.parseDouble(sTempVer) ;
	} else if (isNS6) {
		if (re.match("/Netscape\\/(\\d\\.\\d+)/i",uAgent)) dBrowserVer = Double.parseDouble(re.group(1)) ;
	} else if (isNS) {
		if (re.match("/Mozilla\\/(\\d\\.\\d+)/i",uAgent)) dBrowserVer = Double.parseDouble(re.group(1)) ;
	}
} catch(Exception ex) {}


isHtmlAreaSupported = (isMoz && dBrowserVer >= 1.3) || (isWin && isIE && dBrowserVer >= 5.5) || (isNS6 && dBrowserVer >= 7.1) ;

%><%!

/* *******************************************************************************************
 *         COLORS                                                                            *
 ******************************************************************************************* */

final String imCMS_blue            = "#20568D" ;
final String imCMS_offwhite        = "#f5f5f7" ;
final String imCMS_textbtn         = "#ffffff" ;
final String imCMS_textNormal      = "#000000" ;
final String imCMS_borderBlueL     = "#668DB6" ;
final String imCMS_borderBlueD     = "#000000" ;
final String imCMS_borderOffwhiteL = "#ffffff" ;
final String imCMS_borderOffwhiteD = "#cccccc" ;
final String imCMS_btnBg           = "#e5e5e5" ;
final String imCMS_btnD            = "#bbbbbb" ;
final String imCMS_btnL            = "#ffffff" ;

final String htmlareaBg            = "#e5e5e5" ;
final String ButtonFace            = imCMS_btnBg ;
final String ButtonShadow          = imCMS_btnD ;
final String ButtonHighlight       = imCMS_btnL ;
final String ButtonText            = imCMS_textbtn ;
final String editorGuiText         = imCMS_textNormal ;
final String linkStyle             = "color:#000099; text-decoration:none;" ;
final String Highlight             = "#eeee33" ;
final String HighlightText         = "#000000" ;

%>