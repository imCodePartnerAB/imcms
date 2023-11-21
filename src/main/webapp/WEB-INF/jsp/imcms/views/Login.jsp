${"<!--"}
<%@ page trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ page import="com.imcode.imcms.servlet.VerifyUser" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="cp" value="${pageContext.request.contextPath}"/>

<fmt:setLocale value="${userLanguage}"/>
<fmt:setBundle basename="imcms" var="resource_property"/>

<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="templates/login/index.html/1" bundle="${resource_property}"/></title>
    <link rel="stylesheet" href="${cp}/dist/imcms_admin.css">
    <link rel="stylesheet" href="${cp}/dist/imcms-imports_files.css">
    <link rel="stylesheet" href="${cp}/dist/imcms-login-page.css">
    <script>
        <jsp:include page="/imcms/js/imcms_config.js.jsp"/>
    </script>
    <script src="${cp}/dist/imcms_login_start.js"></script>
</head>
<body>
<div class="imcms-info-page">
    <div class="imcms-info-head imcms-info-head__login">
        <a href="https://www.imcms.net/" class="imcms-info__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="templates/login/index.html/2"
                                                                bundle="${resource_property}"/></div>
    </div>
    <div class="imcms-info-body imcms-info-body__login">
        <div id="login-page-buttons" class="imcms-field imcms-info-body__login-buttons">
	        <div class="imcms-start-page--link">
		        <a href="${cp}/" class="imcms-button imcms-button--neutral imcms-info-body__button"><fmt:message
				        key="templates/login/index.html/2001" bundle="${resource_property}"/></a>
	        </div>
        </div>
        <div id="login-providers" class="imcms-info-body__providers">
            <div id="default-login-provider" class="login-provider login-provider--active">
                <c:if test="${requestScope['error'] ne null}">
                    <div class="imcms-field" id="imcms-login-errors">
                        <div class="imcms-error-msg imcms-login__error-msg"
                             data-remaining-time="${requestScope[VerifyUser.REQUEST_ATTRIBUTE__WAIT_TIME]}">${requestScope['error'].toLocalizedStringByIso639_1(userLanguage)}</div>
                    </div>
                </c:if>
                <div class="imcms-field info-block">
                    <div class="imcms-title">
                        <fmt:message key="templates/login/index.html/4" bundle="${resource_property}"/>
                        <fmt:message key="templates/login/index.html/1001" bundle="${resource_property}"/>
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
                            <label for="username"
                                   class="imcms-label imcms-text-box__label">
                                <fmt:message key="templates/login/index.html/5" bundle="${resource_property}"/>
                            </label>
                            <input id="username" type="text"
                                   name="<%=VerifyUser.REQUEST_PARAMETER__USERNAME%>"
                                   maxlength="250"
                                   class="imcms-input imcms-text-box__input">
                        </div>
                    </div>
                    <div class="imcms-field">
                        <div class="imcms-text-box">
                            <label for="password"
                                   class="imcms-label imcms-text-box__label">
                                <fmt:message key="templates/login/index.html/6" bundle="${resource_property}"/>
                            </label>
                            <input id="password" type="password"
                                   name="<%=VerifyUser.REQUEST_PARAMETER__PASSWORD%>"
                                   maxlength="250"
                                   class="imcms-input imcms-text-box__input">
                        </div>
                    </div>
                </form>
                <div class="imcms-field">
                    <a class="imcms-button imcms-button--neutral imcms-info-body__button"
                       href="${cp}/servlet/PasswordReset"><fmt:message
                            key="templates/login/index.html/2002" bundle="${resource_property}"/></a>
                </div>
                <div class="imcms-info-footer imcms-login__footer">
                    <button type="submit" form="loginForm"
                            class="imcms-button imcms-button--positive imcms-info-footer__button">
                        <fmt:message key="templates/login/index.html/2005" bundle="${resource_property}"/></button>
                    <button type="submit" form="loginForm" name="<%= VerifyUser.REQUEST_PARAMETER__EDIT_USER %>"
                            class="imcms-button imcms-button--save imcms-info-footer__button">
                        <fmt:message key="templates/login/index.html/2006" bundle="${resource_property}"/></button>
                </div>
            </div>
        </div>
    </div>
</div>


</body>
</html>
