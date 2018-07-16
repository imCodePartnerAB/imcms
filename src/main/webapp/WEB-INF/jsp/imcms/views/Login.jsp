${"<!--"}
<%@ page trimDirectiveWhitespaces="true" %>
${"-->"}
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
    <link rel="stylesheet" type="text/css" href="${cp}/css/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${cp}/css/imcms-login-page.css">
    <script>
        <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
    </script>
    <script src="${cp}/js/imcms/imcms_main.js" data-name="imcms"
            data-main="${cp}/js/imcms/starters/imcms_login_start.js"></script>
</head>
<body>
<div class="imcms-info-page">
    <div class="imcms-info-head imcms-info-head__login">
        <a href="https://www.imcms.net/" class="imcms-info__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="templates/login/index.html/2"/></div>
    </div>
    <div class="imcms-info-body imcms-info-body__login">
        <div id="login-page-buttons" class="imcms-field imcms-info-body__login-buttons">
            <a href="${cp}/" class="imcms-button imcms-button--neutral imcms-info-body__button"><fmt:message
                    key="templates/login/index.html/2001"/></a>
            <button class="imcms-button imcms-button--neutral imcms-info-body__button auth-provider-button"
                    id="default-login-button">Imcms Login
            </button>
        </div>
        <div id="login-providers" class="imcms-info-body__providers">
            <div id="default-login-provider" class="login-provider--active">
                <c:if test="${requestScope['error'] ne null}">
                    <div class="imcms-field">
                        <div class="imcms-error-msg imcms-login__error-msg">${requestScope['error'].toLocalizedString(pageContext.request)}</div>
                    </div>
                </c:if>
                <div class="imcms-field">
                    <div class="imcms-title">
                        <fmt:message key="templates/login/index.html/4"/>
                        <fmt:message key="templates/login/index.html/1001"/>
                    </div>
                </div>
                <form action="${cp}/servlet/VerifyUser" id="loginForm" method="post">
                    <c:set var="nextMetaParamName" value="<%=VerifyUser.REQUEST_PARAMETER__NEXT_META%>"/>
                    <c:set var="nextMetaParamValue" value="${requestScope[nextMetaParamName]}"/>

                    <c:set var="nextUrlParamName" value="<%=VerifyUser.REQUEST_PARAMETER__NEXT_URL%>"/>
                    <c:set var="nextUrlParamValue" value="${requestScope[nextUrlParamName]}"/>

                    <c:if test="${nextMetaParamValue ne null}">
                        <input type="hidden" name="${nextMetaParamName}" value="${fn:escapeXml(nextMetaParamValue)}">
                    </c:if>

                    <c:if test="${nextMetaParamValue eq null and nextUrlParamValue ne null}">
                        <input type="hidden" name="${nextUrlParamName}" value="${fn:escapeXml(nextUrlParamValue)}">
                    </c:if>
                    <div class="imcms-field">
                        <div class="imcms-text-box">
                            <label for="<fmt:message key="templates/login/index.html/5"/>"
                                   class="imcms-label imcms-text-box__label"><fmt:message
                                    key="templates/login/index.html/5"/></label>
                            <input id="<fmt:message key="templates/login/index.html/5"/>" type="text"
                                   name="<%=VerifyUser.REQUEST_PARAMETER__USERNAME%>"
                                   class="imcms-input imcms-text-box__input">
                        </div>
                    </div>
                    <div class="imcms-field">
                        <div class="imcms-text-box">
                            <label for="<fmt:message key="templates/login/index.html/6"/>"
                                   class="imcms-label imcms-text-box__label">
                                <fmt:message key="templates/login/index.html/6"/>
                            </label>
                            <input id="<fmt:message key="templates/login/index.html/6"/>" type="password"
                                   name="<%=VerifyUser.REQUEST_PARAMETER__PASSWORD%>"
                                   class="imcms-input imcms-text-box__input">
                        </div>
                    </div>
                </form>
                <div class="imcms-field">
                    <a class="imcms-button imcms-button--neutral imcms-info-body__button"
                       href="${cp}/servlet/PasswordReset"><fmt:message key="templates/login/index.html/2002"/></a>
                </div>
                <div class="imcms-info-footer imcms-login__footer">
                    <button type="submit" form="loginForm"
                            class="imcms-button imcms-button--positive imcms-info-footer__button"><fmt:message
                            key="templates/login/index.html/2005"/></button>
                    <button type="submit" form="loginForm" name="<%= VerifyUser.REQUEST_PARAMETER__EDIT_USER %>"
                            class="imcms-button imcms-button--save imcms-info-footer__button"><fmt:message
                            key="templates/login/index.html/2006"/></button>
                </div>
            </div>
        </div>
    </div>
</div>


</body>
</html>
