${"<!--"}
<%@ page trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ page import="com.imcode.imcms.servlet.VerifyUser" %>
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
		<c:if test="${requestScope['error'] ne null}">
			<div class="imcms-field" id="imcms-login-errors">
				<div class="imcms-error-msg imcms-login__error-msg">${requestScope['error'].toLocalizedStringByIso639_1(userLanguage)}</div>
			</div>
		</c:if>
		<div class="imcms-field info-block">
			<div class="imcms-title">
				<fmt:message key="templates/login/2fa/greeting" bundle="${resource_property}"/>
			</div>
		</div>
		<form action="${cp}/api/mfa/second-factor" method="POST" id="2FAForm">
			<div class="imcms-field">
				<div class="imcms-text-box">
					<label for="<%=VerifyUser.REQUEST_PARAMETER__OTP%>"
					       class="imcms-label imcms-text-box__label">
						<fmt:message key="templates/login/2fa/input-label" bundle="${resource_property}"/>
					</label>
					<input id="<%=VerifyUser.REQUEST_PARAMETER__OTP%>" type="text"
					       name="<%=VerifyUser.REQUEST_PARAMETER__OTP%>"
					       maxlength="250"
					       class="imcms-input imcms-text-box__input"
					       autofocus
					       autocomplete="off">
				</div>
				<div class="imcms-info-footer imcms-login__footer">
					<button type="submit" form="2FAForm"
					        class="imcms-button imcms-button--positive imcms-info-footer__button">
						<fmt:message key="templates/login/2fa/button-label" bundle="${resource_property}"/></button>
					<button type="submit" form="2FAForm" name="<%= VerifyUser.REQUEST_PARAMETER__EDIT_USER %>"
					        class="imcms-button imcms-button--save imcms-info-footer__button">
						<fmt:message key="templates/login/index.html/2006" bundle="${resource_property}"/></button>
				</div>
			</div>
		</form>
	</div>
</div>
</body>
</html>
