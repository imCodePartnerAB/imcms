<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.imcode.imcms.servlet.PasswordReset" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<fmt:setLocale value="${userLanguage}"/>
<fmt:setBundle basename="imcms" var="resource_property"/>

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
		<div class="imcms-title imcms-head__title"><fmt:message key="passwordreset.title"
		                                                        bundle="${resource_property}"/></div>
	</div>
	<div class="imcms-modal-admin-body">
			<% LocalizedMessage error = (LocalizedMessage) request.getAttribute(PasswordReset.REQUEST_ATTR_VALIDATION_ERRORS);
            if (error != null) { %>
		<div class="imcms-field">
			<div class="imcms-error-msg imcms-modal-admin__error-msg">
				<fmt:message key="passwordreset.title.validation_errors" bundle="${resource_property}"/>
			</div>
			<div class="imcms-error-msg imcms-modal-admin__error-msg"><%= error.toLocalizedStringByIso639_1((String) request.getAttribute("userLanguage")) %>
			</div>
		</div>
			<% } %>

		<div class="imcms-field">
			<div class="imcms-title" style="word-break: break-word">
				<fmt:message key="passwordreset.password_reset_form_info" bundle="${resource_property}"/>
			</div>

			<form method="POST" action="<%= request.getContextPath() %>/servlet/PasswordReset">
				<input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_OP%>"
				       value="<%=PasswordReset.Op.SAVE_NEW_PASSWORD%>"/>
				<input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_RESET_ID%>"
				       value="<%=request.getParameter(PasswordReset.REQUEST_PARAM_RESET_ID)%>"/>
				<div class="imcms-field">
					<label class="imcms-label imcms-text-box__label"
					       for="<%=PasswordReset.REQUEST_PARAM_PASSWORD%>">
						<fmt:message key="passwordreset.password_reset_form.lbl_password"
						             bundle="${resource_property}"/>
					</label>
					<input type="password"
					       name="<%=PasswordReset.REQUEST_PARAM_PASSWORD%>"
					       maxlength="250"
					       class="imcms-input imcms-text-box__input">
				</div>
				<div class="imcms-field">
					<label class="imcms-label imcms-text-box__label"
					       for="<%=PasswordReset.REQUEST_PARAM_PASSWORD_CHECK%>">
						<fmt:message key="passwordreset.password_reset_form.lbl_password_check"
						             bundle="${resource_property}"/>
					</label>
					<input type="password"
					       name="<%=PasswordReset.REQUEST_PARAM_PASSWORD_CHECK%>"
					       maxlength="250"
					       class="imcms-input imcms-text-box__input">
				</div>
				<div class="imcms-modal-admin-footer">
					<button type="submit"
					        class="imcms-button imcms-button--positive imcms-modal-admin-footer__button">
						<fmt:message key="passwordreset.password_reset_form.submit" bundle="${resource_property}"/>
					</button>
				</div>
			</form>

			<%--<p>--%>
			<%--Secure password tips:--%>
			<%--<ul>--%>
			<%--<li>Use at least 8 characters, a combination of numbers and letters is best.</li>--%>
			<%--<li>Do not use the same password you have used with us previously.</li>--%>
			<%--<li>Do not use dictionary words, your name, e-mail address, or other personal information that can be easily obtained.</li>--%>
			<%--<li>Do not use the same password for multiple online accounts.</li>--%>
			<%--</ul>--%>
		</div>

<jsp:include page="inc_footer.jsp" flush="true"/>
