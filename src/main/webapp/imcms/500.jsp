<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="imcode.util.Utility" %>
<%@ page import="imcode.server.Imcms" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<fmt:setLocale value="<%=Utility.getUserLanguageFromCookie(request.getCookies()).getCode()%>"/>
<fmt:setBundle basename="imcms" var="resource_property"/>

<html>
<head>
    <title><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/1" bundle="${resource_property}"/></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/404/imcms-page-error.css">

    <link rel="stylesheet" type="text/css"
          href="${pageContext.servletContext.contextPath}/imcms/css/imcms_admin.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
</head>
<body bgcolor="#FFFFFF">
<div class="imcms-page-error">
    <div class="imcms-page-error-head">
        <a href="http://www.imcms.net/" class="imcms-login__logo"></a>
        <div class="imcms-title imcms-head__title">
            <fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/2" bundle="${resource_property}"/>
        </div>
    </div>
    <div class="imcms-page-error-body">
        <div class="imcms-field">
            <form class="imcms-page-error-body__form" action="${contextPath}">
                <a type="Submit" class="imcms-button imcms-button--neutral imcms-page-error-body__button" href="${contextPath}/">
                    <fmt:message key="templates/Startpage" bundle="${resource_property}"/>
                </a>
            </form>
            <div class="imcms-page-error-body__form">
                <button type="Submit" class="imcms-button imcms-button--neutral imcms-page-error-body__button"
                        id="backBtn" style="display: none;">
                    <fmt:message key="templates/Back" bundle="${resource_property}"/>
                </button>
            </div>
        </div>
        <div class="imcms-field">
            <fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/3" bundle="${resource_property}"/>
            <br>
            <fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/4" bundle="${resource_property}">
                <fmt:param value="<%=Imcms.getServices().getSystemData().getWebMaster()%>"/>
                <fmt:param value="<%=Imcms.getServices().getSystemData().getWebMasterAddress()%>"/>
            </fmt:message>
            <h3><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/6" bundle="${resource_property}"/>
                ${errorId}
            </h3>
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {
        $("#hide-show-btn").click(function () {
            var $detailDiv = $('#detail-info');
            $detailDiv.is(':visible') ? $detailDiv.hide() : $detailDiv.show();
        });

        window.onload = function () {
            const history = window.history;
            const backBtn = document.getElementById('backBtn');
            //need to check whether is first page in history is ours
            if (history.length > 2)
                backBtn.style.display = "inline";
            backBtn.addEventListener('click', () => history.back());
        }
    });
</script>
</body>
</html>
