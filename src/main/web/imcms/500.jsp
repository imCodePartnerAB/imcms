<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
    <html>
    <head>
        <title><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/1"/></title>
        <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/imcms/css/imcms_admin.css.jsp">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script>
            #[[
            $( document ).ready(function() {
                $("#hide-show-btn").click(function () {
                    var $detailDiv = $('#detail-info');
                    $detailDiv.is(':visible') ? $detailDiv.hide() : $detailDiv.show();
                });
            });
            ]]#
        </script>
    </head>
    <body bgcolor="#FFFFFF">
    #gui_outer_start()
    #gui_head( "<fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/2"/>" )
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table border="0" cellpadding="0" cellspacing="0">
                    <form action="${pageContext.servletContext.contextPath}/servlet/StartDoc">
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
    #gui_mid()
    <table border="0" cellspacing="0" cellpadding="2">
        <tr>
            <td align="left" class="imcmsAdmText">
                <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/3"/></p>
                <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/4"/></p>
            </td>
        </tr>
    </table>
    #[[
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
    ]]#
    #gui_bottom()
    #gui_outer_end()
    </body>
    </html>
</vel:velocity>
