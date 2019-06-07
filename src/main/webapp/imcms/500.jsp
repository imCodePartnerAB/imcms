<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <html>
    <head>
        <title><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/1"/></title>
        <link rel="stylesheet" type="text/css"
              href="${pageContext.servletContext.contextPath}/imcms/css/imcms_admin.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script>
            $( document ).ready(function() {
                $("#hide-show-btn").click(function () {
                    var $detailDiv = $('#detail-info');
                    $detailDiv.is(':visible') ? $detailDiv.hide() : $detailDiv.show();
                });
            });
        </script>
    </head>
    <body bgcolor="#FFFFFF">
    <ui:imcms_gui_outer_start/>
    <c:set var="heading">
        <fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/2"/>
    </c:set>
    <ui:imcms_gui_head heading="${heading}"/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table border="0" cellpadding="0" cellspacing="0">
                    <form action="${pageContext.servletContext.contextPath}">
                        <tr>
                            <td><input type="Submit" value="<fmt:message key="templates/Startpage"/>" class="imcmsFormBtn"></td>
                        </tr>
                    </form>
                </table>
            </td>
            <td>&nbsp;</td>
            <td>
                <table border="0" cellpadding="0" cellspacing="0">
                    <form action="${pageContext.servletContext.contextPath}/servlet/BackDoc">
                        <tr>
                            <td><input type="Submit" value="<fmt:message key="templates/Back"/>" class="imcmsFormBtn"></td>
                        </tr>
                    </form>
                </table>
            </td>
        </tr>
    </table>
    <ui:imcms_gui_mid/>
    <table border="0" cellspacing="0" cellpadding="2">
        <tr>
            <td align="left" class="imcmsAdmText">
                <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/3"/></p>
                <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/4"/></p>
            </td>
        </tr>
    </table>
    <h2><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/6"/>
        ${errorId}
    </h2>
    <button id="hide-show-btn" class="imcmsFormBtn">
        <fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/9"/>
    </button>
    <div id="detail-info" style="display: none;">
        <br/>
        <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/10"/></strong>
        <pre>${errorUrl}</pre>
        <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/11"/></strong>
        <pre>${message}</pre>
        <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/12"/></strong>
        <pre>${cause}</pre>
        <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/13"/></strong>
        <pre>${stackTrace}</pre>
    </div>
    <ui:imcms_gui_bottom/>
    <ui:imcms_gui_outer_end/>
    </body>
    </html>
