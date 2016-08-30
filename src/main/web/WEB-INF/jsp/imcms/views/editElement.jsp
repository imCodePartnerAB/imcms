<%@ page pageEncoding="UTF-8" %>
<%--
    Created by Serhii from Ubrainians for Imcode
    on 26.08.16.
--%>
<%@taglib prefix="imcms" uri="imcms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <base href="${pageContext.request.contextPath}">
    <title>Powered by imCMS from imCode Partner AB</title>
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/imcms/css/template/demo.css"/>
    <script src="${pageContext.servletContext.contextPath}/js/editElement.js"></script>
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
