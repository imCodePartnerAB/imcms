<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="imcode.server.Imcms" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<html>
<head>
    <title><fmt:message key="templates/sv/no_page.html/1"/></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/css/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/css/404/imcms-page-error.css">

</head>
<body bgcolor="#FFFFFF">
<div class="imcms-page-error">
    <div class="imcms-page-error-head">
        <a href="http://www.imcms.net/" class="imcms-login__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="templates/sv/no_page.html/1"/></div>
    </div>
    <div class="imcms-page-error-body">
        <div class="imcms-field">
            <form class="imcms-page-error-body__form"
                  action="${contextPath}">
                <button type="Submit"
                        class="imcms-button imcms-button--neutral imcms-page-error-body__button"
                        onClick="top.location='${contextPath}';">
                    <fmt:message key="templates/Startpage"/>
                </button>
            </form>
            <form class="imcms-page-error-body__form"
                  action="${contextPath}/servlet/BackDoc">
                <button type="Submit"
                        class="imcms-button imcms-button--neutral imcms-page-error-body__button">
                    <fmt:message key="templates/Back"/>
                </button>
            </form>
        </div>
        <div class="imcms-field" style="margin-top: 30px">
            <div class="imcms-title">
                <fmt:message key="templates/sv/no_page.html/2">
                    <fmt:param value="<%= Imcms.getServices().getSystemData().getServerMasterAddress() %>"/>
                </fmt:message>
            </div>
        </div>
    </div>
</div>

</body>
</html>
