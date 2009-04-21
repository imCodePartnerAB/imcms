<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title>${title}</title>
    <meta http-equiv="imagetoolbar" content="no" />
    <link rel="shortcut icon" href="${contextPath}/images/favicon.ico">
    <link href="${contextPath}/css/image_archive.css" rel="stylesheet" type="text/css" />
    
    ${css}
    <script type="text/javascript" src="${contextPath}/js/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="${contextPath}/js/image_archive.js"></script>
    ${javascript}
</head>
<body>
<form action="/" style="display:none;">
    <input type="hidden" id="contextPath" value="${contextPath}"/>
    <c:url var="dummyUrl" value="/"/>
    <input type="hidden" id="jsessionid" value="${dummyUrl}" />
</form>
