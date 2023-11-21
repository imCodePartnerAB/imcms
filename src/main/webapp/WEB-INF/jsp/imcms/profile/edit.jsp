<%@ page
        import="com.imcode.imcms.servlet.superadmin.AdminProfiles, imcode.server.document.Profile, org.apache.commons.text.StringEscapeUtils"
        contentType="text/html; charset=UTF-8" %>
<%
    Profile profile = (Profile) request.getAttribute("profile");
%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>
</head>
<body>
<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="profile/headline"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>

<form action="<%= request.getContextPath() %>/imcms/admin/profile/edit" method="POST">
    <table border="0" cellspacing="0" cellpadding="2" width="400" align="center">
        <tr>
            <td>
                <input type="submit" name="<%= AdminProfiles.Parameter.BACK %>" value="<fmt:message key="global/back"/>"
                       title="<fmt:message key="global/back"/>" class="imcmsFormBtn">
                <input type="button" value="<fmt:message key="global/help"/>" title="<fmt:message key="global/help"/>"
                       class="imcmsFormBtn" onClick="openHelpW('CategoryAdmin')">
            </td>
        </tr>
    </table>
    <ui:imcms_gui_mid/>
    <input type="hidden" name="<%= AdminProfiles.Parameter.PROFILE_ID %>" value="<%= profile.getId() %>"/>
    <table border="0" cellspacing="0" cellpadding="2" width="100%" align="center">
        <tr>
            <td><fmt:message key="profile/name"/></td>
            <td><input type="text" name="<%= AdminProfiles.Parameter.PROFILE_NAME %>"
                       value="<%= StringEscapeUtils.escapeHtml4(profile.getName() )%>"/></td>
        </tr>
        <tr>
            <td><fmt:message key="profile/document_name"/></td>
            <td><input type="text" name="<%= AdminProfiles.Parameter.PROFILE_DOCUMENT_NAME %>"
                       value="<%= StringEscapeUtils.escapeHtml4(profile.getDocumentName() )%>"/></td>
        </tr>
    </table>
    <input type="submit" value="<fmt:message key="profile/save"/>" class="imcmsFormBtn"/>
</form>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>
</body>
</html>
