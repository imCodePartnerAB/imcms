<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="vel" uri="imcmsvelocity" %>
<%@ taglib prefix="im" uri="imcms" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<vel:velocity>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><spring:message code="admin/search/title" htmlEscape="true"/></title>

    <link rel="stylesheet" href="${contextPath}/imcms/css/imcms_admin.css.jsp" type="text/css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/imcms/jscalendar/skins/aqua/theme.css.jsp" />
    
    <script type="text/javascript" src="${contextPath}/js/jquery-1.3.2.js"></script>
    <script type="text/javascript" src="${contextPath}/imcms/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="${contextPath}/imcms/jscalendar/lang/calendar-${locale.ISO3Language}.js"></script>
    <script type="text/javascript" src="${contextPath}/imcms/jscalendar/calendar-setup.js"></script>
    <script type="text/javascript" src="${contextPath}/js/imcms.js"></script>
    <script type="text/javascript">
        initSearch();
    </script>
</head>
<body bgcolor="#ffffff">
    <form style="display:none;">
        <input type="hidden" id="contextPath" value="${fn:escapeXml(contextPath)}"/>
    </form>
    <div id="container">
        #gui_outer_start()
        #gui_head( "<spring:message code="admin/search/title" htmlEscape="true"/>" )
        #gui_mid()

        <%@ include file="/WEB-INF/jsp/admin/search/search_form.jsp" %>

        #gui_bottom()
        #gui_outer_end()
        <div>&nbsp;</div>
    </div>
</body>
</html>
</vel:velocity>
