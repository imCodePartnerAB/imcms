<%@ page

	import="org.apache.commons.lang3.StringUtils"

	pageEncoding="UTF-8"

%><%

final String host = StringUtils.defaultString(request.getHeader("Host"));
final String siteUrl = StringUtils.isNotBlank(host) ? host + request.getContextPath() : "";

%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="imcode.util.Utility" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<fmt:setLocale value="<%=Utility.getUserLanguageFromCookie(request.getCookies()).getCode()%>"/>
<fmt:setBundle basename="imcms" var="resource_property"/>

<html>
<head>
	<title><fmt:message key="service_unavailable_error_page/headline" bundle="${resource_property}"/></title>

	<link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
	<link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms-imports_files.css">
	<link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/404/imcms-page-error.css">
</head>
<body bgcolor="#FFFFFF">
<div class="imcms-page-error">
	<div class="imcms-page-error-head">
		<a href="http://www.imcms.net/" class="imcms-login__logo"></a>
		<div class="imcms-title imcms-head__title"><fmt:message key="service_unavailable_error_page/headline" bundle="${resource_property}"/></div>
	</div>
	<div class="imcms-page-error-body">
		<div class="imcms-field" style="margin: 10% 0">
			<div class="imcms-title">
				<fmt:message key="service_unavailable_error_page/body" bundle="${resource_property}">
					<fmt:param value="<%=siteUrl%>"/>
				</fmt:message>
			</div>
		</div>
	</div>
</div>
</body>
</html>
