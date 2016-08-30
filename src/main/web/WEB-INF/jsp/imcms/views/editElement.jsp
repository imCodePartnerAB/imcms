<%@ page pageEncoding="UTF-8" %>
<%--
    Created by Serhii from Ubrainians for Imcode
    on 26.08.16.
--%>
<%@taglib prefix="imcms" uri="imcms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <base href="${contextPath}">
    <title>Powered by imCMS from imCode Partner AB</title>
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="${contextPath}/imcms/css/template/demo.css"/>
    <script src="${contextPath}/js/js.cookie.js"></script>
    <script src="${contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_linker.js"></script>
    <script src="${contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_edit_element.js"></script>
</head>
<body>
<div class="container">
    <section class="content">
        <div class="wrapper">
            <c:if test="${metaId ne null}">
                <h3>Attr `meta_id` is ${metaId}</h3>
            </c:if>
            <c:choose>
                <c:when test="${textNo ne null}">
                    <h3>Attr `textNo` is ${textNo}</h3>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${imageNo ne null}">
                            <h3>Attr `imageNo` is ${imageNo}</h3>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${menuNo ne null}">
                                <h3>Attr `menuNo` is ${menuNo}</h3>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</div>
</body>
</html>
