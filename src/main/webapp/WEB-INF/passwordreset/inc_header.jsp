<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.imcode.imcms.servlet.PasswordReset" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<fmt:setLocale value="${userLanguage}"/>
<fmt:setBundle basename="imcms" var="resource_property" scope="request"/>

<html>
<head>
	<meta charset="UTF-8">
	<title><fmt:message key="passwordreset.title" bundle="${resource_property}"/></title>
	<link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
	<link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms-imports_files.css">
	<link rel="stylesheet" type="text/css" href="${contextPath}/dist/modal_window/imcms-modal-admin.css">
	<script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>
</head>
<body>
<div class="imcms-modal-admin">
	<div class="imcms-modal-admin-head">
		<a href="https://imcode.com" class="imcms-login__logo"></a>
		<div class="imcms-title imcms-head__title"><fmt:message key="passwordreset.title" bundle="${resource_property}"/></div>
	</div>
	<div class="imcms-modal-admin-body">
			<% List<String> errors = (List<String>) request.getAttribute(PasswordReset.REQUEST_ATTR_VALIDATION_ERRORS);
            if (errors != null) { %>
		<div class="imcms-field">
			<div class="imcms-error-msg imcms-modal-admin__error-msg">
				<fmt:message key="passwordreset.title.validation_errors" bundle="${resource_property}"/>
			</div>
			<div class="imcms-error-msg imcms-modal-admin__error-msg"><%= errors.get(0) %>
			</div>
		</div>
			<% } %>
