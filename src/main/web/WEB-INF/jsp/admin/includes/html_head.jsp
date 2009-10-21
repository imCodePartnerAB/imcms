<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title><spring:message code="${pageTitle}" htmlEscape="true"/></title>

    <link rel="stylesheet" href="${contextPath}/imcms/css/imcms_admin.css.jsp" type="text/css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/imcms/jscalendar/skins/aqua/theme.css.jsp" />

    <script type="text/javascript" src="${contextPath}/js/jquery-1.3.2.js"></script>
    <script type="text/javascript" src="${contextPath}/imcms/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="${contextPath}/imcms/jscalendar/lang/calendar-${locale.ISO3Language}.js"></script>
    <script type="text/javascript" src="${contextPath}/imcms/jscalendar/calendar-setup.js"></script>
    <script type="text/javascript" src="${contextPath}/js/imcms.js"></script>
    <script type="text/javascript">
        initAdmin();
    </script>
</head>