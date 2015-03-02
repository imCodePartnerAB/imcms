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

#imcmsToolBar {
<%--
    /* Netscape 4, IE 4.x-5.0/Win and other lesser browsers will use this */ --%> position: absolute;
    z-index: 10000000 !important;
    top: 0;
    left: 0;
    width: 100% !important;
    border-bottom: 0 !important; /*1px solid red !important;*/
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
}

<%
if (isIE7plus) { %>
#imcmsToolBar,
#imcmsToolBarHidden {
    position: fixed;
    z-index: 20000000 !important;
}

<%
} else { %><%-- /* used by Opera 5+, Netscape6+/Mozilla, Konqueror, Safari, OmniWeb 4.5+, iCab, ICEbrowser */ --%>
body > #imcmsToolBar,
body > #imcmsToolBarHidden {
    position: fixed;
    z-index: 20000000 !important;
}

<%
}

if (isIE55plus && !isIE7plus) { %>
#imcmsToolBar,
#imcmsToolBarHidden {
    top: expression( ( ( ignoreMe = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop ) ) + 'px' );
}

<%
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
    font: normal 11px Tahoma, Verdana, Geneva, sans-serif !important;
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
    font: normal 10px Verdana, Geneva, sans-serif !important;
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
    font: normal 10px Verdana, Geneva, sans-serif !important;
    color: #a51414 <%--b65004--%> !important;
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
    font: normal 10px Verdana, Geneva, sans-serif !important;
    color: #a51414 <%--b65004--%> !important;
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
    font: normal 10px Verdana, Geneva, sans-serif !important;
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
.imcmsToolBarTabLink SPAN {
    /* removes dotted border onclick */
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
    font: 10px Verdana, Geneva, sans-serif !important;
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
<%= isGecko ? "-moz-border-radius: 4px !important;" : "" %> padding: 5px 5px 5px 25px !important;
    font: 11px Verdana, Geneva, sans-serif !important;
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
.imcmsToolTipIcon_ <%= extData[0] %> {
    background: #fff url(<%= cp + ICON_PATH + iconImg %>) 4px 5px no-repeat !important;
    padding-left: 27px !important;
}

<%
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

#adminPanelTd1_1 {
}

.adminPanelLogo {
    font: bold 11px Verdana, Geneva, sans-serif !important;
    color: #ddf !important;
    letter-spacing: -1px !important;
}

#adminPanelTd1_2 {
}

.adminPanelText,
.adminPanelText SPAN {
    font: 11px Verdana, Geneva, sans-serif !important;
    color: #fff !important;
}

#adminPanelTd1_2 .adminPanelText SPAN {
    white-space: nowrap !important;
}

#adminPanelTd1_3 {
}

.adminPanelTd2 {
    padding: 3px !important;
    height: 32px !important;
    vertical-align: top !important;
}

.adminPanelTd2 A:hover IMG {
<%
    if (isGecko) { %> <%= "-moz-opacity: 0.5 !important;" %><%
    } else if (isIE) { %> <%= "filter: progid:DXImageTransform.Microsoft.BasicImage(grayscale=0, xray=0, mirror=0, invert=0, opacity=0.5, rotation=0) !important;" %><%
    } else { %> <%= "opacity: 0.5 !important;" %><%
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
    font: 10px Verdana, Geneva, sans-serif !important;
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
    font: 10px Verdana, Geneva, sans-serif !important;
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
    cursor: pointer !important;
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
    font: bold 15px Arial, Tahoma, Verdana, sans-serif !important;
    color: #ee0 !important;
    text-decoration: none !important;
}

<%--

editor theme

--%>
.edit-mode {
}

.editor-frame {
    border: 1px dotted #ccc;
    cursor: pointer;
    padding: 0 10px 10px 0;
    position: absolute;
    left: -5px;
    top: -5px;
    width: 100%;
    height: 100%;
}

.editor-frame:hover {
    border: 1px solid #ff9600;
}

.editor-frame .header-ph {
    display: none;
    position: absolute;
    left: -1px;
    top: -1px;
    width: 100%;
}

.editor-frame:hover .header-ph {
    display: block;
}

.editor-frame .header-ph .header {
    position: absolute;
    left: 0;
    top: -20px;
    overflow: hidden;
}

.editor-frame .header-ph .header .title {
    background: #ff9600;
    color: #fff;
    line-height: 20px;
    font-size: 10px;
    text-transform: uppercase;
    float: left;
    padding: 0 10px;
    height: 20px;
}

.editor-form {
    background: #fff;
    display: none;
    position: fixed;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    z-index: 1002;
}

.editor-form, .editor-form *,.pop-up-form *,.pop-up-form  {
    font: normal 15px Arial;
}

.editor-form .header {
    background: #ff9600;
    overflow: hidden;
}

.editor-form .header .title {
    color: #fff;
    line-height: 30px;
    font-size: 15px;
    text-transform: uppercase;
    float: left;
    padding: 0 20px;
    height: 30px;
}

.editor-form .header .save-and-close {
    line-height: 20px;
    float: left;
    margin: 5px 5px 0 0;
    height: 20px;
}

.editor-form .content {
    overflow: auto;
}

.editor-form .content .negative {
    background: #dc0000 url("<%= cp %>/images/remove.png") no-repeat center;
    line-height: 20px;
    display: none;
    padding: 0;
    width: 20px;
    height: 20px;
}

.editor-form .content ul li .jqtree-element:hover .negative {
    display: block;
    float: right
}

.editor-form .footer {
    background: #f0f0f0;
    padding: 20px;
    /*height: 30px;*/
}

.editor-form .footer input {
    border: none;
    padding: 0 10px;
    line-height: 30px;
    float: left;
    width: 200px;
    height: 30px;
}

.editor-form .footer .browse {
    text-align: center;
    float: left;
    padding: 0;
    width: 30px;
}

.editor-form .footer .add {
    float: left;
}

.editor-form .footer .create-new {
    float: left;
    margin-left: 20px;
}

.positive {
    background: #649b00;
    border: none;
    color: #fff;
    cursor: pointer;
    line-height: 30px;
    display: inline-block;
    padding: 0 20px;
    height: 30px;
}

.positive:hover {
    background: #6eaf00;
}

.negative {
    background: #dc0000;
    border: none;
    color: #fff;
    cursor: pointer;
    line-height: 30px;
    display: inline-block;
    padding: 0 10px;
    height: 30px;
}

.negative:hover {
    background: #e60000;
}

.editor-base {
    position: relative;
    /*border: 1px dashed black;*/
}

.editor-menu-wrapper-adder, .editor-menu-wrapper-accepter {
    height: 34px;
    width: 34px;
}

.custom-combobox {
    position: relative;
    display: inline-block;
    float: left;
    margin-right: 45px;
}

.custom-combobox-toggle {
    position: absolute;
    top: 0;
    bottom: 0;
    margin-left: -1px;
    padding: 0;
}

.custom-combobox-input {
    margin: 0;
    padding: 5px 10px;
}

.editor-menu-item > a {
    display: block;
    float: left;
    line-height: 22px;
    margin: 0px 5px
}

.editor-menu-item-wrapper-button {
    width: 22px;
    height: 22px;
}

.editor-menu-form {

}

.editor-menu-form table {
    width: 100%;
    border-color: black;
}

.editor-menu-form table tbody tr:hover {
    background-color: #aabbc5;
}

.editor-menu-form table tbody .clicked {
    background-color: #aa77cc
}

.editor-form .content ul, .editor-form .content ul li {
    margin: 0;
    padding: 0;
}

.editor-form .content ul {
    background: #f0f0f0;
    padding-left: 30px !important;
}

.editor-form .content .jqtree-tree {
    padding: 0 !important;
}

.editor-form .content ul li {
    background: #fff;
    list-style-type: none;
}

.editor-form .content ul li .jqtree-element {
    overflow: hidden;
    line-height: 20px;
    width: 100%;
}

.editor-form .content ul li .jqtree-element span {
    line-height: 30px;
    float: left;
    padding: 0 20px;
}

.editor-form .content ul li .jqtree-element span:last-child {
    float: right;
    padding: 5px;
}

.editor-form ul.jqtree-tree *:nth-child(2n+1) .jqtree-element {
    /*background: #ffffe6;*/
}

ul.jqtree-tree li.jqtree-selected > .jqtree-element,
ul.jqtree-tree li.jqtree-selected > .jqtree-element:hover {
    background-color: #97BDD6 !important;
    background: -webkit-gradient(linear, left top, left bottom, from(#BEE0F5), to(#89AFCA)) !important;
    background: -moz-linear-gradient(top, #BEE0F5, #89AFCA) !important;
    background: -ms-linear-gradient(top, #BEE0F5, #89AFCA) !important;
    background: -o-linear-gradient(top, #BEE0F5, #89AFCA) !important;
    text-shadow: 0 1px 0 rgba(255, 255, 255, 0.7) !important;
}

.ui-dialog {
    z-index: 1003;
}

.editor-form ul.jqtree-tree .jqtree-element:hover {
    background: #ffff64;
}

ul.jqtree-tree span.jqtree-border {
    position: absolute !important;
    display: block !important;
    left: -2px !important;
    top: 0 !important;
    margin: 0 !important;
    border: none !important;
    box-sizing: content-box !important;
    background-color: #649b00 !important;
    height: 100% -ms-filter : "progid:DXImageTransform.Microsoft.Alpha(Opacity=50)"; /* IE 8 */
    filter: alpha(opacity=50); /* IE 5-7 */
    -moz-opacity: 0.5; /* Netscape */
    -khtml-opacity: 0.5; /* Safari 1.x */
    opacity: 0.5;
}

ul.jqtree-tree li.jqtree-ghost span.jqtree-circle {
    border: solid 2px #649b00;
    -webkit-border-radius: 100px;
    -moz-border-radius: 100px;
    border-radius: 100px;
    height: 8px;
    width: 8px;
    position: absolute;
    top: -4px;
    left: -6px;
}

ul.jqtree-tree li.jqtree-ghost span.jqtree-line {
    background-color: #649b00;
    height: 2px;
    padding: 0;
    position: absolute;
    top: -1px;
    left: 2px;
    width: 100%;
}

.clear {
    clear: both;
}

.ui-front {
    z-index: 1005;
}

ul.jqtree-tree ul.jqtree_common {
    margin-left: 0px !important;
}

.neutral {
    background: rgba(0, 0, 0, 0.2);
    border: none;
    color: #fff;
    cursor: pointer;
    line-height: 30px;
    display: inline-block;
    padding: 0 20px;
    height: 30px;
}

.neutral:hover {
    background: rgba(0, 0, 0, 0.1);
}

.editor-form .footer .neutral {
    background: #323232;
}

.editor-form .footer .neutral:hover {
    background: #484848;
}

.editor-form .header .save-and-close, .editor-form .header .close-without-saving {
    line-height: 20px;
    float: left;
    padding: 0 10px;
    margin: 5px 5px 0 0;
    height: 20px;
}

.pop-up-form {
    background: #fff;
    display: none;
    position: fixed;
    width: 600px;
    z-index: 1009;
}

.pop-up-form .title {
    background: #ff9600;
    color: #fff;
    line-height: 30px;
    text-transform: uppercase;
    padding: 0 20px;
}

.pop-up-form .content {
    padding: 20px;
}

.pop-up-form .content .buttons {
    padding: 20px 0 0;
    overflow: hidden;
}

.pop-up-form .content .buttons .positive {
    margin-right: 20px;
}

.pop-up-form .content .buttons .neutral {
    background: #323232;
}

.pop-up-form .content .buttons .neutral:hover {
    background: #484848;
}

.editor-form .content table {
    width: 100%;
}

.editor-form .content table tr td {
    line-height: 30px;
    padding: 0 20px;
}

.editor-form .content table tr:hover td {
    background: #ffff64;
}

.editor-form .content table tr td:last-child {
    line-height: 0;
    padding: 5px;
    width: 105px;
}

.editor-form .content table tr td .positive {
    line-height: 20px;
    text-align: center;
    visibility: hidden;
    display: block;
    float: left;
    margin-right: 5px;
    width: 80px;
    height: 20px;
}

.editor-form .content table tr:hover td .positive {
    visibility: visible;
}

.editor-form .content table tr td .negative {
    background-image: url(../../images/remove.png);
    background-position: center;
    background-repeat: no-repeat;
    line-height: 20px;
    visibility: hidden;
    display: block;
    float: left;
    padding: 0;
    width: 20px;
    height: 20px;
}

.editor-form .content table tr:hover td .negative {
    visibility: visible;
}

.modal {
    background: #000;
    opacity: 0.5;
    filter: alpha(opacity=50);
    display: none;
    position: fixed;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    z-index: 1008;
}






