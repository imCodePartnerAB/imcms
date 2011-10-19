<%@ page contentType="text/css" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
body {
    background: white;
    margin:0;
    padding:0;
}

.mainTable {
    margin:0;
}

.containerShadowRight, .containerShadowBottom {
    display: none;
}

#containerTop, #containerContent {
    background: white;
    padding-left:0;
    padding-right:0;
}

#containerTop {
    padding-top: 40px;
}

#archive_banner {
    display:none;
}

#backButton {
    display:none;
}

.tabs {
    background: #4E88AD url(${cp}/images/bg_topmenu.gif) bottom left repeat-x;
    height: 22px;
}

#languageSwitch {
    margin-top: -50px;
}

ul.tabs li {
margin: 0;
padding: 4px 13px 5px 13px;
font: 11px Verdana, Geneva, sans-serif;
color: white;
text-decoration: none;
background-color: transparent;
border:none;
border-right: 1px solid white;
}

ul.tabs li:hover {
    background: url(${cp}/images/bg_topmenu_hover.gif) top left repeat-x;
}

ul.tabs li.sel {
    border:none;
    border-right: 1px solid white;
    border-top:none;
    border-bottom:none;
    background: white url(${cp}/images/bg_topmenu_act.gif) top left repeat-x;
}

ul.tabs li.first {
    border-left:1px solid white;
    margin-left:10px;
}

ul.tabs li.sel a {
    color: black;
}

.imcmsFormBtn, .imcmsFormBtnSmall, .imcmsSpecialButton{
    background: #4E88AD url(${cp}/images/bg_btn.gif) 0 0 no-repeat;
    border: 1px solid #3E789D;
    font-family: Arial,Helvetica,sans-serif;
    color: white;
    cursor: pointer;
    padding: 0 2px;
}

.imcmsAdmHeading {
    background: #BED6F8 url(${cp}/images/gradient.png) top left repeat-x;
    border: 1px solid #BED6F8;
    padding: 3px 10px 4px 10px;
    color: black;
    font-weight:normal;
}