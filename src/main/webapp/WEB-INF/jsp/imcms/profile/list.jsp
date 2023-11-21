<%@ page
        import="com.imcode.imcms.servlet.superadmin.AdminProfiles, imcode.server.document.Profile, org.apache.commons.text.StringEscapeUtils, java.util.List"
        contentType="text/html; charset=UTF-8" %>
<%
    List<Profile> profiles = (List<Profile>) request.getAttribute("profiles");
%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
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

<form action="<%= request.getContextPath() %>/imcms/admin/profile/list" method="POST">
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
    <c:set var="heading">
        <fmt:message key="profile/headline"/>
    </c:set>
    <ui:imcms_gui_heading heading="${heading}"/>
    <table border="0" cellspacing="0" cellpadding="2" width="100%" align="center">
        <%
            for (Profile profile : profiles) {
        %>
        <tr>
            <td><%= StringEscapeUtils.escapeHtml4(profile.getName()) %>
            </td>
            <td><%= StringEscapeUtils.escapeHtml4(profile.getDocumentName()) %>
            </td>
            <td align="right">
                <input type="submit" class="imcmsFormBtnSmall"
                       name="<%= AdminProfiles.Parameter.EDIT_PREFIX+profile.getId().toString() %>"
                       value="<fmt:message key="profile/edit"/>"/>
                <input type="submit" class="imcmsFormBtnSmall"
                       name="<%= AdminProfiles.Parameter.DELETE_PREFIX+profile.getId( ).toString() %>"
                       value="<fmt:message key="profile/delete"/>"/>
            </td>
        </tr>
        <%
            }
        %>
    </table>
    <ui:imcms_gui_hr wantedcolor="blue"/>
    <input type="submit" name="<%= AdminProfiles.Parameter.NEW_PROFILE %>" value="<fmt:message key="profile/create"/>"
           class="imcmsFormBtn"/>
</form>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>
</body>
</html>
