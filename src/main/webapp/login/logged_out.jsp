<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<html>
<head>
    <title><fmt:message key="templates/login/logged_out.html/1"/></title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/css_new/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/css_new/imcms-logout.css">
</head>
<body>
<div class="imcms-info-page">
    <div class="imcms-info-head imcms-info-head__logout">
        <a href="https://www.imcms.net/" class="imcms-info__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="templates/login/logged_out.html/2"/></div>
    </div>
    <div class="imcms-info-body imcms-info-body__logout">
        <div class="imcms-field">
            <a class="imcms-button imcms-button--neutral imcms-info-body__button" href="${contextPath}/"><fmt:message
                    key="templates/login/logged_out.html/2001"/></a>
            <button type="button" class="imcms-button imcms-button--neutral imcms-info-body__button"
                    onClick="top.location='${contextPath}/login';"><fmt:message
                    key="templates/login/logged_out.html/2002"/></button>
        </div>
        <div class="imcms-field">
            <div class="imcms-title imcms-title__logout"><fmt:message key="templates/login/logged_out.html/4"/></div>
        </div>
    </div>
</div>
</body>
</html>
