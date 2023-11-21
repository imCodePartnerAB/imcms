<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<fmt:setLocale value="${userLanguage}"/>
<fmt:setBundle basename="imcms" var="resource_property"/>

<html>
<head>
	<title><fmt:message key="templates/login/logged_out.html/1" bundle="${resource_property}"/></title>
	<link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms-logout.css">
</head>
<body>
<div class="imcms-info-page">
    <div class="imcms-info-head imcms-info-head__logout">
        <a href="https://www.imcms.net/" class="imcms-info__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="templates/login/logged_out.html/2" bundle="${resource_property}"/></div>
    </div>
    <div class="imcms-info-body imcms-info-body__logout">
        <div class="imcms-field">
            <a href="${contextPath}/" class="imcms-button imcms-button--neutral imcms-info-body__button"><fmt:message
                    key="templates/login/logged_out.html/2001" bundle="${resource_property}"/></a>
            <a href="${contextPath}/login"
               class="imcms-button imcms-button--neutral imcms-info-body__button"><fmt:message
                    key="templates/login/logged_out.html/2002" bundle="${resource_property}"/></a>
        </div>
        <div class="imcms-field">
            <div class="imcms-title imcms-title__logout"><fmt:message key="templates/login/logged_out.html/4" bundle="${resource_property}"/></div>
        </div>
    </div>
</div>
</body>
</html>
