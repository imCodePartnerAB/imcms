<%@ page contentType="text/css" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cp" value="${cp}"/>
.imageArchive {
	width: 905px;
	height: 600px;
}

.minH30 {
	min-height: 30px;
	height: auto !important;
	height: 30px;
}

/* slightly enhanced, universal clearfix hack */
.clearfix:after {
	visibility: hidden;
	display: block;
	font-size: 0;
	content: " ";
	clear: both;
	height: 0;
}

.clearfix {
	display: inline-block;
}

/* start commented backslash hack \*/
* html .clearfix {
	height: 1%;
}

.clearfix {
	display: block;
}

/* close commented backslash hack */

#lightbox {
	position: fixed;
	top: 50%;
	left: 50%;
	width: 500px;
	background: #fff;
	z-index: 1001;
	display: none;
}

#lightbox-shadow, #overlay-shadow {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: #000;
	filter: alpha(opacity=75);
	-moz-opacity: 0.75;
	-khtml-opacity: 0.75;
	opacity: 0.75;
	z-index: 1000;
	display: none;
}

#lightbox-close {
	position: fixed;
	top: 50%;
	left: 50%;
	width: 35px;
	height: 35px;
	z-index: 1003;
	display: none;
	color: white;
	background: url('${cp}/images/close.png') top left no-repeat;
	cursor: pointer;
}

#exportOverlay {
	background-color: #fff;
	border: 1px solid black;
	width: 370px;
	position: fixed;
	top: 50%;
	left: 50%;
	z-index: 1001;
	display: none;
}

#exportOverlay .imcmsAdmHeading {
	padding-left: 10px;
}

.fixedWidth {
	width: 80px;
}

.fixedWidthInput {
	width: 100px;
	margin-left: 5px;
}

#exportImage {
	padding: 50px 45px 30px 45px;
}

.exportBtns {
	text-align: right;
	margin-top: 25px;
}

.imcmsFormBtn, .imcmsFormBtnSmall, .imcmsSpecialButton {
	background: #4E88AD url('${cp}/images/bg_btn.gif') 0 0 no-repeat;
	border: 1px solid #3E789D;
	font-family: Arial, Helvetica, sans-serif;
	color: white;
	cursor: pointer;
	padding: 0 2px;
}

.left {
	float: left;
}

.imcmsAdmHeading {
	background: #BED6F8 url('${cp}/images/gradient.png') top left repeat-x;
	border: 1px solid #BED6F8;
	padding: 3px 10px 4px 10px;
	color: black;
	font-weight: normal;
}

.imcmsAdmText, .imcmsAdmTable td, input, select, textarea, .imcmsAdmForm {
	font-size: 11px;
	font-family: Tahoma, Arial, sans-serif;
	color: black;
}

.adminLinksTable {
	border: 1px solid #000;
	border-width: 0 1px 1px 0;
}

#adminLinksDiv {
	margin: 5px 0 0 0 !important;
	padding: 0 !important;
	float: right;
}

#adminPanelDiv {
	margin: 5px 0 5px 5px !important;
	padding: 0 !important;
	float: left;
}

.imcmsAdmBgHead {
	background-color: #20568D;
	color: #fff;
}

.imcmsAdmBgCont {
	background-color: #f5f5f7;
	color: #000;
}

.imcmsFormBtnPanel {
	font: 9px Verdana, Geneva, sans-serif;
	color: #000000;
	background-color: #e7e7e7;
	cursor: pointer;
	height: 20px;
	border: 1px outset #000;
	border-color: #fff #000 #000 #fff;
	text-align: center;
}

.imcmsFormBtnPanelActive {
	font: 9px Verdana, Geneva, sans-serif;
	color: #000;
	background-color: #e7e7e7;
	cursor: pointer;
	height: 20px;
	border: 1px inset #000;
	border-color: #000 #fff #fff #000;
	text-align: center;
}

.imcms_label,
A.imcms_label:link,
A.imcms_label:visited {
	font: 10px Verdana !important;
	color: #c00000 !important;
	text-decoration: none !important;
	background-color: #ffc !important;
	text-transform: none !important;
	letter-spacing: 0 !important;
}

A.imcms_label:active,
A.imcms_label:hover {
	font: 10px Verdana !important;
	color: #009 !important;
	text-decoration: underline !important;
	background-color: #ffc !important;
	text-transform: none !important;
	letter-spacing: 0 !important;
}

/* *******************************************************************************************
*         LeftMenu                                                                          *
******************************************************************************************* */
#leftmenu {
<%--margin-right: 50px;--%>
}

#leftmenu DIV {
	margin: 0;
	padding: 0;
}

#leftmenu A {
	display: block;
	width: 172px;
	margin: 0;
	padding: 0;
	border: 1px solid #a6c3d6;
	border-width: 0 1px 1px 1px;
	font: 11px Verdana, Geneva, sans-serif;
	color: #333;
	text-decoration: none;
}

#leftmenu A SPAN {
	display: block;
	margin: 0;
	padding: 5px 14px;
}

#leftmenu A.first {
	border-top: 1px solid #a6c3d6;
}

#leftmenu A.lev1 SPAN {
	padding: 5px 14px;
}

#leftmenu A.lev2 SPAN {
	padding: 5px 14px 5px 26px;
}

#leftmenu A.lev3 SPAN {
	padding: 5px 14px 5px 42px;
}

#leftmenu A.lev4 SPAN {
	padding: 5px 14px 5px 58px;
}

#leftmenu A:active,
#leftmenu A:hover {
	background: #ecf5f9;
	color: #333;
	text-decoration: underline;
}

#leftmenu A.act_page {
	width: 173px;
	border-right: 0;
	background: #ecf5f9 url('${cp}/images/bg_leftmenu_act.gif') top right repeat-y;
	color: #333;
}

#leftmenu A.disabled,
#leftmenu A.disabled:active
#leftmenu A.disabled:hover {
	color: #999;
	background-color: inherit;
	text-decoration: none;
	cursor: default;
}

#leftmenu A.act_tree_lev1 SPAN {
	background: transparent url('${cp}/images/menu_black_down.gif') 5px 9px no-repeat;
}

#leftmenu A.act_tree_lev2 SPAN {
	background: transparent url('${cp}/images/menu_black_down.gif') 17px 9px no-repeat;
}

#leftmenu A.act_tree_lev3 SPAN {
	background: transparent url('${cp}/images/menu_black_down.gif') 33px 9px no-repeat;
}

#leftmenu A.act_tree_lev4 SPAN {
	background: transparent url('${cp}/images/menu_black_down.gif') 49px 9px no-repeat;
}

#leftmenu A.inact_tree_lev1 SPAN {
	background: transparent url('${cp}/images/menu_black_right.gif') 4px 10px no-repeat;
}

#leftmenu A.inact_tree_lev2 SPAN {
	background: transparent url('${cp}/images/menu_black_right.gif') 16px 10px no-repeat;
}

#leftmenu A.inact_tree_lev3 SPAN {
	background: transparent url('${cp}/images/menu_black_right.gif') 32px 10px no-repeat;
}

#leftmenu A.inact_tree_lev4 SPAN {
	background: transparent url('${cp}/images/menu_black_right.gif') 48px 10px no-repeat;
}

#leftmenu DIV.leftMenuHeading {
	display: block;
	width: 172px;
	margin: 0;
	padding: 0;
	border: 1px solid #a6c3d6;
	border-width: 0 1px 1px 1px;
	font: bold 11px Verdana, Geneva, sans-serif;
	color: #333;
	background-color: #c9e6f3;
}

#leftmenu A.leftMenuHeadingBg {
	border: 1px solid #a6c3d6;
	background-color: #c9e6f3 !important;
	color: #333 !important;
}

#leftmenu DIV.leftMenuHeadingSpaceBefore {
	margin-top: 35px;
	border-width: 1px;
}

#leftmenu DIV.leftMenuHeadingSpaceAfter {
	margin-bottom: 35px;
}

#leftmenu DIV.leftMenuHeadingFirst {
	border-width: 1px;
}

#leftmenu DIV.leftMenuHeading SPAN {
	display: block;
	margin: 0;
	padding: 5px 14px;
}