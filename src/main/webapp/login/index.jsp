<%@ page import="com.imcode.imcms.servlet.VerifyUser" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="cp" value="${pageContext.request.contextPath}"/>

<html>
<head>
    <title><fmt:message key="templates/login/index.html/1"/></title>
    <link rel="stylesheet" type="text/css" href="${cp}/imcms/css/imcms_admin.css">
    <link rel="stylesheet" type="text/css" href="${cp}/css_new/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${cp}/css_new/admin/imcms-login-page.css">
</head>
<body>
<div id="imcmsLogin" class="imcms-login">
    <div class="imcms-login-head imcms-login__head">
        <a href="http://www.imcms.net/" class="imcms-login__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="templates/login/index.html/2"/></div>
    </div>
    <div class="imcms-login-body imcms-login__body">
        <div class="imcms-field">
            <button type="button"
                    class="imcms-button imcms-button--neutral imcms-login-body__button"
                    onClick="top.location='${cp}/servlet/StartDoc';">
                <fmt:message key="templates/login/index.html/2001"/>
            </button>
        </div>
        <c:if test="${requestScope['error'] ne null}">
            <div class="imcms-field">
                <div class="imcms-error-msg imcms-login__error-msg">
                        ${requestScope['error'].toLocalizedString(pageContext.request)}
                </div>
            </div>
        </c:if>
        <div class="imcms-field">
            <div class="imcms-title">
                <fmt:message key="templates/login/index.html/4"/>
                <fmt:message key="templates/login/index.html/1001"/>
            </div>
        </div>
        <form action="${cp}/servlet/VerifyUser" id="loginForm" method="post">
            <c:set var="nextMetaParamName"
                   value="<%=VerifyUser.REQUEST_PARAMETER__NEXT_META%>"/>
            <c:set var="nextMetaParamValue" value="${requestScope[nextMetaParamName]}"/>

            <c:set var="nextUrlParamName"
                   value="<%=VerifyUser.REQUEST_PARAMETER__NEXT_URL%>"/>
            <c:set var="nextUrlParamValue" value="${requestScope[nextUrlParamName]}"/>

            <c:if test="${nextMetaParamValue ne null}">
                <input type="hidden" name="${nextMetaParamName}"
                       value="${fn:escapeXml(nextMetaParamValue)}">
            </c:if>

            <c:if test="${nextMetaParamValue eq null and nextUrlParamValue ne null}">
                <input type="hidden" name="${nextUrlParamName}"
                       value="${fn:escapeXml(nextUrlParamValue)}">
            </c:if>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="<fmt:message key="templates/login/index.html/5"/>"
                           class="imcms-label imcms-text-box__label">
                        <fmt:message key="templates/login/index.html/5"/>
                    </label>
                    <input id="<fmt:message key="templates/login/index.html/5"/>"
                           name="<%=VerifyUser.REQUEST_PARAMETER__USERNAME%>"
                           type="text"
                           class="imcms-input imcms-text-box__input">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="<fmt:message key="templates/login/index.html/6"/>"
                           class="imcms-label imcms-text-box__label">
                        <fmt:message key="templates/login/index.html/6"/>
                    </label>
                    <input id="<fmt:message key="templates/login/index.html/6"/>"
                           name="<%=VerifyUser.REQUEST_PARAMETER__PASSWORD%>"
                           type="password"
                           class="imcms-input imcms-text-box__input">
                </div>
            </div>
        </form>
        <div class="imcms-field">
            <button type="button"
                    class="imcms-button imcms-button--neutral imcms-login-body__button"
                    onClick="top.location='${cp}/servlet/PasswordReset';">
                <fmt:message key="templates/login/index.html/2002"/>
            </button>
        </div>
    </div>
    <div class="imcms-login-footer imcms-login__footer">
        <button type="submit"
                form="loginForm"
                class="imcms-button imcms-button--positive imcms-login-footer__button">
            <fmt:message key="templates/login/index.html/2005"/>
        </button>
        <button type="submit"
                form="loginForm"
                name="<%= VerifyUser.REQUEST_PARAMETER__EDIT_USER %>"
                class="imcms-button imcms-button--save imcms-login-footer__button">
            <fmt:message key="templates/login/index.html/2006"/>
        </button>
    </div>
</div>


</body>
</html>
