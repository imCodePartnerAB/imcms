<%@ page import="com.imcode.imcms.servlet.VerifyUser" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="cp" value="${pageContext.request.contextPath}"/>

<html>
<head>
    <title><fmt:message key="templates/login/index.html/1"/></title>

    <link rel="stylesheet" type="text/css" href="${cp}/imcms/css/imcms_admin.css.jsp">
    <script src="${cp}/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<ui:imcms_gui_outer_start/>

<c:set var="heading">
    <fmt:message key="templates/login/index.html/2"/>
</c:set>

<ui:imcms_gui_head heading="${heading}"/>

<table border="0" cellspacing="0" cellpadding="0" width="310">
    <form action="">
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><input type="button" class="imcmsFormBtn" style="width:100px"
                                   value="<fmt:message key="templates/login/index.html/2001"/>"
                                   onClick="top.location='${cp}/servlet/StartDoc';"></td>
                        <td>&nbsp;</td>
                        <td><input type="button" class="imcmsFormBtn" style="width:115px"
                                   value="<fmt:message key="templates/login/index.html/2002"/>"
                                   onClick="top.location='${cp}/servlet/PasswordReset';"></td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>
<table border="0" cellspacing="0" cellpadding="2" width="310">
    <tr>
        <td colspan="2" nowrap>
            <span class="imcmsAdmText">
            <c:if test="${requestScope['error'] ne null}">
                <p><b>${requestScope['error'].toLocalizedString(pageContext.request)}</b></p>
            </c:if>
            <fmt:message key="templates/login/index.html/4"/>
            <img alt="" src="${cp}/imcms/images/1x1.gif" width="1" height="5">
            <fmt:message key="templates/login/index.html/1001"/>
        </span>
        </td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
        <td colspan="2" align="center">
            <table border="0" cellspacing="0" cellpadding="1">
                <form action="${cp}/servlet/VerifyUser" method="post">
                    <c:set var="nextMetaParamName" value="<%=VerifyUser.REQUEST_PARAMETER__NEXT_META%>"/>
                    <c:set var="nextMetaParamValue" value="${requestScope[nextMetaParamName]}"/>

                    <c:set var="nextUrlParamName" value="<%=VerifyUser.REQUEST_PARAMETER__NEXT_URL%>"/>
                    <c:set var="nextUrlParamValue" value="${requestScope[nextUrlParamName]}"/>

                    <c:if test="${nextMetaParamValue ne null}">
                        <input type="hidden" name="${nextMetaParamName}" value="${fn:escapeXml(nextMetaParamValue)}">
                    </c:if>

                    <c:if test="${nextMetaParamValue eq null and nextUrlParamValue ne null}">
                        <input type="hidden" name="${nextUrlParamName}" value="${fn:escapeXml(nextUrlParamValue)}">
                    </c:if>

                    <tr>
                        <td><span class="imcmsAdmText"><fmt:message key="templates/login/index.html/5"/></span></td>
                        <td>&nbsp;</td>
                        <td><input type="text" name="<%=VerifyUser.REQUEST_PARAMETER__USERNAME%>" size="15"
                                   style="width:180px"></td>
                    </tr>
                    <tr>
                        <td><span class="imcmsAdmText"><fmt:message key="templates/login/index.html/6"/></span></td>
                        <td>&nbsp;</td>
                        <td><input type="password" name="<%=VerifyUser.REQUEST_PARAMETER__PASSWORD%>" size="15"
                                   style="width:180px">
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">&nbsp;</td>
                    </tr>
                    <tr>
                        <td colspan="2">&nbsp;</td>
                        <td>
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><input class="imcmsFormBtn" type="submit" style="width:80px"
                                               value="<fmt:message key="templates/login/index.html/2005"/>"></td>
                                    <td>&nbsp;</td>
                                    <td><input class="imcmsFormBtn" type="submit" style="width:80px"
                                               name="<%= VerifyUser.REQUEST_PARAMETER__EDIT_USER %>"
                                               value="<fmt:message key="templates/login/index.html/2006"/>"></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </form>
            </table>
        </td>
    </tr>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>
</body>
</html>
