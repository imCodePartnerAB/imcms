<%@ page pageEncoding="UTF-8" %>
<%--
    Page for editing one specialized text/image/menu, without whole document content.
    Created by Serhii from Ubrainians for Imcode
    on 26.08.16.
--%>
<%@taglib prefix="imcms" uri="imcms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<imcms:variables/>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<c:set var="textEdit" value="${textNo ne null}"/>
<c:set var="imageEdit" value="${imageNo ne null}"/>
<c:set var="menuEdit" value="${menuNo ne null}"/>
<c:set var="noEdit" value="${textNo eq null and imageNo eq null and menuNo eq null}"/>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>ImCMS single edit mode</title>
    <meta charset="utf-8"/>
    <jsp:include page="/WEB-INF/templates/${user.language.isoCode639_2}/admin/textdoc/imcms_hard_jquery.html"/>
    <jsp:include page="/WEB-INF/jsp/imcms/imcms_admin_headtag.jsp">
        <jsp:param name="flags" value="${flags}"/>
    </jsp:include>
    <link rel="stylesheet" href="${contextPath}/imcms/css/template/demo.css"/>
    <script src="${contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_edit_element.js"></script>
</head>
<body>
<div class="container">
    <section class="content">
        <div class="wrapper">
            <c:if test="${textEdit}">
                <div id="textEdit" class="hidden">
                    <imcms:text no="${textNo}" document="${document.id}"/>
                </div>
                <script>Imcms.SingleEdit.Text.init()</script>
            </c:if>
            <c:if test="${imageEdit}">
                <div id="imageEdit" class="hidden">
                    <imcms:image no="${imageNo}" document="${document.id}"/>
                </div>
                <script>Imcms.SingleEdit.Image.init()</script>
            </c:if>
            <c:if test="${menuEdit}">
                <imcms:menu no='${menuNo}' docId="${document.id}"/>
                <script>Imcms.SingleEdit.Menu.init()</script>
            </c:if>
            <c:if test="${noEdit}">
                <h3><p>You should to set at least one parameter:</p>
                    <ul>
                        <li>textNo</li>
                        <li>imageNo</li>
                        <li>menuNo</li>
                    </ul>
                </h3>
            </c:if>
        </div>
    </section>
</div>
</body>
</html>
