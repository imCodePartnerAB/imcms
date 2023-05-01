<%@ page import="imcode.util.Utility" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<fmt:setLocale value="<%=Utility.getUserLanguageFromCookie(request.getCookies()).getCode()%>"/>
<fmt:setBundle basename="imcms" var="resource_property"/>

<html>
    <head>
        <title><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/1" bundle="${resource_property}"/></title>
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
        <fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/2" bundle="${resource_property}"/>
    </c:set>
    <ui:imcms_gui_head heading="${heading}"/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table border="0" cellpadding="0" cellspacing="0">
                    <form action="${pageContext.servletContext.contextPath}/">
                        <tr>
                            <td><input type="Submit" value="<fmt:message key="templates/Startpage" bundle="${resource_property}"/>" class="imcmsFormBtn"></td>
                        </tr>
                    </form>
                </table>
            </td>
            <td>&nbsp;</td>
            <td>
                <table border="0" cellpadding="0" cellspacing="0">
                    <div>
                        <tr>
	                        <td>
		                        <button class="imcmsFormBtn" id="backBtn" style="display: none;">
			                        <fmt:message key="templates/Back" bundle="${resource_property}"/>
		                        </button>
	                        </td>
                        </tr>
                    </div>
                </table>
            </td>
        </tr>
    </table>
    <ui:imcms_gui_mid/>
    <table border="0" cellspacing="0" cellpadding="2">
        <tr>
            <td align="left" class="imcmsAdmText">
                <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/3" bundle="${resource_property}"/></p>
                <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/4" bundle="${resource_property}"/></p>
            </td>
        </tr>
    </table>
    <h2><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/6" bundle="${resource_property}"/>
        ${errorId}
    </h2>
    <button id="hide-show-btn" class="imcmsFormBtn">
        <fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/9" bundle="${resource_property}"/>
    </button>
    <div id="detail-info" style="display: none;">
        <br/>
        <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/10" bundle="${resource_property}"/></strong>
        <pre>${errorUrl}</pre>
        <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/11" bundle="${resource_property}"/></strong>
        <pre>${message}</pre>
        <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/12" bundle="${resource_property}"/></strong>
        <pre>${cause}</pre>
        <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/13" bundle="${resource_property}"/></strong>
        <pre>${stackTrace}</pre>
    </div>
    <ui:imcms_gui_bottom/>
    <ui:imcms_gui_outer_end/>
    <script>
	    window.onload = function () {
		    const history = window.history;
		    const backBtn = document.getElementById('backBtn');
		    //need to check whether is first page in history is ours
		    if (history.length > 2)
			    backBtn.style.display = "inline";
		    backBtn.addEventListener('click', () => history.back());
	    }
    </script>
    </body>
    </html>
