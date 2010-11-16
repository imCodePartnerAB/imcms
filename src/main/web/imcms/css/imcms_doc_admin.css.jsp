<%@ page
    
    import="imcode.util.ui.BrowserCheck"
    
    contentType="text/css"
    pageEncoding="UTF-8"
    
%><%!

final static private String ICON_PATH = "/imcms/images/icons/" ;

%><%

String cp = request.getContextPath() ;

/* Check browser */

BrowserCheck browser = new BrowserCheck(request) ;

boolean isIE       = browser.isIE() ;
boolean isGecko    = browser.isGecko() ;
boolean isIE55plus = browser.isIE55plus() ;
boolean isIE7plus  = browser.isIE7plus() ;
boolean isIE9plus  = browser.isIE9plus() ;
boolean isIE8minus = isIE && !isIE9plus ;
boolean isWebKit   = browser.isWebKit() ;


%><%--

NEW adminPanel CSS

--%>
<%--

    NOTE! IE browsers need XHTML in the template for the position to be fixed!

--%>

.imcmsStatusIconImg {
    margin: 0 2px !important;
}


#imcmsToolBar * {
    position: relative;
}

#imcmsToolBar {<%--
    /* Netscape 4, IE 4.x-5.0/Win and other lesser browsers will use this */ --%>
    position: absolute;
    z-index: 10000000 !important;
    top: 0;
    left: 0;
    width: 100% !important;
    border-bottom: 0 !important;/*1px solid red !important;*/
    text-align: center !important;
}
#imcmsToolBarHidden {
    position: absolute;
    display: none;
    top: 0;
    left: 0;
    width: 100% !important;
    text-align: center !important;
    background-color: transparent !important;
}<%
if (isIE7plus) { %>
#imcmsToolBar,
#imcmsToolBarHidden {
    position: fixed;
    z-index: 20000000 !important;
}<%
} else { %><%-- /* used by Opera 5+, Netscape6+/Mozilla, Konqueror, Safari, OmniWeb 4.5+, iCab, ICEbrowser */ --%>
body > #imcmsToolBar,
body > #imcmsToolBarHidden {
    position: fixed;
    z-index: 20000000 !important;
}<%
}

if (isIE55plus && !isIE7plus) { %>
#imcmsToolBar,
#imcmsToolBarHidden {
    top: expression( ( ( ignoreMe = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop ) ) + 'px' );
}<%
} %>

#imcmsToolBarMain {
    position: relative;
    margin: 0 auto !important;
    padding: 0 !important;
    width: 983px !important;
    height: 53px !important;
    background: transparent url(<%= cp %>/imcms/images/adminpanel/bg_toolbar.<%= (isIE55plus && !isIE7plus) ? "gif" : "png" %>) top left no-repeat !important;
    text-align: left !important;
}

#imcmsToolBarHiddenMain {
    position: relative;
    margin: 0 auto !important;
    padding: 0 !important;
    width: 983px !important;
    text-align: left !important;
}

#imcmsToolBar #imcmsToolBarLeft {
    float: left !important;
}
#imcmsToolBar #imcmsToolBarRight {
    float: right !important;
}
#imcmsToolBar .imcmsToolBarDivider {
    display: inline !important;
    padding: 0 5px !important;
}
/*#imcmsToolBar .imcmsToolBarSub,
#imcmsToolBar .imcmsToolBarDrop {
    position: absolute;
    top: 0 !important;
    left: 0 !important;
    padding: 5px !important;
    background-color: #d66613 !important;
}
#imcmsToolBar .imcmsToolBarDrop button {
    display: block;
    margin: 3px 0 !important;
}*/
#imcmsToolBar .imcmsNone {
    display: none;
}
#imcmsToolBar .imcmsToolBarClear {
    clear: both !important;
}

#imcmsToolBar button {
    margin: 0 3px !important;
}
#imcmsToolBar button,
#imcmsToolBarHidden button,
#imcmsToolBar .imcmsToolBarDrop button {
    border: 1px solid #ccc !important;
    background-color: #dedede !important;
    color: #000 !important;
}

#imcmsToolBarLeftTable TD {
    text-align: left !important;
}

#imcmsToolBarLeftTable #imcmsToolBarLogoTd {
    padding: 3px 10px 0 8px !important;
}
#imcmsToolBarLeftTable #imcmsToolBarVersionTd {
    padding: 2px 10px 3px 8px !important;
    text-align: center !important;
    font: normal 11px Tahoma,Verdana,Geneva,sans-serif !important;
    color: #d66613 !important;
    text-shadow: 0 1px 0 #fff !important;
}


<%-- 
/* *******************************************************************************************
 *         Meta TD / Status / Languages                                                      *
 ******************************************************************************************* */
--%>

#imcmsToolBarLeftTable #imcmsToolBarMetaTd * {
    margin: 0 !important;
    padding: 0 !important;
}
#imcmsToolBarLeftTable #imcmsToolBarMetaTd {
    padding: 2px 10px 0 0 !important;
    vertical-align: top !important;
}
#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL {
    list-style-type: none !important;
    display: inline !important;
    float: left !important;
}
#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI {
    display: block !important;
    float: left !important;
    padding: 2px 15px 0 0 !important;
    font: normal 10px Verdana,Geneva,sans-serif !important;
    color: #b65004 !important;
    text-shadow: 0 1px 0 #fff !important;
}

#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI#imcmsMetaIdTd {
    padding: 2px 15px 0 10px !important;
}

#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI#statusIcon {
    padding: 0 20px 0 2px !important;
}
#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI#statusIcon .imcmsStatusIconImg {
    margin: 0 !important;
}
#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI LABEL {
    padding-right: 8px !important;
    font-weight: bold !important;
}

#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI.langIcon {
    padding: 2px 5px 0 0 !important;
}
#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI.langIcon IMG {
    margin: 0 5px 0 0 !important;
    border: 0 !important;
    vertical-align: middle !important;
}
#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI.langIcon A {
    color: #000 !important;
    text-decoration: none !important;
}
#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI.langIcon .langCode {
    padding: 0 10px 0 0 !important;
}

#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI.langIconDefault .langCode {
    color: #000 !important;
}

#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI.langIconCurrent .langCode {
    color: #000 !important;
    text-decoration: underline !important;
}

#imcmsToolBarLeftTable #imcmsToolBarMetaTd UL LI.langIconDisabled .langCode {
    color: #999 !important;
}


<%-- Top right links --%>

#imcmsToolBarRightTop {
    position: absolute !important;
    top: 3px !important;
    right: 12px !important;
}
#imcmsToolBarRightTop table td {
    padding-left: 5px !important;
}

.imcmsToolBarLink {
    display: block !important;
    margin: 0 3px !important;
    padding: 1px 3px !important;
    font: normal 10px Verdana,Geneva,sans-serif !important;
    color: #a51414<%--b65004--%> !important;
    text-decoration: none !important;
    white-space: nowrap !important;
    text-shadow: 0 1px 0 #fff !important;
    background-color: transparent !important;
    border: 1px solid transparent !important;
}
.imcmsToolBarLink:hover {
    background-color: #e0e0e0 !important;
    border: 1px inset !important;
    border-color: #7e7e7e #f7f7f7 #f7f7f7 #7e7e7e !important;
}

.imcmsToolBarIconLink {
    background-color: transparent !important;
    border: 1px solid transparent !important;
}
.imcmsToolBarIconLink:hover {
    background-color: #e0e0e0 !important;
    border: 1px inset !important;
    border-color: #7e7e7e #f7f7f7 #f7f7f7 #7e7e7e !important;
}

<%-- Plain Buttons --%>

.imcmsToolBarBtn {
    margin: 0 3px !important;
    padding: 1px 3px !important;
    font: normal 10px Verdana,Geneva,sans-serif !important;
    color: #a51414<%--b65004--%> !important;
    text-decoration: none !important;
    white-space: nowrap !important;
    text-shadow: 0 1px 0 #fff !important;
    background-color: transparent !important;
    border: 1px outset !important;
    border-color: #f7f7f7 #7e7e7e #7e7e7e #f7f7f7 !important;
}
.imcmsToolBarBtn:hover {
    background-color: #e0e0e0 !important;
    border: 1px inset !important;
    border-color: #7e7e7e #f7f7f7 #f7f7f7 #7e7e7e !important;
}


<%-- Buttons / Tabs --%>

#imcmsToolBarLeft .tabsDiv {
    position: absolute !important;
    top: 23px !important;
    left: 85px !important;
}
#imcmsToolBarRight .tabsDiv {
    position: absolute !important;
    top: 23px !important;
    right: 50px !important;
}

.imcmsToolBarTabLink,
.imcmsToolBarTabLink * {
    display: block !important;
    margin: 0 !important;
    padding: 0 !important;
    border: 0 !important;
    background: none !important;
    font-size: 0px !important;
    line-height: 0px !important;
}
.imcmsToolBarTabLink,
.imcmsToolBarTabLink SPAN,
.imcmsToolBarTabLink SPAN B {
    text-align: center !important;
    cursor: pointer !important;
    text-decoration: none !important;
}
.imcmsToolBarTabLink,
.imcmsToolBarTabLink SPAN {
    height: 22px !important;
    background: transparent url(<%= cp %>/imcms/images/adminpanel/bg_tabs.gif) 100% 0 scroll no-repeat !important;
}
.imcmsToolBarTabLink SPAN {
    background-position: 0 -32px !important;
}
.imcmsToolBarTabLink SPAN B {
    font: normal 10px Verdana,Geneva,sans-serif !important;
    color: #333 !important;
    padding: 5px 10px 0 10px !important;
    text-shadow: 0 1px 0 #eee !important;
    font-weight: normal !important;
    background: none !important;
    white-space: nowrap !important;
}
<%--
.imcmsToolBarTabLinkIcon SPAN B {
    padding: 2px 0 0 15px !important;
}
.imcmsToolBarTabLinkIcon SPAN B IMG {
    margin-right: 5px !important;
    vertical-align: middle !important;
}
--%>

<%-- Active tab --%>

.imcmsToolBarTabLink:hover,
.imcmsToolBarTabLink.imcmsToolBarTabActive {
    background-position: 100% -64px !important;
}
.imcmsToolBarTabLink:hover SPAN,
.imcmsToolBarTabLink.imcmsToolBarTabActive SPAN {
    background-position: 0 -96px !important;
    color: #000 !important;
}
.imcmsToolBarTabLink:hover SPAN B,
.imcmsToolBarTabLink.imcmsToolBarTabActive SPAN B {
    color: #333 !important;
    text-shadow: 0 1px 0 #fff !important;
}

.imcmsToolBarTabLink,
.imcmsToolBarTabLink SPAN {/* removes dotted border onclick */
    outline: none !important;
    -moz-outline-style: none !important;
}

<%-- Active tab with sub --%>

.imcmsToolBarTabLink.imcmsToolBarTabSubActive {
    background-position: 100% -128px !important;
}
.imcmsToolBarTabSubActive:hover SPAN,
.imcmsToolBarTabLink.imcmsToolBarTabSubActive SPAN {
    background-position: 0 -160px !important;
    color: #000 !important;
}

#imcmsToolBarHidden #imcmsToolBarShow {
    position: relative;
    float: right !important;
    width: 92px !important;
    height: 32px !important;
    background: transparent url(<%= cp %>/imcms/images/adminpanel/bg_toolbar_show.<%= (isIE55plus && !isIE7plus) ? "gif" : "png" %>) top left no-repeat !important;
    cursor: pointer !important;
}


<%-- Sub panels --%>

.imcmsToolBarSubPanelLeft {
    display: none;
    position: absolute !important;
    top: 45px;
    left: 86px;
}

.imcmsToolBarSubPanelRight {
    display: none;
    position: absolute !important;
    top: 45px;
    right: 44px;
}



table.imcmsToolBarSubTable {
    
}

table.imcmsToolBarSubTable td.imcmsToolBarSubTdLeft {
    background: transparent url(<%= cp %>/imcms/images/adminpanel/bg_toolbar_sub_left.<%=
    (isIE55plus && !isIE7plus) ? "gif" : "png" %>) bottom right no-repeat !important;
}
table.imcmsToolBarSubTable td.imcmsToolBarSubTdMid {
    padding: 8px 5px 17px 0 !important;
    background: transparent url(<%= cp %>/imcms/images/adminpanel/bg_toolbar_sub_mid.<%=
    (isIE55plus && !isIE7plus) ? "gif" : "png" %>) bottom left repeat-x !important;
}
table.imcmsToolBarSubTable td.imcmsToolBarSubTdRight {
    background: transparent url(<%= cp %>/imcms/images/adminpanel/bg_toolbar_sub_right.<%=
    (isIE55plus && !isIE7plus) ? "gif" : "png" %>) bottom left no-repeat !important;
}


table.imcmsToolBarSubTable td.imcmsToolBarSubWidthTdLeft {
    width: 7px !important;
}
table.imcmsToolBarSubTable td.imcmsToolBarSubWidthTdMid {
}
table.imcmsToolBarSubTable td.imcmsToolBarSubWidthTdRight {
    width: 12px !important;
}



<%-- 
/* *******************************************************************************************
 *         Form elements                                                                     *
 ******************************************************************************************* */
--%>

.imcmsSelectBox,
.imcmsTextField {
    font: 10px Verdana,Geneva,sans-serif !important;
    color: #000 !important;
    border: 1px solid #999;
}


<%-- 
/* *******************************************************************************************
 *         Misc                                                                              *
 ******************************************************************************************* */
--%>

.imcmsToolTip {
    cursor: help;
}
A.imcmsToolTip {
    cursor: pointer;
}
.imcmsHelper {
    text-decoration: none;
    border-bottom: 1px dashed #0b0;
    cursor: help;
}
.imcmsHelper IMG {
    border: 0 !important;
}
#imcmsToolTipPop {
    position: absolute !important;
    border: 1px solid #000 !important;
    <%= isGecko ? "-moz-border-radius: 4px !important;" : "" %>
    padding: 5px 5px 5px 25px !important;
    font: 11px Verdana,Geneva,sans-serif !important;
    color: #000 !important;
    text-shadow: 0 1px 0 #ccc !important;
    text-align: left !important;
    background: #fff url(<%= cp + ICON_PATH %>icon_info.gif) 4px 5px no-repeat !important;
    z-index: 20000010 !important;
    display: none;
}
<%
String[][] arrIconExt = {
    { "JPG", "icon_file_image.gif" },
    { "PNG", "icon_file_image.gif" },
    { "GIF", "icon_file_image.gif" },
    { "MP3", "icon_file_audio.gif" },
    { "AVI", "icon_file_video.gif" },
    { "MPG", "icon_file_video.gif" },
    { "EXT_LINK", "icon_link_ext.gif" },
    { "URL", "icon_link_ext.gif" },
    { "PDF", "" },
    { "DOC", "" },
    { "ZIP", "" },
    { "XLS", "" },
    { "PPT", "" },
    { "IMAGE", "" },
    { "SWF", "" },
    { "VIDEO", "" },
    { "AUDIO", "" },
    { "TXT", "" },
    { "RTF", "" },
    { "HTM", "" },
    { "OO-WRITE", "" },
    { "OO-CALC", "" },
    { "OO-DRAW", "" },
    { "OO-IMPRESS", "" },
    { "ODT", "icon_file_oo-write.gif" },
    { "OTT", "icon_file_oo-write.gif" },
    { "SXW", "icon_file_oo-write.gif" },
    { "STW", "icon_file_oo-write.gif" },
    { "ODS", "icon_file_oo-calc.gif" },
    { "OTS", "icon_file_oo-calc.gif" },
    { "SXC", "icon_file_oo-calc.gif" },
    { "STC", "icon_file_oo-calc.gif" },
    { "ODG", "icon_file_oo-draw.gif" },
    { "OTG", "icon_file_oo-draw.gif" },
    { "SXD", "icon_file_oo-draw.gif" },
    { "STD", "icon_file_oo-draw.gif" },
    { "ODP", "icon_file_oo-impress.gif" },
    { "OTP", "icon_file_oo-impress.gif" },
    { "SXI", "icon_file_oo-impress.gif" },
    { "STI", "icon_file_oo-impress.gif" }
} ;
for (String[] extData : arrIconExt) {
    String iconImg = extData[1] ;
    if ("".equals(iconImg)) {
        iconImg = "icon_file_" + extData[0].toLowerCase() + ".gif" ;
    } %>
.imcmsToolTipIcon_<%= extData[0] %> {
    background: #fff url(<%= cp + ICON_PATH + iconImg %>) 4px 5px no-repeat !important;
    padding-left: 27px !important;
}<%
} %>










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





<%-- Testing feature --%>
.ui-effects-transfer {
    border: 2px dotted #20568d !important;
}
<%----%>
<jsp:include page="imcms_vaadin.css.jsp" />







<%--

OLD adminPanel CSS - Soon removed!

--%>

<%-- adminMode --%>

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

<%-- changePage --%>

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


